package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedutbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;


public class HovedutbetalingVM implements FeedItemVM, Serializable {

    public static final Function<Hovedutbetaling, HovedutbetalingVM> TIL_HOVEDUTBETALINGVM = hovedytelse -> new HovedutbetalingVM(hovedytelse);

    private String beskrivelse;
    private DateTime hovedytelseDato;
    private String belop;
    private String status;
    private Interval periode;
    private String utbetalingId;
    private Mottakertype mottakertype;
    private String mottaker;
    private boolean isUtbetalt;


    public HovedutbetalingVM(Hovedutbetaling hovedutbetaling) {
        boolean isFlereHovedytelser = hovedutbetaling.getHovedytelser().size() > 1;
        Hovedytelse forsteYtelse = hovedutbetaling.getHovedytelser().get(0);

        if (isFlereHovedytelser) {
            this.beskrivelse = "Diverse ytelser";
        } else {
            this.beskrivelse = forsteYtelse.getYtelse();
        }
        this.hovedytelseDato = hovedutbetaling.getHovedytelsesdato();
        this.belop = formaterBelop(hovedutbetaling.getNettoUtbetalt());
        this.status = hovedutbetaling.getStatus();
        this.periode = forsteYtelse.getYtelsesperiode();
        this.utbetalingId = hovedutbetaling.getId();
        this.mottakertype = forsteYtelse.getMottakertype();
        this.mottaker = forsteYtelse.getUtbetaltTil().getNavn();
        this.isUtbetalt = hovedutbetaling.isUtbetalt();
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

    public static class UtbetalingVMComparator implements Comparator<HovedutbetalingVM> {
        @Override
        public int compare(HovedutbetalingVM hovedutbetalingVM1, HovedutbetalingVM hovedutbetalingVM2) {
            if (hovedutbetalingVM1.getHovedytelseDato() == null && hovedutbetalingVM2.getHovedytelseDato() == null) {
                return 0;
            }
            if (hovedutbetalingVM1.getHovedytelseDato() == null) {
                return -1;
            }
            if (hovedutbetalingVM2.getHovedytelseDato() == null) {
                return 1;
            }
            return hovedutbetalingVM2.getHovedytelseDato().compareTo(hovedutbetalingVM1.getHovedytelseDato());
        }
    }
}
