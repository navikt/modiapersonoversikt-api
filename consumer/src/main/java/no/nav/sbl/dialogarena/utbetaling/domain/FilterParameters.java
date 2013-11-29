package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;


public class FilterParameters implements Serializable {

    public LocalDate startDato;
    public LocalDate sluttDato;
    public Boolean visBruker;
    public Boolean visArbeidsgiver;

    public FilterParameters(LocalDate startDato, LocalDate sluttDato, Boolean visBruker, Boolean visArbeidsgiver) {
        this.startDato = startDato;
        this.sluttDato = sluttDato;
        this.visBruker = visBruker;
        this.visArbeidsgiver = visArbeidsgiver;
    }
}
