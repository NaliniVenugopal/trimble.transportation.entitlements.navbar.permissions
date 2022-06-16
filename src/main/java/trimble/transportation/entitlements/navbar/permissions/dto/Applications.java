package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;

import java.util.Set;

@Data
public class Applications {
    private String parent;
    private Set<String> children;
}
