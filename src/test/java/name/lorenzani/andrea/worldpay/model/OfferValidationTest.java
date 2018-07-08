package name.lorenzani.andrea.worldpay.model;

import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class OfferValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidateOffer() {
        //Given
        Offer offer = new Offer();

        //When
        List<String> validationMessages = validator.validate(offer).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(5));
        assertThat(validationMessages, hasItem("title must not be null"));
        assertThat(validationMessages, hasItem("description must not be null"));
        assertThat(validationMessages, hasItem("vendor must not be null"));
        assertThat(validationMessages, hasItem("price must not be null"));
        assertThat(validationMessages, hasItem("memorableWord must not be null"));
    }
}