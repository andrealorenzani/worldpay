package name.lorenzani.andrea.worldpay.controller;

import name.lorenzani.andrea.worldpay.exception.BadRequest;
import name.lorenzani.andrea.worldpay.exception.InternalServerError;
import name.lorenzani.andrea.worldpay.model.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

/**
 * Every error is an {@link ErrorMessage}: 01 internal error, 02 invalid request, 03 invalid input (malformed or
 * failing validation), 99 unexpected, any other code is the HTTP status (404, 405...) of the error.
 */
@RestControllerAdvice
public class ErrorHandlerAdviceController extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandlerAdviceController.class);

    @ExceptionHandler(InternalServerError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorMessage handleInternalServerError(InternalServerError ex) {
        log.error("Internal server error", ex);
        return new ErrorMessage("01", ex.getMessage());
    }

    @ExceptionHandler(BadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage handleBadRequest(BadRequest ex) {
        return new ErrorMessage("02", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorMessage handleGenericErrors(Exception ex) {
        log.error("Generic error", ex);
        return new ErrorMessage("99", "Unexpected error");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s %s".formatted(error.getField(), error.getDefaultMessage()))
                .sorted()
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(status).headers(headers).body(new ErrorMessage("03", message));
    }

    /** All the other errors raised by Spring MVC itself (malformed JSON, wrong type, unknown path...). */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode status, WebRequest request) {
        String code = status.value() == 400 ? "03" : String.valueOf(status.value());
        String message = status.is4xxClientError() ? clientMessage(ex, status) : "Unexpected error";
        return ResponseEntity.status(status).headers(headers).body(new ErrorMessage(code, message));
    }

    private static String clientMessage(Exception ex, HttpStatusCode status) {
        return status.value() == 400 ? "Malformed request: " + rootMessage(ex)
                : HttpStatus.valueOf(status.value()).getReasonPhrase();
    }

    private static String rootMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage() == null ? ex.getClass().getSimpleName() : root.getMessage();
    }
}
