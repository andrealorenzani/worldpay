package name.lorenzani.andrea.worldpay.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** What the vendor sends to create an offer. */
public record Offer(
        @Schema(example = "New Tesla Model 3", description = "The title of the offer", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String title,

        @Schema(example = "The new Tesla Model 3 at a special price only in our shop",
                description = "The details of the offer", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String description,

        @Schema(example = "Andrea Lorenzani Spa.", description = "The person or company that created the offer",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String vendor,

        @Schema(example = "100000", description = "The price of the offer", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero BigDecimal price,

        @Schema(example = "EUR", description = "The currency to apply to the price", defaultValue = "GBP")
        OfferCurrency currency,

        @Schema(example = "86400", description = "The duration of the offer in seconds, if not cancelled (DEFAULT: 1 day)",
                defaultValue = "86400")
        @Positive Integer duration,

        @Schema(example = "pa$$w0rd", description = "A memorable word to send if you want to delete an offer",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String memorableWord) {

    public static final int DEFAULT_DURATION_SECONDS = 86_400;

    public Offer {
        currency = currency == null ? OfferCurrency.GBP : currency;
        duration = duration == null ? DEFAULT_DURATION_SECONDS : duration;
    }
}
