package trimble.transportation.entitlements.navbar.permissions.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermissionEntity;

import java.util.UUID;

@Repository
public interface NavbarEntityRepository extends MongoRepository<NavBarPermissionEntity, UUID>, NavbarEntityCustomRepository {
}

