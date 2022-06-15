package trimble.transportation.entitlements.navbar.permissions.service;

import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermission;


public interface NavbarPermissionsService {

    NavBarPermission postNavigationBarValues(NavBarPermission navBarPermission);

    NavBarPermission constructNavigationMenu(String jwtToken);

    NavBarPermission getApplicationList(String matchingIdentifier, String matcher);
}