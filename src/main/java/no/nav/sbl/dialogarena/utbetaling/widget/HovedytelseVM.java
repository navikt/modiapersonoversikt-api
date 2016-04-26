package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
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

    public static final Transformer<Hovedytelse, HovedytelseVM> TIL_HOVEDYTELSEVM = new Transformer<Hovedytelse, HovedytelseVM>() {
        @Override
        public HovedytelseVM transform(Hovedytelse hovedytelse) {
            return new HovedytelseVM(hovedytelse);
        }
    };


    private String beskrivelse;
    private DateTime hovedytelseDato;
    private String belop;
    private String status;
    private Interval periode;
    private String utbetalingId;
    private Mottakertype mottakertype;
    private String mottaker;
    private boolean isUtbetalt;


    public HovedytelseVM(Hovedytelse hovedytelse) {
        this.beskrivelse = hovedytelse.getYtelse();
        this.hovedytelseDato = hovedytelse.getHovedytelsedato();
        this.belop = formaterBelop(hovedytelse.getNettoUtbetalt());
        this.status = hovedytelse.getUtbetalingsstatus();
        this.periode = hovedytelse.getYtelsesperiode();
        this.utbetalingId = hovedytelse.getId().toString();
        this.mottakertype = hovedytelse.getMottakertype();
        this.mottaker = hovedytelse.getUtbetaltTil().getNavn();
        this.isUtbetalt = hovedytelse.getUtbetalingsDato() != null;
    }

    public boolean isUtbetalt() {
        return isUtbetalt;
    }

    public DateTime getHovedytelseDato() {
        return hovedytelseDato;
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

    public String getMottaker() {
        return mottaker;
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
            if (hovedytelseVM1.getHovedytelseDato() == null && hovedytelseVM2.getHovedytelseDato() == null) {
                return 0;
            }
            if (hovedytelseVM1.getHovedytelseDato() == null) {
                return -1;
            }
            if (hovedytelseVM2.getHovedytelseDato() == null) {
                return 1;
            }
            return hovedytelseVM2.getHovedytelseDato().compareTo(hovedytelseVM1.getHovedytelseDato());
        }
    }
}
