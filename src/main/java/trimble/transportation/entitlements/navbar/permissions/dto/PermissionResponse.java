package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionResponse {

	private String id;
	private String resource_id;
	private String resource_name;
	private String feature_name;
	private String upstream_url;
	private PermissionDto permissions;
}
