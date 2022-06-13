package trimble.transportation.entitlements.navbar.permissions.utils.exception;

public class GeneralizedException extends RuntimeException{
    public GeneralizedException() {
    }

    public GeneralizedException(String message) {
        super(message);
    }

    public GeneralizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralizedException(Throwable cause) {
        super(cause);
    }

    public GeneralizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
