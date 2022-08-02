package trimble.transportation.entitlements.navbar.permissions.dto;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "permissions_mdm")
public class NavBarListEntity {
	private String permissionId;
    private List<String> permissionValues;
}