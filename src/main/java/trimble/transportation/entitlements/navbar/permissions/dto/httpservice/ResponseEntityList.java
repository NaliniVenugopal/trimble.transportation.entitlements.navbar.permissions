package trimble.transportation.entitlements.navbar.permissions.dto.httpservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.http.HttpHeaders;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntityList<T> {
    private List<T> responseBodyList;
    private HttpHeaders headers;
}
