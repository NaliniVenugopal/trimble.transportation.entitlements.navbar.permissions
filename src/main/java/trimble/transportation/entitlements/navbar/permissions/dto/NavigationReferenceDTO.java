package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;

import java.util.List;

@Data
public class NavigationReferenceDTO {
    private String matchingIdentifier;
    private String matcher;
    private List<NavBarListDto> applicationList;

}
