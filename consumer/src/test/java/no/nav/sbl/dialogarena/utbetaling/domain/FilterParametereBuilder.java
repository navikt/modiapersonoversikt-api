package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;
import org.joda.time.LocalDate;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class FilterParametereBuilder {
    private LocalDate startDato = new LocalDate().minusMonths(1).minusDays(1);
    private LocalDate sluttDato = new LocalDate().plusDays(1);
    private Boolean visBruker = true;
    private Boolean visArbeidsgiver = true;
    private Set<String> hovedYtelser = new HashSet<>(asList("Dagpenger"));

    public FilterParametereBuilder setStartDato(LocalDate startDato) {
        this.startDato = startDato;
        return this;
    }

    public FilterParametereBuilder setSluttDato(LocalDate sluttDato) {
        this.sluttDato = sluttDato;
        return this;
    }

    public FilterParametereBuilder setVisBruker(Boolean visBruker) {
        this.visBruker = visBruker;
        return this;
    }

    public FilterParametereBuilder setVisArbeidsgiver(Boolean visArbeidsgiver) {
        this.visArbeidsgiver = visArbeidsgiver;
        return this;
    }

    public FilterParametereBuilder setHovedYtelser(Set<String> hovedYtelser) {
        this.hovedYtelser = hovedYtelser;
        return this;
    }

    public FilterParametere createFilterParametere() {
        return new FilterParametere(startDato, sluttDato, visBruker, visArbeidsgiver, hovedYtelser);
    }
}