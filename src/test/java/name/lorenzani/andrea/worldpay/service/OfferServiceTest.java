package name.lorenzani.andrea.worldpay.service;

import name.lorenzani.andrea.worldpay.TestClockConfig;
import name.lorenzani.andrea.worldpay.dao.StoredOffersRepository;
import name.lorenzani.andrea.worldpay.exception.BadRequest;
import name.lorenzani.andrea.worldpay.model.Offer;
import name.lorenzani.andrea.worldpay.model.OfferCurrency;
import name.lorenzani.andrea.worldpay.model.OfferView;
import name.lorenzani.andrea.worldpay.model.StoredOffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestClockConfig.class)
class OfferServiceTest {

    private static final Instant NOW = TestClockConfig.NOW;

    @Autowired
    private OfferService service;

    @Autowired
    private StoredOffersRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    private void save(String id, String title, String vendor, String price, Duration expiresIn) {
        repository.save(new StoredOffer(id, title, "Description of " + title, vendor, new BigDecimal(price),
                OfferCurrency.GBP, NOW.plus(expiresIn), encoder.encode("a")));
    }

    private static Offer offer(String title, int duration) {
        return new Offer(title, "Description", "Vendor", new BigDecimal("99.90"), OfferCurrency.EUR, duration, "pa$$w0rd");
    }

    @Test
    void storesAnOfferThatExpiresAfterItsDuration() {
        OfferView stored = service.store(offer("Tesla", 3600));

        assertThat(stored.id()).isNotBlank();
        assertThat(stored.expiration()).isEqualTo(NOW.plusSeconds(3600));
        assertThat(stored.price()).isEqualByComparingTo("99.90");
        assertThat(stored.currency()).isEqualTo(OfferCurrency.EUR);
        assertThat(repository.findById(stored.id())).isPresent();
    }

    @Test
    void doesNotStoreTheMemorableWordInTheClear() {
        OfferView stored = service.store(offer("Tesla", 3600));

        String persisted = repository.findById(stored.id()).orElseThrow().getMemorableWord();

        assertThat(persisted).isNotEqualTo("pa$$w0rd");
        assertThat(encoder.matches("pa$$w0rd", persisted)).isTrue();
    }

    @Test
    void retrievesOnlyTheOffersThatDidNotExpire() {
        save("so1", "Valid", "vendor1", "1000", Duration.ofDays(2));
        save("so2", "Expired", "vendor1", "1000", Duration.ZERO);
        save("so3", "Long expired", "vendor1", "1000", Duration.ofDays(-5));

        assertThat(service.retrieve(null, null, null, null, null).offers()).extracting(OfferView::id)
                .containsExactly("so1");
    }

    @Test
    void retrievesByTitleDescriptionAndVendorIgnoringTheCase() {
        save("so1", "First Stored Offer", "vendor1", "1000", Duration.ofDays(2));
        save("so2", "Second Stored Offer", "vendor2", "2000", Duration.ofDays(2));

        assertThat(service.retrieve("second", null, null, null, null).offers()).extracting(OfferView::id)
                .containsExactly("so2");
        assertThat(service.retrieve("STORED", null, null, null, null).offers()).hasSize(2);
        assertThat(service.retrieve(null, "VENDOR1", null, null, null).offers()).extracting(OfferView::id)
                .containsExactly("so1");
        assertThat(service.retrieve(null, "vendor", null, null, null).offers()).isEmpty(); // vendor is not a substring
        assertThat(service.retrieve(null, null, "of the first", null, null).offers()).isEmpty();
        assertThat(service.retrieve(null, null, "Description of first", null, null).offers())
                .extracting(OfferView::id).containsExactly("so1");
        assertThat(service.retrieve("nothing like this", null, null, null, null).offers()).isEmpty();
    }

    @Test
    void retrievesByPriceRange() {
        save("so1", "One", "v", "1000", Duration.ofDays(2));
        save("so2", "Two", "v", "2000.50", Duration.ofDays(2));
        save("so3", "Three", "v", "13000", Duration.ofDays(2));

        var inclusive = service.retrieve(null, null, null, new BigDecimal("1000"), new BigDecimal("2000.50"));
        assertThat(inclusive.offers()).extracting(OfferView::id).containsExactly("so1", "so2");
        assertThat(service.retrieve(null, null, null, new BigDecimal("999"), new BigDecimal("1001")).offers())
                .extracting(OfferView::id).containsExactly("so1");
        assertThat(service.retrieve(null, null, null, new BigDecimal("2001"), null).offers())
                .extracting(OfferView::id).containsExactly("so3");
        assertThat(service.retrieve(null, null, null, null, new BigDecimal("10")).offers()).isEmpty();
    }

    @Test
    void combinesTheCriteria() {
        save("so1", "Car", "vendor1", "1000", Duration.ofDays(2));
        save("so2", "Car", "vendor2", "1000", Duration.ofDays(2));

        assertThat(service.retrieve("car", "vendor2", null, new BigDecimal("500"), null).offers())
                .extracting(OfferView::id).containsExactly("so2");
    }

    @Test
    void deletesAnOfferWithTheRightMemorableWord() {
        save("so1", "One", "v", "1000", Duration.ofDays(2));

        OfferView deleted = service.delete("so1", "a");

        assertThat(deleted.id()).isEqualTo("so1");
        assertThat(repository.existsById("so1")).isFalse();
    }

    @Test
    void doesNotDeleteWithAWrongMemorableWordOrId() {
        save("so1", "One", "v", "1000", Duration.ofDays(2));

        assertThatThrownBy(() -> service.delete("so1", "wrong"))
                .isInstanceOf(BadRequest.class).hasMessage("Wrong ID or MEMORABLEWORD");
        assertThatThrownBy(() -> service.delete("nope", "a"))
                .isInstanceOf(BadRequest.class).hasMessage("Wrong ID or MEMORABLEWORD");
        assertThat(repository.existsById("so1")).isTrue();
    }

    @Test
    void doesNotDeleteAnExpiredOffer() {
        save("so5", "Expired", "v", "1000", Duration.ZERO);

        assertThatThrownBy(() -> service.delete("so5", "a"))
                .isInstanceOf(BadRequest.class).hasMessage("Offer already expired");
        assertThat(repository.existsById("so5")).isTrue();
    }
}
