package name.lorenzani.andrea.worldpay.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import name.lorenzani.andrea.worldpay.exception.BadRequest;
import name.lorenzani.andrea.worldpay.exception.InternalServerError;
import name.lorenzani.andrea.worldpay.model.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandlerAdviceController {

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler({ InternalServerError.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorMessage handleInternalServerError(
            InternalServerError ex, WebRequest request) {
        return new ErrorMessage("01", ex.getMessage());
    }

    @ExceptionHandler({ BadRequest.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleBadRequest(
            BadRequest ex, WebRequest request) {
        return new ErrorMessage("02", ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleBindException(BindException exception) {
        log.error("Processing BindException", exception);
        return new ErrorMessage("03", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidation(MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        ErrorMessage error = new ErrorMessage();
        error.setCode("03");
        error.setMessage(errors.stream()
                .map(err -> String.format("%s %s", err.getField(), err.getDefaultMessage()))
                .collect(Collectors.joining(", ")));
        return error;
    }

    @ExceptionHandler({Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleGenericErrors(Throwable ex) {
        ErrorMessage error = new ErrorMessage();
        error.setMessage(ex.getMessage());
        error.setCode("99");
        log.error("Generic error", ex);
        return error;
    }
}
