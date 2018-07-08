package name.lorenzani.andrea.worldpay.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Oleksandr Murha on 04/11/2016.
 */

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = DateFormatValidator.class)
@Documented
public @interface DateFormat {

    String message() default "must be in correct date format";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String value();

    boolean nullable() default false;
}