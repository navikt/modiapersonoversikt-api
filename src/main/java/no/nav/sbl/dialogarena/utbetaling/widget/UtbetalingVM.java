package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.Mottaktertype;


public class UtbetalingVM implements FeedItemVM, Serializable {

    public static final Transformer<Utbetaling, UtbetalingVM> TIL_UTBETALINGVM = new Transformer<Utbetaling, UtbetalingVM>() {
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
        return utbetaling.getUtbetalingsdato();
    }

    public String getBeskrivelse() {
        return utbetaling.getHovedytelse();
    }

    public String getBelop() {
        return formaterBelop(utbetaling.getUtbetalt());
    }

    public String getValuta() {
        String valuta = utbetaling.getValuta();
        return (valuta != null && !valuta.isEmpty() ? valuta : "kr");
    }

    public String getStatus() {
        return utbetaling.getStatus();
    }

    public DateTime getStartDato() {
        return utbetaling.getPeriode().getStart();
    }

    public DateTime getSluttDato() {
        return utbetaling.getPeriode().getEnd();
    }

    public String getUtbetalingId(){
        return utbetaling.getUtbetalingId();
    }

    public Mottaktertype getMottakertype() {
        return utbetaling.getMottaktertype();
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
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(2);
        return nf.format(nettoBelop);
    }

    /**
     * Sorterer i omvendt kronologisk rekkefølge på utbetalingsdato
     */
    public static class UtbetalingVMComparator implements Comparator<UtbetalingVM> {
        @Override
        public int compare(UtbetalingVM utbetalingVM1, UtbetalingVM utbetalingVM2) {
            if (utbetalingVM1.getUtbetalingsDato() == null && utbetalingVM2.getUtbetalingsDato() == null) {
                return 0;
            }
            if (utbetalingVM1.getUtbetalingsDato() == null) {
                return -1;
            }
            if (utbetalingVM2.getUtbetalingsDato() == null) {
                return 1;
            }
            return utbetalingVM2.getUtbetalingsDato().compareTo(utbetalingVM1.getUtbetalingsDato());
        }
    }
}
