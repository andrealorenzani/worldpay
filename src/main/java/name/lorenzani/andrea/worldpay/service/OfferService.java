package name.lorenzani.andrea.worldpay.service;

import name.lorenzani.andrea.worldpay.dao.StoredOffersRepository;
import name.lorenzani.andrea.worldpay.exception.BadRequest;
import name.lorenzani.andrea.worldpay.model.Offer;
import name.lorenzani.andrea.worldpay.model.OfferView;
import name.lorenzani.andrea.worldpay.model.Offers;
import name.lorenzani.andrea.worldpay.model.StoredOffer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class OfferService {

    private final StoredOffersRepository offers;
    private final PasswordEncoder memorableWords;
    private final Clock clock;

    public OfferService(StoredOffersRepository offers, PasswordEncoder memorableWords, Clock clock) {
        this.offers = offers;
        this.memorableWords = memorableWords;
        this.clock = clock;
    }

    @Transactional
    public OfferView store(Offer offer) {
        var stored = new StoredOffer(
                UUID.randomUUID().toString(),
                offer.title(),
                offer.description(),
                offer.vendor(),
                offer.price(),
                offer.currency(),
                clock.instant().plusSeconds(offer.duration()),
                memorableWords.encode(offer.memorableWord()));
        return OfferView.of(offers.save(stored));
    }

    /** The offers that did not expire and match all the given (not null) criteria. */
    @Transactional(readOnly = true)
    public Offers retrieve(String title, String vendor, String description, BigDecimal minPrice, BigDecimal maxPrice) {
        return new Offers(offers.findByCriteria(clock.instant(), title, vendor, description, minPrice, maxPrice)
                .stream()
                .map(OfferView::of)
                .toList());
    }

    /** Cancels an offer that did not expire yet. */
    @Transactional
    public OfferView delete(String id, String memorableWord) {
        StoredOffer offer = offers.findById(id)
                .filter(found -> memorableWords.matches(memorableWord, found.getMemorableWord()))
                .orElseThrow(() -> new BadRequest("Wrong ID or MEMORABLEWORD"));
        Instant now = clock.instant();
        if (offer.isExpiredAt(now)) {
            throw new BadRequest("Offer already expired");
        }
        offers.delete(offer);
        return OfferView.of(offer);
    }
}
