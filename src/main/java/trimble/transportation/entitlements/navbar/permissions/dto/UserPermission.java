package trimble.transportation.entitlements.navbar.permissions.dto;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPermission {

	
	private UUID id;
	private String name;
	private String objectId;
}
