package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;

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


    private String beskrivelse;
    private DateTime utbetalingsDato;
    private String belop;
    private String status;
    private Interval periode;
    private String utbetalingId;
    private Mottakertype mottakertype;


    public HovedytelseVM(Record<Hovedytelse> hovedytelse) {
        this.beskrivelse = hovedytelse.get(Hovedytelse.ytelse);
        this.utbetalingsDato = hovedytelse.get(Hovedytelse.utbetalingsDato);
        this.belop = formaterBelop(hovedytelse.get(Hovedytelse.ytelseNettoBeloep));
        this.status = hovedytelse.get(Hovedytelse.utbetalingsstatus);
        this.periode = hovedytelse.get(Hovedytelse.ytelsesperiode);
        this.utbetalingId = hovedytelse.get(Hovedytelse.id).toString();
        this.mottakertype = hovedytelse.get(Hovedytelse.mottakertype);
    }

    public DateTime getUtbetalingsDato() {
        return utbetalingsDato;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public String getBelop() {
        return belop;
    }

    public String getStatus() {
        return status;
    }

    public DateTime getStartDato() {
        return periode.getStart();
    }

    public DateTime getSluttDato() {
        return periode.getEnd();
    }

    public String getUtbetalingId(){
        return utbetalingId;
    }

    public Mottakertype getMottakertype() {
        return mottakertype;
    }

    @Override
    public String getType() {
        return "utbetaling";
    }

    @Override
    public String getId() {
        return getUtbetalingId();
    }

    private String formaterBelop(Double nettoBelop) {
        if(nettoBelop == null) {
            return "";
        }

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
