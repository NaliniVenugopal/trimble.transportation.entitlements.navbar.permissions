package trimble.transportation.entitlements.navbar.permissions.service;

import trimble.transportation.entitlements.navbar.permissions.dto.NavBarListEntity;
import trimble.transportation.entitlements.navbar.permissions.dto.NavigationReferenceDTO;


public interface NavbarPermissionsService {

    NavBarListEntity postNavigationBarValues(NavBarListEntity navBarListEntity);

    NavigationReferenceDTO constructNavigationMenu(String jwtToken);
}
