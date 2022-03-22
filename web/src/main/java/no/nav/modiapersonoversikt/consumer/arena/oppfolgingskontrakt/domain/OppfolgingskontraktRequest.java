package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Objects;

public class OppfolgingskontraktRequest implements Serializable {
    private String fodselsnummer;
    /** Fra og med */
    private LocalDate from;
    /** Til og med */
    private LocalDate to;

    public OppfolgingskontraktRequest() {
    }

    public String getFodselsnummer() {
        return fodselsnummer;
    }

    public void setFodselsnummer(String fodselsnummer) {
        this.fodselsnummer = fodselsnummer;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OppfolgingskontraktRequest that = (OppfolgingskontraktRequest) o;
        return fodselsnummer.equals(that.fodselsnummer) && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fodselsnummer, from, to);
    }
}
