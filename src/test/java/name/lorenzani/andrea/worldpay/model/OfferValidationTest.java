package name.lorenzani.andrea.worldpay.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class OfferValidationTest {

    private static Locale originalLocale;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeAll
    static void useEnglishMessages() {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @AfterAll
    static void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    private List<String> validate(Offer offer) {
        return validator.validate(offer).stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .toList();
    }

    private static Offer offer(String title, BigDecimal price, Integer duration, String memorableWord) {
        return new Offer(title, "Description", "Vendor", price, null, duration, memorableWord);
    }

    @Test
    void anEmptyOfferMissesTheMandatoryFields() {
        var offer = new Offer(null, null, null, null, null, null, null);

        assertThat(validate(offer)).containsExactlyInAnyOrder(
                "title must not be blank",
                "description must not be blank",
                "vendor must not be blank",
                "price must not be null",
                "memorableWord must not be blank");
    }

    @Test
    void blankTextsAreRejected() {
        assertThat(validate(offer("  ", BigDecimal.TEN, null, "word"))).containsExactly("title must not be blank");
        assertThat(validate(offer("t", BigDecimal.TEN, null, ""))).containsExactly("memorableWord must not be blank");
    }

    @Test
    void priceCannotBeNegativeButCanBeZero() {
        assertThat(validate(offer("t", BigDecimal.valueOf(-1), null, "w")))
                .containsExactly("price must be greater than or equal to 0");
        assertThat(validate(offer("t", BigDecimal.ZERO, null, "w"))).isEmpty();
    }

    @Test
    void durationMustBePositive() {
        assertThat(validate(offer("t", BigDecimal.TEN, 0, "w"))).containsExactly("duration must be greater than 0");
        assertThat(validate(offer("t", BigDecimal.TEN, -5, "w"))).containsExactly("duration must be greater than 0");
    }

    @Test
    void currencyAndDurationHaveDefaults() {
        var offer = offer("t", BigDecimal.TEN, null, "w");

        assertThat(offer.currency()).isEqualTo(OfferCurrency.GBP);
        assertThat(offer.duration()).isEqualTo(86_400);
        assertThat(validate(offer)).isEmpty();
    }
}
