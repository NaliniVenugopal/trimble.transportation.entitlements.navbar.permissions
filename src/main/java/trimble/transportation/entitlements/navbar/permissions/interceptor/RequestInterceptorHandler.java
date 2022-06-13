package trimble.transportation.entitlements.navbar.permissions.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import trimble.transportation.entitlements.navbar.permissions.utils.exception.GeneralizedException;
import trimble.transportation.entitlements.navbar.permissions.utils.interceptor.TenantInformation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.UUID;

import static trimble.transportation.entitlements.navbar.permissions.constants.NavbarPermissionsConstants.*;


@Component
@RequiredArgsConstructor
public class RequestInterceptorHandler extends HandlerInterceptorAdapter {

    private final TenantContext tenantContext;


    @Override
    @SneakyThrows
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(X_CREDENTIAL_ACC_ID);
        String username = request.getHeader(X_CREDENTIAL_USERNAME);
        String jwtToken = request.getHeader(X_CREDENTIAL_JWT);
        TenantInformation tenantInformation = new TenantInformation();
        boolean processFlag = false;
        if (StringUtils.isNotBlank(jwtToken)) {
            String[] tokenChunks = jwtToken.split("\\.");
            var decoder = Base64.getDecoder();
            try {
                String payload = new String(decoder.decode(tokenChunks[1]));
                JSONObject jsonObject = new JSONObject(payload);
                if (!jsonObject.isNull("identity_type") &&
                        ("application".equals(jsonObject.get("identity_type")) ||
                                "user".equals(jsonObject.get("identity_type")))) {
                    processFlag = true;
                    UUID accountId = jsonObject.isNull("account_id") ? null : UUID.fromString(jsonObject.get("account_id").toString());
                    tenantInformation = TenantInformation.builder()
                            .accountId(accountId)
                            .tenantId(tenantId)
                            .username(username)
                            .email(jsonObject.isNull("email") ? null : jsonObject.get("email").toString())
                            .token(jsonObject.isNull("token") ? null : jsonObject.get("token").toString())
                            .jwtToken(jwtToken)
                            .build();
                }
            } catch (Exception e) {
                throw new GeneralizedException("Invalid Token");
            }
        }
        if (StringUtils.isNotBlank(tenantId) &&
                StringUtils.isNotBlank(username) &&
                StringUtils.isNotBlank(jwtToken) &&
                !processFlag) {
            processFlag = true;
            tenantInformation = TenantInformation.builder()
                    .tenantId(tenantId)
                    .username(username)
                    .jwtToken(jwtToken)
                    .build();
        }
        if (!processFlag)
            throw new GeneralizedException("Invalid Credentials");
        this.tenantContext.setTenantInformation(tenantInformation);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        tenantContext.clear();
    }
}
