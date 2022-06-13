package trimble.transportation.entitlements.navbar.permissions.dto.httpservice;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.http.HttpHeaders;
import java.util.Map;

@Data
@AllArgsConstructor
public class ResponseEntityMap<K, V> {
    private Map<K, V> responseBodyMap;
    private HttpHeaders headers;
}

