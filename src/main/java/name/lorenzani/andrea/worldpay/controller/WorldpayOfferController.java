package name.lorenzani.andrea.worldpay.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import name.lorenzani.andrea.worldpay.model.ErrorMessage;
import name.lorenzani.andrea.worldpay.model.Offer;
import name.lorenzani.andrea.worldpay.model.OfferView;
import name.lorenzani.andrea.worldpay.model.Offers;
import name.lorenzani.andrea.worldpay.service.OfferService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/offer", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "offer", description = "Handle offer (create, remove, retrieve)")
@ApiResponse(responseCode = "400", description = "Invalid input",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
@ApiResponse(responseCode = "500", description = "Internal error",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
public class WorldpayOfferController {

    private final OfferService offerService;

    public WorldpayOfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @Operation(operationId = "retrieveOffer", summary = "Retrieve offer(s)",
            description = "Retrieves the offers that did not expire and match all the given criteria")
    @GetMapping
    public Offers retrieveOffer(
            @RequestParam(required = false) @Parameter(description = "(Part of) the title of the offer") String title,
            @RequestParam(required = false) @Parameter(description = "The vendor of the offer") String vendor,
            @RequestParam(required = false) @Parameter(description = "(Part of) the description of the offer") String description,
            @RequestParam(required = false) @Parameter(description = "Minimal price") BigDecimal minPrix,
            @RequestParam(required = false) @Parameter(description = "Maximum price") BigDecimal maxPrix) {
        return offerService.retrieve(title, vendor, description, minPrix, maxPrix);
    }

    @Operation(operationId = "createOffer", summary = "Insert a new offer")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public OfferView createOffer(@Valid @RequestBody Offer request) {
        return offerService.store(request);
    }

    @Operation(operationId = "delOffer", summary = "Remove a specific offer",
            description = "Given an offerId and the memorable word used to create it, it removes the offer (unless it expired)")
    @DeleteMapping("/{offerId}/{memorableWord}")
    public OfferView delOffer(@PathVariable String offerId, @PathVariable String memorableWord) {
        return offerService.delete(offerId, memorableWord);
    }
}
