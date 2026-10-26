package name.lorenzani.andrea.worldpay.dao;

import name.lorenzani.andrea.worldpay.model.StoredOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface StoredOffersRepository extends JpaRepository<StoredOffer, String> {

    /**
     * The offers that are not expired and match all the criteria that are not null: the title and the description
     * contain the given text, the vendor is the given one (ignoring the case), the price is in the range.
     */
    @Query("""
            SELECT o FROM StoredOffer o
            WHERE o.expiration > :now
              AND (:title IS NULL OR LOWER(o.title) LIKE LOWER(CONCAT('%', :title, '%')))
              AND (:vendor IS NULL OR LOWER(o.vendor) = LOWER(:vendor))
              AND (:description IS NULL OR LOWER(o.description) LIKE LOWER(CONCAT('%', :description, '%')))
              AND (:minPrice IS NULL OR o.price >= :minPrice)
              AND (:maxPrice IS NULL OR o.price <= :maxPrice)
            ORDER BY o.expiration, o.id
            """)
    List<StoredOffer> findByCriteria(@Param("now") Instant now,
                                     @Param("title") String title,
                                     @Param("vendor") String vendor,
                                     @Param("description") String description,
                                     @Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice);
}
