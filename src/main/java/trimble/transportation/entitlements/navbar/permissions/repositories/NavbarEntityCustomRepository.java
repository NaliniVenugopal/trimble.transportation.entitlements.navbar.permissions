package trimble.transportation.entitlements.navbar.permissions.repositories;

import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermission;

public interface NavbarEntityCustomRepository {

    NavBarPermission findByMatchingIdentifierAndMatcher(String matchingIdentifier, String matcher);
}
