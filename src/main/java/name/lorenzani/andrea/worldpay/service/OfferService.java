package name.lorenzani.andrea.worldpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.lorenzani.andrea.worldpay.dao.StoredOffersRepository;
import name.lorenzani.andrea.worldpay.exception.BadRequest;
import name.lorenzani.andrea.worldpay.model.Offer;
import name.lorenzani.andrea.worldpay.model.Offers;
import name.lorenzani.andrea.worldpay.model.StoredOffer;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OfferService {

    private final StoredOffersRepository offersDao;

    public StoredOffer store(Offer offer){
        StoredOffer so = new StoredOffer();
        so.setId(UUID.randomUUID().toString());
        so.setTitle(offer.getTitle());
        so.setCurrency(offer.getCurrency());
        so.setDescription(offer.getDescription());
        so.setExpiration(new DateTime().plus(offer.getDuration()*1000).toDate());
        so.setPrice(offer.getPrice());
        so.setVendor(offer.getVendor());
        so.setMemorableWord(offer.getMemorableWord());
        return offersDao.save(so);
    }

    public Offers retrieve(String title,
                                      String vendor,
                                      String description,
                                      Integer minPrix,
                                      Integer maxPrix){
        Offers res = new Offers();
        res.setOffers(offersDao.findByCriteria(Optional.ofNullable(title).map(String::toLowerCase).orElse(null),
                                                vendor,
                                                Optional.ofNullable(description).map(String::toLowerCase).orElse(null),
                                                Optional.ofNullable(minPrix).map(BigDecimal::new).orElse(null),
                                                Optional.ofNullable(maxPrix).map(BigDecimal::new).orElse(null)));
        return res;
    }

    public StoredOffer delete(String id, String memorableWord) {
        StoredOffer so = offersDao.findById(id).orElse(new StoredOffer());
        if(Objects.isNull(so.getId()) || !so.getMemorableWord().equals(memorableWord)){
            throw new BadRequest("Wrong ID or MEMORABLEWORD");
        }
        offersDao.delete(so);
        return so;
    }
}
