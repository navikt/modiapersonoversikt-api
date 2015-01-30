package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;



public class HovedytelseVM implements FeedItemVM, Serializable {

    public static final Transformer<Record<Hovedytelse>, HovedytelseVM> TIL_HOVEDYTELSEVM = new Transformer<Record<Hovedytelse>, HovedytelseVM>() {
        @Override
        public HovedytelseVM transform(Record<Hovedytelse> hovedytelse) {
            return new HovedytelseVM(hovedytelse);
        }
    };

    private transient Record<Hovedytelse> hovedytelse;

    public HovedytelseVM(Record<Hovedytelse> hovedytelse) {
        this.hovedytelse = hovedytelse;
    }

    public DateTime getUtbetalingsDato() {
        return hovedytelse.get(Hovedytelse.utbetalingsDato);
    }

    public String getBeskrivelse() {
        return hovedytelse.get(Hovedytelse.ytelse);
    }

    public String getBelop() {
        return formaterBelop(hovedytelse.get(Hovedytelse.ytelseNettoBeloep));
    }

    public String getStatus() {
        return hovedytelse.get(Hovedytelse.utbetalingsstatus);
    }

    public DateTime getStartDato() {
        return hovedytelse.get(Hovedytelse.ytelsesperiode).getStart();
    }

    public DateTime getSluttDato() {
        return hovedytelse.get(Hovedytelse.ytelsesperiode).getEnd();
    }

    public String getUtbetalingId(){
        return hovedytelse.get(Hovedytelse.id).toString();
    }

    public Mottakertype getMottakertype() {
        return hovedytelse.get(Hovedytelse.mottakertype);
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
