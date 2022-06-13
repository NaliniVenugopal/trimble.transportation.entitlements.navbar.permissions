package trimble.transportation.entitlements.navbar.permissions.dto.httpservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.http.HttpHeaders;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntity<T> {
    private T responseBody;
    private HttpHeaders headers;
}
