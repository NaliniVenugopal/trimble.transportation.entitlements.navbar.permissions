package trimble.transportation.entitlements.navbar.permissions.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * InterceptorHandlerConfig
 */
@Configuration
public class InterceptorHandlerConfig implements WebMvcConfigurer {

    private final RequestInterceptorHandler requestInterceptorHandler;

    public InterceptorHandlerConfig(RequestInterceptorHandler requestInterceptorHandler) {
        this.requestInterceptorHandler = requestInterceptorHandler;
    }

    @Bean
    public MappedInterceptor requestInceptor() {
        String[] includes = new String[]{"/**"};
        String[] excludes = new String[]{
                "/v3/api-docs",
                "/v3/api-docs.*",
                "/v3/api-docs/swagger-config",
                "/configuration/ui",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/actuator/**"};
        return new MappedInterceptor(includes, excludes, requestInterceptorHandler);
    }
}
