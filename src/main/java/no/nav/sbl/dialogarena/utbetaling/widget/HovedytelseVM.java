package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.Mottaktertype;


public class HovedytelseVM implements FeedItemVM, Serializable {

    public static final Transformer<Hovedytelse, HovedytelseVM> TIL_HOVEDYTELSEVM = new Transformer<Hovedytelse, HovedytelseVM>() {
        @Override
        public HovedytelseVM transform(Hovedytelse hovedytelse) {
            return new HovedytelseVM(hovedytelse);
        }
    };

    private Hovedytelse hovedytelse;

    public HovedytelseVM(Hovedytelse hovedytelse) {
        this.hovedytelse = hovedytelse;
    }

    public DateTime getUtbetalingsDato() {
        return hovedytelse.getUtbetalingsDato();
    }

    public String getBeskrivelse() {
        return hovedytelse.getYtelsesType();
    }

    public String getBelop() {
        return formaterBelop(hovedytelse.getYtelseNettoBeloep());
    }

    public String getValuta() {
        String valuta = hovedytelse.getValuta();
        return (valuta != null && !valuta.isEmpty() ? valuta : "kr");
    }

    public String getStatus() {
        return hovedytelse.getStatus();
    }

    public DateTime getStartDato() {
        return hovedytelse.getPeriode().getStart();
    }

    public DateTime getSluttDato() {
        return hovedytelse.getPeriode().getEnd();
    }

    public String getUtbetalingId(){
        return hovedytelse.getUtbetalingId();
    }

    public Mottaktertype getMottakertype() {
        return hovedytelse.getMottakertype();
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
    public static class UtbetalingVMComparator implements Comparator<HovedytelseVM> {
        @Override
        public int compare(HovedytelseVM hovedytelseVM1, HovedytelseVM hovedytelseVM2) {
            if (hovedytelseVM1.getUtbetalingsDato() == null && hovedytelseVM2.getUtbetalingsDato() == null) {
                return 0;
            }
            if (hovedytelseVM1.getUtbetalingsDato() == null) {
                return -1;
            }
            if (hovedytelseVM2.getUtbetalingsDato() == null) {
                return 1;
            }
            return hovedytelseVM2.getUtbetalingsDato().compareTo(hovedytelseVM1.getUtbetalingsDato());
        }
    }
}
