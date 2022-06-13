package trimble.transportation.entitlements.navbar.permissions.utils.interceptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantInformation {
    private UUID accountId;
    private String tenantId;
    private String username;
    private String email;
    private String jwtToken;
    private String token;
}
