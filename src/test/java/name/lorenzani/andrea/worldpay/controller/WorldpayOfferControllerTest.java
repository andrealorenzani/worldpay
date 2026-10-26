package name.lorenzani.andrea.worldpay.controller;

import name.lorenzani.andrea.worldpay.exception.BadRequest;
import name.lorenzani.andrea.worldpay.exception.InternalServerError;
import name.lorenzani.andrea.worldpay.model.OfferCurrency;
import name.lorenzani.andrea.worldpay.model.OfferView;
import name.lorenzani.andrea.worldpay.model.Offers;
import name.lorenzani.andrea.worldpay.service.OfferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorldpayOfferController.class)
class WorldpayOfferControllerTest {

    private static final String VALID_OFFER = """
            {"title":"Title","description":"Description","vendor":"Vendor","price":1000,"memorableWord":"Memorable"}""";

    private static final OfferView STORED = new OfferView("id-1", "Stored", "Description", "Vendor",
            BigDecimal.valueOf(1000), OfferCurrency.GBP, Instant.parse("2026-01-02T12:00:00Z"));

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OfferService offerService;

    private static MockHttpServletRequestBuilder createRequest(String body) {
        return post("/offer").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void createOfferReturnsTheStoredOffer() throws Exception {
        when(offerService.store(any())).thenReturn(STORED);

        mvc.perform(createRequest(VALID_OFFER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.title").value("Stored"))
                .andExpect(jsonPath("$.expiration").value("2026-01-02T12:00:00Z"))
                .andExpect(jsonPath("$.memorableWord").doesNotExist());
    }

    @Test
    void createOfferAppliesTheDefaultsBeforeCallingTheService() throws Exception {
        when(offerService.store(any())).thenReturn(STORED);

        mvc.perform(createRequest(VALID_OFFER)).andExpect(status().isOk());

        verify(offerService).store(new name.lorenzani.andrea.worldpay.model.Offer("Title", "Description", "Vendor",
                BigDecimal.valueOf(1000), OfferCurrency.GBP, 86_400, "Memorable"));
    }

    @Test
    void createOfferValidatesTheInput() throws Exception {
        mvc.perform(createRequest("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("03"))
                .andExpect(jsonPath("$.message").value(
                        "description must not be blank, memorableWord must not be blank, price must not be null, "
                                + "title must not be blank, vendor must not be blank"));
    }

    @Test
    void createOfferRejectsMalformedJsonAndUnknownCurrencies() throws Exception {
        mvc.perform(createRequest("{not json")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("03"));
        mvc.perform(createRequest(VALID_OFFER.replace("\"price\"", "\"currency\":\"XXX\",\"price\"")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("03"));
    }

    @Test
    void createOfferMapsServiceErrors() throws Exception {
        doThrow(new BadRequest("This is a mocked error")).when(offerService).store(any());
        mvc.perform(createRequest(VALID_OFFER)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("02")).andExpect(jsonPath("$.message").value("This is a mocked error"));

        doThrow(new InternalServerError("mocked")).when(offerService).store(any());
        mvc.perform(createRequest(VALID_OFFER)).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("01"));
    }

    @Test
    void retrieveOfferPassesTheCriteria() throws Exception {
        when(offerService.retrieve(any(), any(), any(), any(), any())).thenReturn(new Offers(List.of(STORED)));

        mvc.perform(get("/offer?title=tit&vendor=v&description=d&minPrix=10.5&maxPrix=20").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offers[0].title").value("Stored"));

        verify(offerService).retrieve("tit", "v", "d", new BigDecimal("10.5"), new BigDecimal("20"));
    }

    @Test
    void retrieveOfferWithoutCriteria() throws Exception {
        when(offerService.retrieve(isNull(), isNull(), isNull(), isNull(), isNull())).thenReturn(new Offers(List.of()));

        mvc.perform(get("/offer")).andExpect(status().isOk()).andExpect(jsonPath("$.offers").isEmpty());
    }

    @Test
    void retrieveOfferRejectsANonNumericPrice() throws Exception {
        mvc.perform(get("/offer?minPrix=cheap")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("03"));
    }

    @Test
    void retrieveOfferMapsServiceErrors() throws Exception {
        doThrow(new BadRequest("Mock error")).when(offerService).retrieve(any(), any(), any(), any(), any());
        mvc.perform(get("/offer")).andExpect(status().isBadRequest());

        doThrow(new InternalServerError("Mock error")).when(offerService).retrieve(any(), any(), any(), any(), any());
        mvc.perform(get("/offer")).andExpect(status().isInternalServerError());
    }

    @Test
    void deleteOfferReturnsTheDeletedOffer() throws Exception {
        when(offerService.delete("a", "b")).thenReturn(STORED);

        mvc.perform(delete("/offer/a/b")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value("id-1"));
    }

    @Test
    void deleteOfferMapsServiceErrors() throws Exception {
        doThrow(new BadRequest("Mock error")).when(offerService).delete(anyString(), anyString());
        mvc.perform(delete("/offer/a/b")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("02"));

        doThrow(new InternalServerError("Mock error")).when(offerService).delete(anyString(), anyString());
        mvc.perform(delete("/offer/a/b")).andExpect(status().isInternalServerError());
    }

    @Test
    void unexpectedErrorsDoNotLeakTheirDetails() throws Exception {
        doThrow(new IllegalStateException("secret detail")).when(offerService).delete(anyString(), anyString());

        mvc.perform(delete("/offer/a/b"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("99"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }

    @Test
    void unknownPathsAndMethodsAreClientErrors() throws Exception {
        mvc.perform(get("/nothing/here")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("404"));
        mvc.perform(post("/offer/a/b")).andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("405"));
    }
}
