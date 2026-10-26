package name.lorenzani.andrea.worldpay.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorMessage(
        @Schema(description = "A code for the error", example = "02") String code,
        @Schema(description = "The description of the error", example = "Wrong ID or MEMORABLEWORD") String message) {
}
