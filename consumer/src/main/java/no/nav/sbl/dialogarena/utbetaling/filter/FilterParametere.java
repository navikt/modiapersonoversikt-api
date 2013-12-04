package no.nav.sbl.dialogarena.utbetaling.filter;

import org.joda.time.LocalDate;

import java.io.Serializable;


public class FilterParametere implements Serializable {

    public static final String ENDRET = "filterParametere.endret";

    private LocalDate startDato;
    private LocalDate sluttDato;
    private Boolean visBruker;
    private Boolean visArbeidsgiver;

    public FilterParametere(LocalDate startDato, LocalDate sluttDato, Boolean visBruker, Boolean visArbeidsgiver) {
        this.startDato = startDato;
        this.sluttDato = sluttDato;
        this.visBruker = visBruker;
        this.visArbeidsgiver = visArbeidsgiver;
    }

    public Boolean getVisArbeidsgiver() {
        return visArbeidsgiver;
    }

    public Boolean getVisBruker() {
        return visBruker;
    }

    public LocalDate getSluttDato() {
        return sluttDato;
    }

    public LocalDate getStartDato() {
        return startDato;
    }
}
