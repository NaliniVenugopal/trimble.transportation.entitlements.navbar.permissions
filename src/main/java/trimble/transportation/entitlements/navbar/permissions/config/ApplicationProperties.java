package trimble.transportation.entitlements.navbar.permissions.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import trimble.transportation.entitlements.navbar.permissions.dto.Applications;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "tms-shell-nav-values")
@Data
public class ApplicationProperties {


    List<Applications> brokerNavValues;
    List<Applications> shipperNavValues;
    List<Applications> contractNavValues;

}
