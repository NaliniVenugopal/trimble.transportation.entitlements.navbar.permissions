package trimble.transportation.entitlements.navbar.permissions.controller.advice;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import trimble.transportation.entitlements.navbar.permissions.dto.exception.ExceptionResponse;
import trimble.transportation.entitlements.navbar.permissions.utils.exception.GeneralizedException;
import trimble.transportation.entitlements.navbar.permissions.utils.exception.HttpClientException;

@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(HttpClientException.class)
    public ResponseEntity<Object> handleHttpClientException(HttpClientException exception) {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpClientException: ");
        sb.append(exception.getMessage());
        var error = new ExceptionResponse();
        error.setCode("NAVBAR_501");
        error.setMessage(sb.toString());
        log.info("Error : " + error);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {
        var error = new ExceptionResponse();
        error.setCode("NAVBAR_500");
        error.setMessage(getShortExceptionMessage(exception));
        log.info("Error : " + error);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(GeneralizedException.class)
    public ResponseEntity<Object> handleGeneralizedException(GeneralizedException exception) {
        var error = new ExceptionResponse();
        error.setCode("NAVBAR_400");
        error.setMessage(exception.getMessage());
        log.info("Expected/ Violation : " + error);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String getShortExceptionMessage(Exception e) {
        return ExceptionUtils.getStackTrace(e);
    }
}
