package trimble.transportation.entitlements.navbar.permissions.utils.exception;


public class HttpClientException extends RuntimeException {
    private final String code;

    public HttpClientException(String message, String code) {
        super(message);
        this.code = code;
    }

    public HttpClientException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public HttpClientException(Throwable cause, String code) {
        super(cause);
        this.code = code;
    }

    public HttpClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
