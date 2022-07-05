package trimble.transportation.entitlements.navbar.permissions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Data
public class NavBarPermission {
    private String matchingIdentifier;
    private String matcher;
    private String defaultURL;
    private List<Applications> applicationList;

    @JsonIgnore
    private Map<String, LinkedHashSet<String>> mapApplication;
}
