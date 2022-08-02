package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document(collection = "permissions_mdm")
public class NavBarListEntity {
    private String permissionId;
    private List<String> permissionValues;
}