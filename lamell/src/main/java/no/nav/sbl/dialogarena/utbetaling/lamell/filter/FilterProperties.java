package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.FilterParameters;
import no.nav.sbl.dialogarena.utbetaling.logikk.Filtrerer;
import org.joda.time.LocalDate;

import java.io.Serializable;


public class FilterProperties implements Serializable {
    public static final String ENDRET = "filter.endret";

    private FilterParameters params;

    public FilterProperties(LocalDate startDato, LocalDate sluttDato, Boolean brukerCheckbox, Boolean arbeidsgiverCheckbox) {
        params = new FilterParameters();
        params.visBruker = brukerCheckbox;
        params.visArbeidsgiver = arbeidsgiverCheckbox;
        params.startDato = startDato;
        params.sluttDato = sluttDato;
    }

    public boolean filtrerPaaDatoer(LocalDate utbetalingsDato) {
        return Filtrerer.filtrerPaaDatoer(utbetalingsDato, params.startDato, params.sluttDato);
    }

    public boolean filtrerPaaMottaker(String mottakerkode) {
        return Filtrerer.filtrerPaaMottaker(mottakerkode, params.visArbeidsgiver, params.visBruker);
    }

    public void setStartDato(LocalDate startDato) {
        params.startDato = startDato;
    }

    public void setSluttDato(LocalDate sluttDato) {
        params.sluttDato = sluttDato;
    }

    public void setVisBruker(Boolean visBruker) {
        params.visBruker = visBruker;
    }

    public void setVisArbeidsgiver(Boolean visArbeidsgiver) {
        params.visArbeidsgiver = visArbeidsgiver;
    }

    public FilterParameters getParams() {
        return params;
    }

    public Boolean getVisBruker() {
        return params.visBruker;
    }

    public Boolean getVisArbeidsgiver() {
        return params.visArbeidsgiver;
    }

    public LocalDate getStartDato() {
        return params.startDato;
    }


    public LocalDate getSluttDato() {
        return params.sluttDato;
    }
}
