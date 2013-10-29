package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class UtbetalingVM implements FeedItemVM, Serializable, Comparable<UtbetalingVM> {

    private Utbetaling utbetaling;

    public UtbetalingVM(Utbetaling utbetaling) {
        this.utbetaling = utbetaling;
    }

    public DateTime getUtbetalingsDato() {
        return utbetaling.getUtbetalingsDato();
    }

    public String getBeskrivelse() {
        return utbetaling.getBeskrivelse();
    }

    public String getPeriode() {
        return utbetaling.getPeriode();
    }

    public String getBelop() {
        return formaterBelop(utbetaling.getNettoBelop());
    }

    public String getValuta() {
        String valuta = utbetaling.getValuta();
        return (valuta != null && !valuta.isEmpty() ? valuta : "kr");
    }

    public String getStatus() {
        return utbetaling.getStatuskode();
    }

    public DateTime getStartDato() {
        return utbetaling.getStartDate();
    }

    public DateTime getSluttDato() {
        return utbetaling.getEndDate();
    }

    @Override
    public String getType() {
        return "utbetaling";
    }

    @Override
    public String getId() {
        return "1";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(UtbetalingVM o) {
        return o.getUtbetalingsDato().compareTo(getUtbetalingsDato());
    }

    // CHECKSTYLE:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UtbetalingVM that = (UtbetalingVM) o;

        if (!utbetaling.equals(that.utbetaling)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return utbetaling.hashCode();
    }

    // CHECKSTYLE:ON

    private String formaterBelop(double nettoBelop) {
        NumberFormat nf =  NumberFormat.getNumberInstance(Locale.forLanguageTag("nb"));
        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(2);
        return nf.format(nettoBelop);
    }
}
