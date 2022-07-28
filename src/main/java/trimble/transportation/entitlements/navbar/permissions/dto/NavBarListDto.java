package trimble.transportation.entitlements.navbar.permissions.dto;

import java.util.List;

import lombok.Data;


@Data
public class NavBarListDto {
    private String id;
    private List<String> permissionValues;
}