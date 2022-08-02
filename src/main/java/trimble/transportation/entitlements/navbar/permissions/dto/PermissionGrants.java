package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionGrants {

	private String permission_name;
	private String permission_granted;
}
