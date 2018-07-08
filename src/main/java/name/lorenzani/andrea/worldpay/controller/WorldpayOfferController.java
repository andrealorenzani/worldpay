package name.lorenzani.andrea.worldpay.controller;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.lorenzani.andrea.worldpay.exception.BadRequest;
import name.lorenzani.andrea.worldpay.exception.InternalServerError;
import name.lorenzani.andrea.worldpay.model.ErrorMessage;
import name.lorenzani.andrea.worldpay.model.Offer;
import name.lorenzani.andrea.worldpay.model.Offers;
import name.lorenzani.andrea.worldpay.model.StoredOffer;
import name.lorenzani.andrea.worldpay.service.OfferService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@Api(tags = "offer", description = "Handle offer (create, remove, retrieve)")
public class WorldpayOfferController {

    private final OfferService offerService;

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Offers.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorMessage.class)})
    @GetMapping(value = "/offer",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Offers retrieveOffer(@ApiParam @RequestParam(required = false) String title,
                                @ApiParam @RequestParam(required = false) String vendor,
                                @ApiParam @RequestParam(required = false) String description,
                                @ApiParam @RequestParam(required = false) Integer minPrix,
                                @ApiParam @RequestParam(required = false) Integer maxPrix) {
        log.info("Called GET /offer with title '{}', vendor '{}', description '{}', min price '{}', max price '{}'",
                log(title), log(vendor), log(description), log(minPrix), log(maxPrix));
        return offerService.retrieve(title, vendor, description, minPrix, maxPrix);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = StoredOffer.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorMessage.class)
    })
    @ApiOperation(value = "createOffer", notes = "Insert a new offer")
    @PostMapping(value = "/offer",
            consumes = MediaType.APPLICATION_JSON_VALUE ,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StoredOffer createOffer(@Valid @RequestBody Offer request) {
        log.info("Called PUT /offer with {}", request);
        return offerService.store(request);
    }

    @ApiOperation(value = "delOffer", notes = "Given an offerId, it removes it from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = StoredOffer.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorMessage.class)})
    @DeleteMapping(value = "/offer/{offerId}/{memorableWord}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StoredOffer delOffer(@PathVariable String offerId,
                                @PathVariable String memorableWord) {
        log.info("Called DELETE /offer/{}/{}", offerId, memorableWord);
        return offerService.delete(offerId, memorableWord);
    }

    public <T> String log(T value) {
        return Optional.ofNullable(value).map(T::toString).orElse("");
    }

}
