package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import no.nav.sbl.dialogarena.utbetaling.domain.Trekk;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import org.joda.time.DateTime;

import java.util.List;

import static no.nav.sbl.dialogarena.time.Datoformat.kortUtenLiteral;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UtbetalingVM {

    private transient Hovedytelse ytelse;

    public UtbetalingVM(Hovedytelse ytelse) {
        this.ytelse = ytelse;
    }

    public String getUtbetalingId() {
        return ytelse.getId().toString();
    }

    public String getStatus() {
        return ytelse.getUtbetalingsstatus();
    }

    public String getYtelse() {
        return ytelse.getYtelse();
    }

    public String getMelding() {
        return ytelse.getUtbetalingsmelding();
    }

    public String getMottakerNavn() {
        return ytelse.getUtbetaltTil().getNavn();
    }

    public Mottakertype getMottakertype() {
        return ytelse.getMottakertype();
    }

    public String getKontonr() {
        return ytelse.getUtbetaltTilKonto();
    }

    public List<Underytelse> getUnderytelser() {
        return ytelse.getUnderytelseListe();
    }

    public List<Trekk> getTrekkListe() {
        return ytelse.getTrekkListe();
    }

    public List<Double> getSkatteTrekk() {
        return ytelse.getSkattListe();
    }

    public DateTime getUtbetalingDato() {
        return ytelse.getUtbetalingsDato();
    }

    public boolean isUtbetalt() {
        return ytelse.getUtbetalingsDato() != null;
    }

    public String getVisningsdatoFormatted() {
        DateTime ytelseDato = ytelse.getHovedytelsedato();
        if(ytelseDato == null) {
            return "Ingen utbetalingsdato";
        }

        return WidgetDateFormatter.date(ytelseDato);
    }

    public DateTime getForfallsDato() {
        return ytelse.getForfallsdato();
    }

    public String getForfallsDatoFormatted() {
        DateTime ytelseDato = ytelse.getForfallsdato();
        if(ytelseDato == null) {
            return "Ingen forfallsdato";
        }

        return WidgetDateFormatter.date(ytelseDato);
    }

    public String getPeriodeMedKortDato() {

        return kortUtenLiteral(ytelse.getYtelsesperiode().getStart()) + " - " + kortUtenLiteral(ytelse.getYtelsesperiode().getEnd());
    }

    public String getBruttoBelop() {
        return getBelopString(ytelse.getBruttoUtbetalt());
    }


    public String getTrekk() {
        return getBelopString(ytelse.getSammenlagtTrekkBeloep());
    }

    public String getUtbetalt() {
        return getBelopString(ytelse.getNettoUtbetalt());
    }

    public DateTime getStartDato() {
        return ytelse.getYtelsesperiode().getStart();
    }

    public DateTime getSluttDato() {
        return ytelse.getYtelsesperiode().getEnd();
    }
}
