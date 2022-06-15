package trimble.transportation.entitlements.navbar.permissions.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermissionEntity;
import trimble.transportation.entitlements.navbar.permissions.dto.enums.AccountType;

@Repository
public interface NavbarEntityRepository extends MongoRepository<NavBarPermissionEntity, AccountType>, NavbarEntityCustomRepository {
}

