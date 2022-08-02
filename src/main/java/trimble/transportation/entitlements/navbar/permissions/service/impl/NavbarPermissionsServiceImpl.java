package trimble.transportation.entitlements.navbar.permissions.service.impl;



import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import trimble.transportation.entitlements.navbar.permissions.constants.NavbarPermissionsConstants;
import trimble.transportation.entitlements.navbar.permissions.dto.Applications;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarListEntity;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermission;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermissionEntity;
import trimble.transportation.entitlements.navbar.permissions.dto.PermissionResponse;
import trimble.transportation.entitlements.navbar.permissions.dto.UserPermission;
import trimble.transportation.entitlements.navbar.permissions.dto.enums.MatchingIdentifier;
import trimble.transportation.entitlements.navbar.permissions.repositories.NavBarListEntityRepository;
import trimble.transportation.entitlements.navbar.permissions.repositories.NavbarEntityRepository;
import trimble.transportation.entitlements.navbar.permissions.service.NavbarPermissionsService;
import trimble.transportation.entitlements.navbar.permissions.utils.HttpService;
import trimble.transportation.entitlements.navbar.permissions.utils.NavbarPermissionUtils;
import trimble.transportation.entitlements.navbar.permissions.utils.exception.GeneralizedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NavbarPermissionsServiceImpl implements NavbarPermissionsService {

    private final HttpService httpService;

    private final NavbarEntityRepository navbarEntityRepository;

    private final ObjectMapper objectMapper;

    @Value("${external.accountServiceUrl}")
    private String accountServiceUrl;

    @Value("${external.accountServiceEndpoint}")
    private String accountServiceEndpoint;
    
    @Value("${external.userServiceUrl}")
    private String userServiceUrl;

    @Value("${external.userServiceEndpoint}")
    private String userServiceEndpoint;
    
    
    @Value("${external.permissionServiceUrl}")
    private String permissionServiceUrl;

    @Value("${external.permissionServiceEndpoint}")
    private String permissionServiceEndpoint;
    
    private final NavBarListEntityRepository navBarListEntityRepository;
    
    public NavBarPermission postNavigationBarValues(NavBarPermission navBarPermission) {
        var navBarEntity = navbarEntityRepository.findByMatchingIdentifierAndMatcher(navBarPermission.getMatchingIdentifier(), navBarPermission.getMatcher());
        if (!ObjectUtils.isEmpty(navBarEntity)) {
            navBarEntity.setApplicationList(navBarPermission.getApplicationList());
            var response = navbarEntityRepository.save(navBarEntity);
            return objectMapper.convertValue(response, NavBarPermission.class);
        } else {
            var navbar = objectMapper.convertValue(navBarPermission, NavBarPermissionEntity.class);
            navbar.setId(UUID.randomUUID());
            var response = navbarEntityRepository.save(navbar);
            return objectMapper.convertValue(response, NavBarPermission.class);
        }
    }

    public NavBarPermission updateNavigationBarValues(NavBarPermission navBarPermission) {
        var navBarEntity = navbarEntityRepository.findByMatchingIdentifierAndMatcher(navBarPermission.getMatchingIdentifier(), navBarPermission.getMatcher());
        if (navBarEntity == null)
            throw new GeneralizedException("Matcher and Matching Identifer cannot be found. Use POST to insert values ");
        var appListEntity = navBarEntity.getApplicationList();
        navBarPermission.getApplicationList().stream().forEach(application -> {
            Optional<Applications> isParentPresent = appListEntity.stream().filter(list -> list.getParent().equals(application.getParent())).findFirst();
            if (isParentPresent.isPresent()) {
                isParentPresent.get().getChildren().addAll(application.getChildren());
            } else {
                appListEntity.add(application);
            }
        });
        var navBarSavedEntity = navbarEntityRepository.save(navBarEntity);
        return objectMapper.convertValue(navBarSavedEntity, NavBarPermission.class);
    }

    @SneakyThrows
    public NavBarPermission constructNavigationMenu(String jwtToken, boolean filterByTTCPerms) {
        String[] tokenChunks = jwtToken.split("\\.");
        var decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(tokenChunks[1]));
        JSONObject jwtJson = new JSONObject(payload);
        var accountId = (String) jwtJson.get(NavbarPermissionsConstants.ACCOUNT_ID);
        var userId = (String) jwtJson.get(NavbarPermissionsConstants.USERID);
        var url = NavbarPermissionUtils.concatStrings(accountServiceUrl, accountServiceEndpoint, accountId);
        JSONObject accountJson = new JSONObject(httpService.getEntity(url, NavbarPermissionUtils.constructHeaders("Account",jwtToken,""), String.class).getResponseBody());
        var accountTypes = accountJson.get("accountTypes");
        var accountTypeList = new ObjectMapper().readValue(accountTypes.toString(), new TypeReference<List<String>>() {
        });
        return !filterByTTCPerms ? handleMultipleAccountTypes(accountTypeList) : constructNavBarByPermissions(accountTypeList,userId,jwtToken);
    }
    
    @SneakyThrows
    private NavBarPermission constructNavBarByPermissions(List<String> accountTypeList, String userId, String jwtToken) {
    	 NavBarPermission navBarPermission = handleMultipleAccountTypes(accountTypeList);
    	 List<PermissionResponse> permissionsRespTTC = getPermissionResponses(userId, jwtToken);
    	 var permissionList = navBarListEntityRepository.findAll();
    	 for (Iterator<Applications> iteratorParent = navBarPermission.getApplicationList().iterator(); iteratorParent.hasNext();) {
        	 Applications accountTypeNavBarLink = iteratorParent.next();
        	 boolean isParentNodeOnly = false;
        	 if(accountTypeNavBarLink.getChildren().isEmpty())
        		 isParentNodeOnly = true;
        	 if(!isParentNodeOnly) {
		        	 for (Iterator<String> iterator = accountTypeNavBarLink.getChildren().iterator(); iterator.hasNext();) {
		        		 	String child = iterator.next();
		        		 	var permissionValues = permissionList.stream().filter(mdm -> mdm.getPermissionId().equals(child)).findFirst();
		        		 	if(permissionValues.isPresent()) {
		        				List<String> ttcResourceNamesConfigured = permissionValues.get().getPermissionValues();
		        				boolean permissionPresent = permissionsRespTTC.stream().anyMatch(ttcAssignedPermissions -> 
		        														ttcResourceNamesConfigured.contains(ttcAssignedPermissions.getResource_name())
		        											);
		        				if(!permissionPresent) {
		        					//System.out.println(" PERMISSION NOT CONFIGURED FOR THE CHILD "+child);
		        					iterator.remove();
		        				}else {
		        					//Permission is available for child
		        				}
		        			}else {
		        				//System.out.println(" NO RESOURCE CONFIGURED FOR THE CHILD "+child);
		        				iterator.remove();
		        			}
		        	 }
		        	 if(accountTypeNavBarLink.getChildren().isEmpty())
		        		 iteratorParent.remove();
        	 }else {
	        		 String parentId = accountTypeNavBarLink.getParent();
	        		 var permissionValues = permissionList.stream().filter(mdm -> mdm.getPermissionId().equals(parentId)).findFirst();
	        		 if(permissionValues.isPresent()){
	     				List<String> ttcResourceNamesConfigured =  permissionValues.get().getPermissionValues();
	     				boolean permissionPresent = permissionsRespTTC.stream().anyMatch(ttcAssignedPermissions -> 
	     											ttcResourceNamesConfigured.contains(ttcAssignedPermissions.getResource_name()));
	     				if(!permissionPresent) {
	     					//log.info(" PERMISSION NOT CONFIGURED FOR THE PARENT "+parentId);
	     					iteratorParent.remove();
	     				}else {
	     					//Permission is available for Parent
	     				}
	     			}else {
	     				//log.info(" NO RESOURCE CONFIGURED FOR THE PARENT "+parentId);
	     				iteratorParent.remove();
	     			}
        	 }
         };
        NavbarPermissionUtils.validateDefaultUrl(navBarPermission); 
    	return navBarPermission;
    }
    
    private NavBarPermission handleMultipleAccountTypes(List<String> accountTypeList) {
        NavBarPermission initialList = null;
        NavBarPermission secondaryList = null;
        List<String> availablePermissions = new ArrayList<>();
        for (String singleAccountType : accountTypeList) {
            if (initialList == null) {
                initialList = getApplicationListWithMap(MatchingIdentifier.ACCOUNT_TYPE.getValue(), singleAccountType, availablePermissions);
            } else {
                NavBarPermission mergedList = initialList;
                secondaryList = getApplicationListWithMap(MatchingIdentifier.ACCOUNT_TYPE.getValue(), singleAccountType, availablePermissions);
                if (!ObjectUtils.isEmpty(initialList) && !CollectionUtils.isEmpty(initialList.getApplicationList()) && !ObjectUtils.isEmpty(secondaryList)) {
                    secondaryList.getMapApplication().forEach(
                            (key, value) ->
                                    mergedList.getMapApplication().merge(key, value, (v1, v2) -> {
                                                mergedList.getMapApplication().get(key).addAll(v2.stream().collect(Collectors.toList()));
                                                return mergedList.getMapApplication().get(key);
                                            }
                                    ));
                }
            }
        }
        if (initialList == null) {
            initialList = getApplicationList(MatchingIdentifier.DEFAULT.getValue(), NavbarPermissionsConstants.DEFAULT_PERMISSIONS);
            return initialList;
        }
        initialList.setMatchingIdentifier(MatchingIdentifier.ACCOUNT_TYPE.getValue());
        var accountType = !CollectionUtils.isEmpty(availablePermissions) ? availablePermissions.stream().filter(type -> StringUtils.isNotBlank(type)).collect(Collectors.joining(",")) : "";
        initialList.setMatcher(accountType);
        List<Applications> applicationList = initialList.getMapApplication().entrySet()
                .stream()
                .map(link -> new Applications(link.getKey(), link.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));

        initialList.setApplicationList(applicationList);
        return initialList;
    }

    public NavBarPermission getApplicationList(String matchingIdentifier, String matcher) {
        var navBarEntity = navbarEntityRepository.findByMatchingIdentifierAndMatcher(matchingIdentifier, matcher);
        if (!ObjectUtils.isEmpty(navBarEntity)) {
            return objectMapper.convertValue(navBarEntity, NavBarPermission.class);
        } else {
            throw new GeneralizedException("No records available");
        }
    }

    public NavBarPermission getApplicationListWithMap(String matchingIdentifier, String matcher, List<String> availablePermissions) {
        var navBarEntity = navbarEntityRepository.findByMatchingIdentifierAndMatcher(matchingIdentifier, matcher);
        if (!ObjectUtils.isEmpty(navBarEntity)) {
            var navBarPermission = objectMapper.convertValue(navBarEntity, NavBarPermission.class);
            Map<String, LinkedHashSet<String>> convertedMap = navBarPermission.getApplicationList().stream().
                    collect(Collectors.toMap(Applications::getParent, Applications::getChildren,
                            (parent, children) -> parent, LinkedHashMap::new));
            navBarPermission.setMapApplication(convertedMap);
            availablePermissions.add(matcher);
            return navBarPermission;
        } else {
            return null;
        }
    }

    public void deletePermission(String matchingIdentifier, String matcher) {
        navbarEntityRepository.deleteByMatchingIdentifierAndMatcher(matchingIdentifier, matcher);
    }
    
    private List<PermissionResponse> getPermissionResponses(String userId, String jwtToken) throws JSONException, JsonProcessingException {
        var url = NavbarPermissionUtils.concatStrings(userServiceUrl, userServiceEndpoint, userId);
        JSONObject userJson = new JSONObject(httpService.getEntity(url, NavbarPermissionUtils.constructHeaders("Users", jwtToken, ""), String.class).getResponseBody());
        var accountObjectId = (String) userJson.get("accountObjectId");
        List<UserPermission> userPermission = new ObjectMapper().readValue(userJson.get("roles").toString(), new TypeReference<List<UserPermission>>() {
        });
        List<String> objectIds = userPermission.stream().map(x -> x.getObjectId()).collect(Collectors.toList());
        url = NavbarPermissionUtils.concatStrings(permissionServiceUrl, permissionServiceEndpoint, "?filter=true");
        List<PermissionResponse> permissionsRespTTC = httpService.postEntityList(url, NavbarPermissionUtils.constructHeaders("Permissions", jwtToken, accountObjectId), objectIds, PermissionResponse.class).getResponseBodyList();
        return permissionsRespTTC;
    }

}
