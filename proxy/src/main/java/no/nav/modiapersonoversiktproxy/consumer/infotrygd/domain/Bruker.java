package no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain;

import java.io.Serializable;

public class Bruker implements Serializable {
    private String ident;

    public Bruker() {
    }

    public Bruker(String personId) {
        this.ident = personId;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }
}
