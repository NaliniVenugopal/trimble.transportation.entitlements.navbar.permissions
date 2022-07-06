package trimble.transportation.entitlements.navbar.permissions.dto;

import java.util.LinkedHashSet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Applications {
    private String parent;
    private LinkedHashSet<String> children;
}
