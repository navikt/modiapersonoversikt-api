package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.logikk.Filtrerer;
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
        return Filtrerer.filtrerPaaDatoer(utbetalingsDato, startDato, sluttDato);
    }

    public boolean filtrerPaaMottaker(String mottakerkode) {
        return Filtrerer.filtrerPaaMottaker(mottakerkode, visArbeidsgiver, visBruker);
    }
}
