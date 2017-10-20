package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.kjerneinfo.domain.person.Personnavn;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.Serializable;

import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

public class GrunnInfo implements Serializable {
    public Bruker bruker;
    public Saksbehandler saksbehandler;

    public GrunnInfo(Bruker bruker, Saksbehandler saksbehandler) {
        this.bruker = bruker;
        this.saksbehandler = saksbehandler;
    }

    private static String kombinerTilFulltNavn(String fornavn, String etternavn) {
        return String.format("%s %s", fornavn, etternavn);
    }

    private static String namifyString(String navn) {
        return capitalizeFully(navn, ' ', '-', '\'');
    }

    public static class Bruker implements Serializable {
        public String fnr, fornavn, etternavn, navn, navkontor;

        public Bruker(String fnr, String fornavn, String etternavn, String navkontor) {
            this.fnr = fnr;
            this.fornavn = namifyString(fornavn);
            this.etternavn = namifyString(etternavn);
            this.navn = kombinerTilFulltNavn(this.fornavn, this.etternavn);
            this.navkontor = navkontor;
        }

        public Bruker(String fnr) {
            this.fnr = fnr;
        }

        public Bruker withPersonnavn(Personnavn personnavn) {
            this.fornavn = namifyString(personnavn.getFornavn());
            this.etternavn = namifyString(personnavn.getEtternavn());
            this.navn = kombinerTilFulltNavn(this.fornavn, this.etternavn);
            return this;
        }

        public Bruker withEnhet(String enhet) {
            this.navkontor = enhet;
            return this;
        }

        public String getFornavnAsEcmaScriptString(){
            return StringEscapeUtils.escapeEcmaScript(this.fornavn);
        }
    }

    public static class Saksbehandler implements Serializable {
        public String enhet, fornavn, etternavn, navn;

        public Saksbehandler(String enhet, String fornavn, String etternavn) {
            this.enhet = enhet;
            this.fornavn = namifyString(fornavn);
            this.etternavn = namifyString(etternavn);
            this.navn = kombinerTilFulltNavn(this.fornavn, this.etternavn);
        }
    }
}
