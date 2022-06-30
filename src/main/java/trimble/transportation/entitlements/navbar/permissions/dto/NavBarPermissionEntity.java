package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "nav_bar_permissions")
public class NavBarPermissionEntity {

    @Id
    private UUID id;
    private String matchingIdentifier;
    private String matcher;
    private String defaultURL;
    private List<Applications> applicationList;
}
