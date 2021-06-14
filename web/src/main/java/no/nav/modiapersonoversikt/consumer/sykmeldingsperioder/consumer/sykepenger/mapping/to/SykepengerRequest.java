package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.mapping.to;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class SykepengerRequest implements Serializable {
    private String ident;
    private LocalDate from;
    private LocalDate to;

    public SykepengerRequest() {
    }

    public SykepengerRequest(LocalDate from, String ident, LocalDate to) {
        this.from = from;
        this.ident = ident;
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
}
