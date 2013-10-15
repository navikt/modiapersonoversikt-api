package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import java.io.Serializable;

public class UtbetalingVM implements FeedItemVM, Serializable {

    private Utbetaling utbetaling;

    public UtbetalingVM(Utbetaling utbetaling) {
        this.utbetaling = utbetaling;
    }

    public String getUtbetalingsDato() {
        return utbetaling.getUtbetalingsDato().toString();
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

    @Override
    public String getType() {
        return "utbetaling";
    }

    @Override
    public String getId() {
        return "1";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
