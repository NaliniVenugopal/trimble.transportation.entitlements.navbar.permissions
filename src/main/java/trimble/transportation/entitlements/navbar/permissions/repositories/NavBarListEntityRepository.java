package trimble.transportation.entitlements.navbar.permissions.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarListEntity;

@Repository
public interface NavBarListEntityRepository extends MongoRepository<NavBarListEntity, ObjectId> {
}
