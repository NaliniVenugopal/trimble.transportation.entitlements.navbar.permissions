package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;

import java.util.List;


@Data
public class NavBarListDto {
    private String permissionId;
    private List<String> permissionValues;
}