package name.lorenzani.andrea.worldpay.controller;

import name.lorenzani.andrea.worldpay.TestClockConfig;
import name.lorenzani.andrea.worldpay.dao.StoredOffersRepository;
import name.lorenzani.andrea.worldpay.model.OfferCurrency;
import name.lorenzani.andrea.worldpay.model.StoredOffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The scenarios that used to be the Spring Cloud Contract files, against the real service and database. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestClockConfig.class)
class OfferApiIntegrationTest {

    private static final Instant NOW = TestClockConfig.NOW;
    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private StoredOffersRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void populate() {
        repository.deleteAll();
        offer("so1", "First Stored Offer", "vendor1", 1000, Duration.ofDays(2));
        offer("so2", "Second Stored Offer", "vendor2", 2000, Duration.ofDays(2));
        offer("so3", "Third Stored Offer", "vendor1", 3000, Duration.ZERO);
        offer("so4", "Fourth Stored Offer", "vendor1", 13000, Duration.ofDays(2));
        offer("so5", "Fifth Stored Offer", "vendor1", 13000, Duration.ZERO);
    }

    private void offer(String id, String title, String vendor, int price, Duration expiresIn) {
        repository.save(new StoredOffer(id, title, "Description of the " + title.toLowerCase(), vendor,
                BigDecimal.valueOf(price), OfferCurrency.GBP, NOW.plus(expiresIn), encoder.encode("a")));
    }

    @Test
    void createsAnOffer() throws Exception {
        mvc.perform(post("/offer").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"New Tesla Model 3",
                                 "description":"The new Tesla Model 3 at a special price only in our shop",
                                 "vendor":"Andrea Lorenzani Spa.","price":100000,"currency":"EUR","duration":86400,
                                 "memorableWord":"pa$$w0rd"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(matchesPattern(UUID_PATTERN)))
                .andExpect(jsonPath("$.title").value("New Tesla Model 3"))
                .andExpect(jsonPath("$.vendor").value("Andrea Lorenzani Spa."))
                .andExpect(jsonPath("$.price").value(100000))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.expiration").value("2026-01-02T12:00:00Z"));
    }

    @Test
    void aCreatedOfferCanBeFoundAndDeletedWithItsMemorableWord() throws Exception {
        mvc.perform(post("/offer").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Bike","description":"A red bike","vendor":"Bob","price":50,
                                 "memorableWord":"secret"}"""))
                .andExpect(status().isOk());

        mvc.perform(get("/offer?title=bike"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offers.length()").value(1))
                .andExpect(jsonPath("$.offers[0].currency").value("GBP"))
                .andExpect(jsonPath("$.offers[0].expiration").value("2026-01-02T12:00:00Z"))
                .andExpect(jsonPath("$.offers[0].memorableWord").doesNotExist());

        String id = repository.findAll().stream().filter(o -> o.getTitle().equals("Bike")).findFirst().orElseThrow().getId();
        mvc.perform(delete("/offer/" + id + "/wrong")).andExpect(status().isBadRequest());
        mvc.perform(delete("/offer/" + id + "/secret")).andExpect(status().isOk());
        mvc.perform(get("/offer?title=bike")).andExpect(jsonPath("$.offers").isEmpty());
    }

    @Test
    void failsValidationOnAnEmptyOffer() throws Exception {
        mvc.perform(post("/offer").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("03"));
    }

    @Test
    void retrievesByPrice() throws Exception {
        mvc.perform(get("/offer?minPrix=999&maxPrix=1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offers.length()").value(1))
                .andExpect(jsonPath("$.offers[0].id").value("so1"))
                .andExpect(jsonPath("$.offers[0].title").value("First Stored Offer"))
                .andExpect(jsonPath("$.offers[0].description").value("Description of the first stored offer"))
                .andExpect(jsonPath("$.offers[0].vendor").value("vendor1"))
                .andExpect(jsonPath("$.offers[0].price").value(1000))
                .andExpect(jsonPath("$.offers[0].currency").value("GBP"));
        mvc.perform(get("/offer?minPrix=5000&maxPrix=6000"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.offers").isEmpty());
    }

    @Test
    void retrievesByTitleAndDescription() throws Exception {
        mvc.perform(get("/offer?title=second")).andExpect(jsonPath("$.offers[0].id").value("so2"));
        mvc.perform(get("/offer?description=fourth stored")).andExpect(jsonPath("$.offers[0].id").value("so4"));
        mvc.perform(get("/offer?title=nothing")).andExpect(jsonPath("$.offers").isEmpty());
        mvc.perform(get("/offer?vendor=VENDOR2")).andExpect(jsonPath("$.offers[0].id").value("so2"));
    }

    @Test
    void anExpiredOfferIsNotRetrieved() throws Exception {
        mvc.perform(get("/offer?title=Third"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offers").isEmpty());
        mvc.perform(get("/offer")).andExpect(jsonPath("$.offers.length()").value(3));
    }

    @Test
    void deletesAnOffer() throws Exception {
        mvc.perform(delete("/offer/so4/a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("so4"))
                .andExpect(jsonPath("$.price").value(13000));
        mvc.perform(delete("/offer/so4/a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Wrong ID or MEMORABLEWORD"));
    }

    @Test
    void doesNotDeleteWithAWrongMemorableWord() throws Exception {
        mvc.perform(delete("/offer/so1/pa$$w0rd"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("02"))
                .andExpect(jsonPath("$.message").value("Wrong ID or MEMORABLEWORD"));
    }

    @Test
    void doesNotDeleteAnExpiredOffer() throws Exception {
        mvc.perform(delete("/offer/so5/a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("02"))
                .andExpect(jsonPath("$.message").value("Offer already expired"));
    }

    @Test
    void exposesHealthAndInfo() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/actuator/info")).andExpect(status().isOk());
        mvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
    }
}
