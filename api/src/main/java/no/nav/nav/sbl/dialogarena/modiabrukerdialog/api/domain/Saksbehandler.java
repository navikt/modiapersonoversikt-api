package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

public class Saksbehandler extends Person {

    private final String navIdent;

    public Saksbehandler(String fornavn, String etternavn, String navIdent) {
        super(fornavn, etternavn);
        this.navIdent = navIdent;
    }

    public String getIdent() {
        return navIdent;
    }

}
