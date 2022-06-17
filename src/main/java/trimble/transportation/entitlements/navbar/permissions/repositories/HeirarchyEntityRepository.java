package trimble.transportation.entitlements.navbar.permissions.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import trimble.transportation.entitlements.navbar.permissions.dto.HeirarchyEntity;

@Repository
public interface HeirarchyEntityRepository extends MongoRepository<HeirarchyEntity, ObjectId> {
}

