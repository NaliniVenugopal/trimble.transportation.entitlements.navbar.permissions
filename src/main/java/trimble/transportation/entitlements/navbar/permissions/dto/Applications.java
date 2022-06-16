package trimble.transportation.entitlements.navbar.permissions.dto;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Applications {
    private String parent;
    private LinkedHashSet<String> children;
}
