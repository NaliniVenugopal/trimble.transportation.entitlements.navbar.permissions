package trimble.transportation.entitlements.navbar.permissions.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionDto {

	private List<PermissionGrants> basic;
	
	//Ignore for now as we dont know the Class Type
	//private List<String> others;
}
