package no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;

public class OppfolgingskontraktResponse implements Serializable {
    private List<SYFOPunkt> syfoPunkter;
    private Bruker bruker;
    private LocalDate vedtaksdato;

    public OppfolgingskontraktResponse() {
    }

    public Bruker getBruker() {
        return bruker;
    }

    public void setBruker(Bruker bruker) {
        this.bruker = bruker;
    }

    public List<SYFOPunkt> getSyfoPunkter() {
        return syfoPunkter;
    }

    public void setSyfoPunkter(List<SYFOPunkt> syfoPunkter) {
        this.syfoPunkter = syfoPunkter;
    }

    public LocalDate getVedtaksdato() {
        return vedtaksdato;
    }

    public void setVedtaksdato(LocalDate vedtaksdato) {
        this.vedtaksdato = vedtaksdato;
    }
}
