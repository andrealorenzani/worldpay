package name.lorenzani.andrea.worldpay.exception;

public class InternalServerError extends RuntimeException {

    public InternalServerError(String message) {
        super(message);
    }

    public InternalServerError(String message, Throwable error) {
        super(message, error);
    }

}