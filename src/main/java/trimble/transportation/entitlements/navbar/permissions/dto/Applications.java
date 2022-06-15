package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;

import java.util.List;

@Data
public class Applications {
    private String parent;
    private List<String> children;
}
