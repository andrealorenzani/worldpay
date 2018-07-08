package name.lorenzani.andrea.worldpay.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class Offer {

    @ApiModelProperty(example = "New Tesla Model 3", required = true, value = "The title of the offer")
    @NotNull
    private String title;
    @ApiModelProperty(example = "The new Tesla Model 3 at a special price only in our shop", required = true, value = "The details of the offer")
    @NotNull
    private String description;
    @ApiModelProperty(example = "Andrea Lorenzani Spa.", required = true, value = "The person or company that created the offer")
    @NotNull
    private String vendor;
    @ApiModelProperty(example = "100000", required = true, value = "The price of the offer")
    @NotNull
    private BigDecimal price;
    @ApiModelProperty(example = "EUR", value = "The currency to apply to the price")
    private CurrencyEnum currency = CurrencyEnum.GBP;
    @ApiModelProperty(example = "86400", value = "The duration of the offer in seconds, if not cancelled (DEFAULT: 1 day)")
    private Integer duration = 86400;
    @ApiModelProperty(example = "pa$$w0rd", required = true, value = "A memorable word to send if you want to delete an offer")
    @NotNull
    private String memorableWord;


}
