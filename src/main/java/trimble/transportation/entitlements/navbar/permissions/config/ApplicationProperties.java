package trimble.transportation.entitlements.navbar.permissions.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarListDto;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "tms-shell-nav-values")
@Data
public class ApplicationProperties {


    List<NavBarListDto> brokerNavValues;
    List<NavBarListDto> shipperNavValues;
    List<NavBarListDto> contractNavValues;

}
