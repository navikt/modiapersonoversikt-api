package no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.oppfolgingskontrakt.to;

import org.joda.time.LocalDate;

import java.io.Serializable;

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
}
