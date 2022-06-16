package trimble.transportation.entitlements.navbar.permissions.repositories.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermissionEntity;
import trimble.transportation.entitlements.navbar.permissions.repositories.NavbarEntityCustomRepository;

@Repository
@RequiredArgsConstructor
public class NavbarEntityCustomRepositoryImpl implements NavbarEntityCustomRepository {

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper;

    public NavBarPermissionEntity findByMatchingIdentifierAndMatcher(String matchingIdentifier, String matcher) {
        Query query = new Query().addCriteria(new Criteria().andOperator(Criteria.where("matchingIdentifier").is(matchingIdentifier),
                Criteria.where("matcher").is(matcher)));
        return mongoTemplate.findOne(query, NavBarPermissionEntity.class);
    }

}
