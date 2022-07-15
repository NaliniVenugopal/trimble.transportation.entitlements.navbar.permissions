package trimble.transportation.entitlements.navbar.permissions.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDBConfig {

    private static final int MONGO_SOCKET_TIMEOUT = 5000;
    private static final int MONGO_CONNECT_TIMEOUT = 30000;
    private static final int MONGO_MAX_WAIT_TIME = 30000;
    private static final int MONGO_SERVER_SELECTION_TIMEOUT = 10000;

    @Value("${spring.data.mongodb.uri:}")
    private String mongoURI;

    @Value("${spring.data.mongodb.dbname:}")
    private String mongoDBName;

    public String getConnectionUri() {
        return mongoURI;
    }

    public String getDatabaseName() {
        return mongoDBName;
    }

    @Bean()
    public MongoClient mongoDbClient() {
        if (StringUtils.isEmpty(getConnectionUri()))
            return null;

        String options = String.format("?uuidRepresentation=STANDARD&connectTimeoutMS=%d&socketTimeoutMS=%d&waitQueueTimeoutMS=%d&serverSelectionTimeoutMS=%d&retryWrites=%s&retryReads=%s&ssl=%s",
                MONGO_CONNECT_TIMEOUT, MONGO_SOCKET_TIMEOUT, MONGO_MAX_WAIT_TIME, MONGO_SERVER_SELECTION_TIMEOUT, "true", "true", isSSL());
        return MongoClients.create(getConnectionUri() + "/" + getDatabaseName() + options);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbClient(), mongoDBName);
    }

    private String isSSL() {
        if (StringUtils.containsIgnoreCase(getConnectionUri(), "localhost")) {
            return "false";
        } else {
            return "true";
        }
    }
}