package no.nav.modiapersonoversikt.consumer.arena.kontrakter.domain.ytelse;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class Vedtak implements Serializable {
    private String vedtakstype;
    private LocalDate activeFrom;
    private LocalDate activeTo;
    private LocalDate vedtaksdato;
    private String vedtakstatus;
    private String aktivitetsfase;

    public Vedtak() {
    }

    public LocalDate getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(LocalDate activeFrom) {
        this.activeFrom = activeFrom;
    }

    public LocalDate getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(LocalDate activeTo) {
        this.activeTo = activeTo;
    }

    public LocalDate getVedtaksdato() {
        return vedtaksdato;
    }

    public void setVedtaksdato(LocalDate vedtaksdato) {
        this.vedtaksdato = vedtaksdato;
    }

    public String getVedtakstatus() {
        return vedtakstatus;
    }

    public void setVedtakstatus(String vedtakstatus) {
        this.vedtakstatus = vedtakstatus;
    }

    public String getVedtakstype() {
        return vedtakstype;
    }

    public void setVedtakstype(String vedtakstype) {
        this.vedtakstype = vedtakstype;
    }

    public String getAktivitetsfase() {
        return aktivitetsfase;
    }

    public void setAktivitetsfase(String aktivitetsfase) {
        this.aktivitetsfase = aktivitetsfase;
    }
}
