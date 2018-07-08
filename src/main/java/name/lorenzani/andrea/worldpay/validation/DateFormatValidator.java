package name.lorenzani.andrea.worldpay.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Created by Oleksandr Murha on 04/11/2016.
 */

public class DateFormatValidator implements ConstraintValidator<DateFormat, String>  {

    private String format;

    private boolean nullable;

    @Override
    public void initialize(DateFormat dateFormatAnnotation) {
        format = dateFormatAnnotation.value();
        nullable = dateFormatAnnotation.nullable();
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return validateNullableDate(context);
        }
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validateNullableDate(ConstraintValidatorContext context) {
        if (nullable) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate("may not be empty")
                .addConstraintViolation();
        return false;
    }
}
