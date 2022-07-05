package trimble.transportation.entitlements.navbar.permissions.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import trimble.transportation.entitlements.navbar.permissions.config.ApplicationProperties;
import trimble.transportation.entitlements.navbar.permissions.constants.NavbarPermissionsConstants;
import trimble.transportation.entitlements.navbar.permissions.dto.Applications;
import trimble.transportation.entitlements.navbar.permissions.dto.Heirarchy;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermission;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermissionEntity;
import trimble.transportation.entitlements.navbar.permissions.dto.enums.MatchingIdentifier;
import trimble.transportation.entitlements.navbar.permissions.repositories.HeirarchyEntityRepository;
import trimble.transportation.entitlements.navbar.permissions.repositories.NavbarEntityRepository;
import trimble.transportation.entitlements.navbar.permissions.service.NavbarPermissionsService;
import trimble.transportation.entitlements.navbar.permissions.utils.HttpService;
import trimble.transportation.entitlements.navbar.permissions.utils.NavbarPermissionUtils;
import trimble.transportation.entitlements.navbar.permissions.utils.exception.GeneralizedException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavbarPermissionsServiceImpl implements NavbarPermissionsService {

    private final HttpService httpService;

    private final NavbarEntityRepository navbarEntityRepository;

    private final HeirarchyEntityRepository heirarchyEntityRepository;

    private final ApplicationProperties applicationProperties;

    private final ObjectMapper objectMapper;

    @Value("${external.accountServiceUrl}")
    private String accountServiceUrl;

    @Value("${external.accountServiceEndpoint}")
    private String accountServiceEndpoint;

    @Value("${external.authorization}")
    private String authorization;


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
    public NavBarPermission constructNavigationMenu(String jwtToken) {
        String[] tokenChunks = jwtToken.split("\\.");
        var decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(tokenChunks[1]));
        JSONObject jwtJson = new JSONObject(payload);
        var accountId = (String) jwtJson.get(NavbarPermissionsConstants.ACCOUNT_ID);
        Object roles = null;
        List<String> rolesList = null;
        //Check if roles is there as only for identity_user "USER" roles will be there. For ClientCredentials it will not be there
        if (jwtJson.has("roles")) {
            roles = jwtJson.get("roles");
            rolesList = new ObjectMapper().readValue(roles.toString(), new TypeReference<List<String>>() {
            });
        }
        Map<String, String> headers = new HashMap<>();
        var url = NavbarPermissionUtils.concatStrings(accountServiceUrl, accountServiceEndpoint, accountId);
        headers.put("x-credential-jwt", jwtToken);
        headers.put("Content-Type", "application/json");
        //Comment before commit
//        headers.put("Authorization", "Bearer " + authorization);
        JSONObject accountJson = new JSONObject(httpService.getEntity(url, headers, String.class).getResponseBody());
        var accountTypes = accountJson.get("accountTypes");
        var accountTypeList = new ObjectMapper().readValue(accountTypes.toString(), new TypeReference<List<String>>() {
        });
        return handleMultipleAccountTypes(accountTypeList);
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

    private boolean canProceedMultipleAccountCheck(Heirarchy hierarchy, List<String> accountTypeList) {
        for (String singleAccountType : hierarchy.getValue().split(",")) {
            if (!accountTypeList.contains(singleAccountType)) {
                return false;
            }
        }
        return true;
    }

}
