package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.FilterParameters;
import no.nav.sbl.dialogarena.utbetaling.logikk.Filtrerer;
import org.joda.time.LocalDate;

import java.io.Serializable;


public class FilterProperties implements Serializable {
    public static final String ENDRET = "filter.endret";
    private LocalDate startDato;
    private LocalDate sluttDato;
    private Boolean visBruker;
    private Boolean visArbeidsgiver;

    public FilterProperties(LocalDate startDato, LocalDate sluttDato, Boolean visBruker, Boolean visArbeidsgiver) {
        this.startDato = startDato;
        this.sluttDato = sluttDato;
        this.visBruker = visBruker;
        this.visArbeidsgiver = visArbeidsgiver;
    }

    public boolean filtrerPaaDatoer(LocalDate utbetalingsDato) {
        return Filtrerer.filtrerPaaDatoer(utbetalingsDato, startDato, sluttDato);
    }

    public boolean filtrerPaaMottaker(String mottakerkode) {
        return Filtrerer.filtrerPaaMottaker(mottakerkode, visArbeidsgiver, visBruker);
    }

    public FilterParameters getParams() {
        FilterParameters params = new FilterParameters();
        params.visBruker = visBruker;
        params.visArbeidsgiver = visArbeidsgiver;
        params.startDato = startDato;
        params.sluttDato = sluttDato;

        return params;
    }

    public LocalDate getStartDato() {
        return startDato;
    }

    public LocalDate getSluttDato() {
        return sluttDato;
    }


}
