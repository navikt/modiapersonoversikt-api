package no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain;

import java.io.Serializable;
import java.util.List;

public class YtelseskontraktResponse implements Serializable {

    private List<Ytelse> ytelser;
    private String rettighetsgruppe;

    public YtelseskontraktResponse() {
    }

    public YtelseskontraktResponse(List<Ytelse> ytelser) {
        this.ytelser = ytelser;
    }

    public List<Ytelse> getYtelser() {
        return ytelser;
    }

    public void setYtelser(List<Ytelse> ytelser) {
        this.ytelser = ytelser;
    }

    public String getRettighetsgruppe() {
        return rettighetsgruppe;
    }

    public void setRettighetsgruppe(String rettighetsgruppe) {
        this.rettighetsgruppe = rettighetsgruppe;
    }
}
