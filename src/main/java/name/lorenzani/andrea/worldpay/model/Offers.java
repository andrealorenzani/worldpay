package name.lorenzani.andrea.worldpay.model;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class Offers {

    @Valid
    private List<StoredOffer> offers;

}
