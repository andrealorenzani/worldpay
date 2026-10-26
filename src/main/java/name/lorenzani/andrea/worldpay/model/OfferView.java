package name.lorenzani.andrea.worldpay.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/** An offer as the clients see it (without the memorable word). */
public record OfferView(
        @Schema(example = "a09ee932-c8cc-4025-9292-101ebec7f175", description = "The id of the offer") String id,
        @Schema(example = "New Tesla Model 3", description = "The title of the offer") String title,
        @Schema(example = "The new Tesla Model 3 at a special price only in our shop",
                description = "The details of the offer") String description,
        @Schema(example = "Andrea Lorenzani Spa.", description = "The person or company that created the offer") String vendor,
        @Schema(example = "100000", description = "The price of the offer") BigDecimal price,
        @Schema(example = "EUR", description = "The currency to apply to the price") OfferCurrency currency,
        @Schema(example = "2026-07-21T17:32:28Z", description = "The expiration datetime of this offer") Instant expiration) {

    public static OfferView of(StoredOffer offer) {
        return new OfferView(offer.getId(), offer.getTitle(), offer.getDescription(), offer.getVendor(),
                offer.getPrice(), offer.getCurrency(), offer.getExpiration());
    }
}
