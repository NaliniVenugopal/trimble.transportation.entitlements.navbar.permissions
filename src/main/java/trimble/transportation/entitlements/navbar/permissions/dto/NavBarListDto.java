package trimble.transportation.entitlements.navbar.permissions.dto;

import java.util.List;

import lombok.Data;


@Data
public class NavBarListDto {
    private String permissionId;
    private List<String> permissionValues;
}