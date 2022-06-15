package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "NavBarPermissions")
public class NavBarPermissionEntity {

    private String matchingIdentifier;
    private String matcher;
    private List<Applications> applicationList;
}
