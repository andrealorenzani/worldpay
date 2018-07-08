package name.lorenzani.andrea.worldpay.dao;

import name.lorenzani.andrea.worldpay.model.StoredOffer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StoredOffersRepository extends CrudRepository<StoredOffer, String> {

    @Query("SELECT p FROM StoredOffer p WHERE p.expiration > NOW() AND (:title IS NULL OR LOWER(p.title) LIKE CONCAT('%',:title,'%')) AND (:vendor IS NULL OR LOWER(p.vendor) = LOWER(:vendor)) AND (:description IS NULL OR LOWER(p.description) LIKE CONCAT('%',:description,'%')) AND (:minprix IS NULL OR p.price >= :minprix) AND (:maxprix IS NULL OR p.price <= :maxprix)")
    public List<StoredOffer> findByCriteria(@Param("title") String title, @Param("vendor") String vendor, @Param("description") String description, @Param("minprix") BigDecimal minprix, @Param("maxprix") BigDecimal maxprix);
    @Query("DELETE FROM StoredOffer p WHERE p.expiration > NOW() AND :id = p.id AND :memorableWord = p.memorableWord")
    public Integer deleteIfMemorable(@Param("id") String id, @Param("memorableWord") String memorableWord);
}
