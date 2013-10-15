package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;

import java.io.Serializable;

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
        return Double.toString(utbetaling.getNettoBelop());
    }

    public String getStatus() {
        return utbetaling.getStatuskode();
    }

    public DateTime getStartDato(){
        return utbetaling.getStartDate();
    }

    public DateTime getSluttDato(){
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
}
