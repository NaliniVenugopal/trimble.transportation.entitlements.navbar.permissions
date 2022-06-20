package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "nav_bar_mdm")
public class HeirarchyEntity {

    private List<Heirarchy> heirarchyList;
}
