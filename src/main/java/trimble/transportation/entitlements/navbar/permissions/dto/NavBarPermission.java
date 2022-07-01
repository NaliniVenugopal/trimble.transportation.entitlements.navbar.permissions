package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;

import java.util.List;

@Data
public class NavBarPermission {
    private String matchingIdentifier;
    private String matcher;
    private String defaultURL;
    private List<Applications> applicationList;
}
