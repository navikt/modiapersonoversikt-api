package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Mottaker;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import org.joda.time.LocalDate;

import java.io.Serializable;


public class Filter implements Serializable {

    public static final String ENDRET = "filter.endret";

    private LocalDate startDato;
    private LocalDate sluttDato;
    private Boolean visBruker;
    private Boolean visArbeidsgiver;

    public Filter(LocalDate startDato, LocalDate sluttDato, Boolean brukerCheckbox, Boolean arbeidsgiverCheckbox) {
        this.visBruker = brukerCheckbox;
        this.visArbeidsgiver = arbeidsgiverCheckbox;
        this.startDato = startDato;
        this.sluttDato = sluttDato;
    }

    public LocalDate getStartDato() {
        return startDato;
    }

    public LocalDate getSluttDato() {
        return sluttDato;
    }

    public boolean filtrerPaaDatoer(LocalDate utbetalingsDato) {
        return utbetalingsDato.isAfter(startDato) &&
                utbetalingsDato.isBefore(sluttDato);
    }

    public boolean filtrerPaaMottaker(String mottakerkode) {
        boolean arbeidsgiverVises = this.visArbeidsgiver && Mottaker.ARBEIDSGIVER.equalsIgnoreCase(mottakerkode);
        boolean brukerVises = this.visBruker && Mottaker.BRUKER.equalsIgnoreCase(mottakerkode);
        return arbeidsgiverVises || brukerVises;
    }

    public Periode getPeriode() {
        return new Periode(startDato.toDateTimeAtCurrentTime(), sluttDato.toDateTimeAtCurrentTime());
    }
}
