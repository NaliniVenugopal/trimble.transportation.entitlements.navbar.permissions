package trimble.transportation.entitlements.navbar.permissions.repositories;

import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermissionEntity;

public interface NavbarEntityCustomRepository {

    NavBarPermissionEntity findByMatchingIdentifierAndMatcher(String matchingIdentifier, String matcher);
}
