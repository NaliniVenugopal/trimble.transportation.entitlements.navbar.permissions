package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import trimble.transportation.entitlements.navbar.permissions.dto.enums.AccountType;

import java.util.List;

@Data
@Document(collection = "NavBarPermissions")
public class NavBarListEntity {

    private AccountType accountType;
    private List<NavBarListDto> applicationList;
}
