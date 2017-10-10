package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

public class GrunnInfo implements Serializable {

    public Bruker bruker;
    public Saksbehandler saksbehandler;

    public GrunnInfo(Bruker bruker, Saksbehandler saksbehandler) {
        this.bruker = bruker;
        this.saksbehandler = saksbehandler;
    }

    public static class Bruker implements Serializable {
        public String fnr, fornavn, etternavn, navn, navkontor;

        public Bruker(String fnr, String fornavn, String etternavn, String navkontor) {
            this.fnr = fnr;
            this.fornavn = namifyString(fornavn);
            this.etternavn = namifyString(etternavn);
            this.navn = String.format("%s %s", this.fornavn, this.etternavn);
            this.navkontor = navkontor;
        }

        public Bruker(String fnr) {
            this.fnr = fnr;
        }

        public Bruker withPersonnavn(String fornavn, String etternavn) {
            this.fornavn = namifyString(fornavn);
            this.etternavn = namifyString(etternavn);
            this.navn = String.format("%s %s", this.fornavn, this.etternavn);
            return this;
        }

        public Bruker withEnhet(String enhet) {
            this.navkontor = enhet;
            return this;
        }
    }

    public static class SaksbehandlerNavn implements Serializable {
        public String fornavn, etternavn, navn;

        public SaksbehandlerNavn(String fornavn, String etternavn) {
            this.fornavn = namifyString(fornavn);
            this.etternavn = namifyString(etternavn);
            this.navn = String.format("%s %s", this.fornavn, this.etternavn);
        }
    }

    public static class Saksbehandler implements Serializable {
        public String enhet, fornavn, etternavn, navn;

        public Saksbehandler(String enhet, String fornavn, String etternavn) {
            this.enhet = enhet;
            this.fornavn = namifyString(fornavn);
            this.etternavn = namifyString(etternavn);
            this.navn = String.format("%s %s", this.fornavn, this.etternavn);
        }
    }

    private static String namifyString(String navn) {
        return capitalize(lowerCase(navn));
    }

}
