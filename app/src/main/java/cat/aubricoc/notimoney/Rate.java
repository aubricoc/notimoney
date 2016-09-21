package cat.aubricoc.notimoney;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class Rate implements Comparable<Rate> {

    private Date date;

    private Double rate;

    public Rate(Date date, Double rate) {
        this.date = date;
        this.rate = rate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        String dateString = DateFormat.getDateInstance().format(date);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        numberFormat.setCurrency(Currency.getInstance(new Locale("ES", "CO")));
        String rateString = numberFormat.format(rate);
        return dateString + " - " + rateString;
    }

    @Override
    public int compareTo(@NonNull Rate other) {
        return date.compareTo(other.date) * -1;
    }
}
