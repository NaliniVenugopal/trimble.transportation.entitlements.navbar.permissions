package trimble.transportation.entitlements.navbar.permissions.interceptor;

import org.springframework.stereotype.Component;
import trimble.transportation.entitlements.navbar.permissions.utils.interceptor.TenantInformation;

@Component
public class TenantContext {

    private final ThreadLocal<TenantInformation> context = new ThreadLocal<>();

    public void setTenantInformation(TenantInformation tenantInformation) {
        context.set(tenantInformation);
    }

    public TenantInformation getTenantInformation() {
        return context.get();
    }

    public void clear() {
        context.remove();
    }
}
