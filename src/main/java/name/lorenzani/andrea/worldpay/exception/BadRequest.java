package name.lorenzani.andrea.worldpay.exception;

public class BadRequest extends RuntimeException {

    public BadRequest(String message) {
        super(message);
    }

    public BadRequest(String message, Throwable error) {
        super(message, error);
    }

}