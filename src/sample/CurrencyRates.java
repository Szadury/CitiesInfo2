package sample;

import java.util.Currency;
import java.util.Date;
import java.util.Map;

public class CurrencyRates {
    Map<String, Double> rates;
    Date date;
    String base;

    public CurrencyRates(Map<String, Double> rates, Date date, String base) {
        this.rates = rates;
        this.date = date;
        this.base = base;
    }
}
