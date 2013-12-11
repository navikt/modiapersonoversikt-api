package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottaker;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class UtbetalingVM implements FeedItemVM, Serializable {

    public static final Transformer<Utbetaling, UtbetalingVM> UTBETALING_UTBETALINGVM_TRANSFORMER = new Transformer<Utbetaling, UtbetalingVM>() {
        @Override
        public UtbetalingVM transform(Utbetaling utbetaling) {
            return new UtbetalingVM(utbetaling);
        }
    };

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
        return utbetaling.getPeriode().getPeriode();
    }

    public String getBelop() {
        return formaterBelop(utbetaling.getNettoBelop());
    }

    public String getValuta() {
        String valuta = utbetaling.getValuta();
        return (valuta != null && !valuta.isEmpty() ? valuta : "kr");
    }

    public String getStatus() {
        return utbetaling.getStatusBeskrivelse();
    }

    public DateTime getStartDato() {
        return utbetaling.getStartDate();
    }

    public DateTime getSluttDato() {
        return utbetaling.getEndDate();
    }

    public String getUtbetalingId(){
        return utbetaling.getUtbetalingId();
    }

    public Mottaker getMottaker() {
        return utbetaling.getMottaker();
    }

    @Override
    public String getType() {
        return "utbetaling";
    }

    @Override
    public String getId() {
        return getUtbetalingId();
    }

    private String formaterBelop(double nettoBelop) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("nb"));
        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(2);
        return nf.format(nettoBelop);
    }
}
