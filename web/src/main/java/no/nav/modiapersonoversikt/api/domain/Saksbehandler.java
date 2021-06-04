package no.nav.modiapersonoversikt.api.domain;

public class Saksbehandler extends Person {

    private final String navIdent;

    public Saksbehandler(String fornavn, String etternavn, String navIdent) {
        super(fornavn, etternavn, false);
        this.navIdent = navIdent;
    }

    public String getIdent() {
        return navIdent;
    }

}
