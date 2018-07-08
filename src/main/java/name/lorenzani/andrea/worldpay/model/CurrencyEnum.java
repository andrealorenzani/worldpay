package name.lorenzani.andrea.worldpay.model;


/**
 * The currency to apply to the price
 */
public enum CurrencyEnum {
    EUR("EUR"),

    GBP("GBP"),

    USD("USD");

    private String value;

    CurrencyEnum(String value) {
        this.value = value;
    }

}
