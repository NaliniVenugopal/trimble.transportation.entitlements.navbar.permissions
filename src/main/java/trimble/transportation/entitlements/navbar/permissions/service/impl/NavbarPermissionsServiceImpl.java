package trimble.transportation.entitlements.navbar.permissions.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import trimble.transportation.entitlements.navbar.permissions.config.ApplicationProperties;
import trimble.transportation.entitlements.navbar.permissions.constants.NavbarPermissionsConstants;
import trimble.transportation.entitlements.navbar.permissions.dto.NavigationReferenceDTO;
import trimble.transportation.entitlements.navbar.permissions.interceptor.TenantContext;
import trimble.transportation.entitlements.navbar.permissions.service.NavbarPermissionsService;
import trimble.transportation.entitlements.navbar.permissions.utils.HttpService;
import trimble.transportation.entitlements.navbar.permissions.utils.NavbarPermissionUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NavbarPermissionsServiceImpl implements NavbarPermissionsService {

    private final TenantContext tenantContext;

    private final HttpService httpService;

    private final ApplicationProperties applicationProperties;

    @Value("${external.accountServiceUrl}")
    private String accountServiceUrl;

    @Value("${external.accountServiceEndpoint}")
    private String accountServiceEndpoint;

    @Value("${external.authorization}")
    private String authorization;

    @SneakyThrows
    public NavigationReferenceDTO constructNavigationMenu(String jwtToken) {
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
        return construcRefDto(accountTypeList, rolesList);

    }

    private NavigationReferenceDTO construcRefDto(List<String> accountTypeList, List<String> rolesList) {
        NavigationReferenceDTO dto = new NavigationReferenceDTO();

        if (accountTypeList.contains(NavbarPermissionsConstants.BROKER)) {
            dto.setMatchingIdentifier(NavbarPermissionsConstants.ACCOUNT_TYPE);
            dto.setMatcher(NavbarPermissionsConstants.BROKER_TEXT);
            dto.setApplicationList(applicationProperties.getBrokerNavValues());
        } else if (rolesList != null && rolesList.contains(NavbarPermissionsConstants.SHIPPER_MANAGER)) {
            dto.setMatchingIdentifier(NavbarPermissionsConstants.ROLE_TYPE);
            dto.setMatcher(NavbarPermissionsConstants.SHIPPER_MANAGER);
            dto.setApplicationList(applicationProperties.getShipperNavValues());
        } else if (rolesList != null && rolesList.contains(NavbarPermissionsConstants.CONTRACT_MANAGER)) {
            dto.setMatchingIdentifier(NavbarPermissionsConstants.ROLE_TYPE);
            dto.setMatcher(NavbarPermissionsConstants.CONTRACT_MANAGER);
            dto.setApplicationList(applicationProperties.getContractNavValues());
        } else if (accountTypeList.contains(NavbarPermissionsConstants.SHIPPER)) {
            dto.setMatchingIdentifier(NavbarPermissionsConstants.ACCOUNT_TYPE);
            dto.setMatcher(NavbarPermissionsConstants.SHIPPER_TEXT);
            dto.setApplicationList(applicationProperties.getShipperNavValues());
        } else {
            dto.setMatchingIdentifier(NavbarPermissionsConstants.DEFAULT);
            dto.setMatcher(NavbarPermissionsConstants.DEFAULT_PERMISSIONS);
            dto.setApplicationList(applicationProperties.getShipperNavValues());
        }
        return dto;
    }
}
