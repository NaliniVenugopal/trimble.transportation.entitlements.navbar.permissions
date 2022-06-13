package trimble.transportation.entitlements.navbar.permissions.dto.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> violations;
}
