package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.FilterParametere;
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

    public FilterParametere getParams() {
        return new FilterParametere(startDato, sluttDato, visBruker, visArbeidsgiver);
    }

    public LocalDate getStartDato() {
        return startDato;
    }

    public LocalDate getSluttDato() {
        return sluttDato;
    }

}
