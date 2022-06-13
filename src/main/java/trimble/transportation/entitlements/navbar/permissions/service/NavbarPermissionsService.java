package trimble.transportation.entitlements.navbar.permissions.service;

import trimble.transportation.entitlements.navbar.permissions.dto.NavigationReferenceDTO;


public interface NavbarPermissionsService {

    NavigationReferenceDTO constructNavigationMenu(String jwtToken);
}
