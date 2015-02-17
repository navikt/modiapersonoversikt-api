package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import java.io.Serializable;

public class GrunnInfo implements Serializable {

    public Bruker bruker;
    public Saksbehandler saksbehandler;

    public GrunnInfo(Bruker bruker, Saksbehandler saksbehandler) {
        this.bruker = bruker;
        this.saksbehandler = saksbehandler;
    }

    public static class Bruker implements Serializable {
        public static final String FALLBACK_FORNAVN = "bruker";

        public String fnr, fornavn, etternavn;

        public Bruker(String fnr, String fornavn, String etternavn) {
            this.fnr = fnr;
            this.fornavn = fornavn;
            this.etternavn = etternavn;
        }
    }

    public static class Saksbehandler implements Serializable {
        public String ident, enhet, navn;

        public Saksbehandler(String ident, String enhet, String navn) {
            this.ident = ident;
            this.enhet = enhet;
            this.navn = navn;
        }
    }

}
