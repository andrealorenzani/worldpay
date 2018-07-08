package name.lorenzani.andrea.worldpay.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "offers")
public class StoredOffer {

    @Id
    @ApiModelProperty(example = "a09ee932-c8cc-4025-9292-101ebec7f175", value = "The id of the offer")
    private String id = null;

    @Column(nullable = false)
    @ApiModelProperty(example = "New Tesla Model 3", value = "The title of the offer")
    private String title = null;

    @Column(nullable = false)
    @ApiModelProperty(example = "The new Tesla Model 3 at a special price only in our shop", value = "The details of the offer")
    private String description = null;

    @Column(nullable = false)
    @ApiModelProperty(example = "Andrea Lorenzani Spa.", value = "The person or company that created the offer")
    private String vendor = null;

    @Column(nullable = false)
    @ApiModelProperty(example = "100000", value = "The price of the offer")
    private BigDecimal price = null;

    @Column(nullable = false)
    @ApiModelProperty(example = "EUR", value = "The currency to apply to the price")
    private CurrencyEnum currency = CurrencyEnum.GBP;

    @Column(nullable = false)
    @ApiModelProperty(example = "2017-07-21T17:32:28Z", value = "The expiration datetime of this offer")
    private Date expiration = null;

    @Column(nullable = false)
    @JsonIgnore
    private String memorableWord = null;
}
