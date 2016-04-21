package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.sbl.dialogarena.utbetaling.domain.*;
import org.joda.time.DateTime;

import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT_UTEN_LITERAL;
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
        return ytelse.getUtbetaltTil().get(Aktoer.navn);
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
        return optional(ytelse.getYtelsesperiode().getStart()).map(KORT_UTEN_LITERAL).getOrElse("") + " - "
                + optional(ytelse.getYtelsesperiode().getEnd()).map(KORT_UTEN_LITERAL).getOrElse("");
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
