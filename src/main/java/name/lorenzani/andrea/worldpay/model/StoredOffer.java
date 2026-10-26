package name.lorenzani.andrea.worldpay.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/** An offer as it is stored. It is never sent as it is: see {@link OfferView}. */
@Entity
@Table(name = "offers")
public class StoredOffer {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String vendor;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferCurrency currency;

    @Column(nullable = false)
    private Instant expiration;

    /** The hash of the memorable word. */
    @Column(nullable = false)
    private String memorableWord;

    protected StoredOffer() {
    }

    public StoredOffer(String id, String title, String description, String vendor, BigDecimal price,
                       OfferCurrency currency, Instant expiration, String memorableWord) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.vendor = vendor;
        this.price = price;
        this.currency = currency;
        this.expiration = expiration;
        this.memorableWord = memorableWord;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public OfferCurrency getCurrency() {
        return currency;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public String getMemorableWord() {
        return memorableWord;
    }

    public boolean isExpiredAt(Instant instant) {
        return !expiration.isAfter(instant);
    }
}
