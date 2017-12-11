package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

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
        public String fnr, fornavn, etternavn, navn, navkontor, geografiskTilknytning, diskresjonskode;

        public Bruker(String fnr, String fornavn, String etternavn, String navkontor, String geografiskTilknytning, String diskresjonskode) {
            this.fnr = fnr;
            this.fornavn = namifyString(fornavn);
            this.etternavn = namifyString(etternavn);
            this.navn = kombinerTilFulltNavn(this.fornavn, this.etternavn);
            this.navkontor = navkontor;
            this.geografiskTilknytning = geografiskTilknytning;
            this.diskresjonskode = diskresjonskode;
        }

        public Bruker(String fnr) {
            this.fnr = fnr;
        }

        public Bruker withPersonnavn(String fornavn, String etternavn) {
            this.fornavn = namifyString(fornavn);
            this.etternavn = namifyString(etternavn);
            this.navn = kombinerTilFulltNavn(this.fornavn, this.etternavn);
            return this;
        }

        public Bruker withEnhet(String enhet) {
            this.navkontor = enhet;
            return this;
        }

        public Bruker withGeografiskTilknytning(String geografiskTilhorighet) {
            this.geografiskTilknytning = geografiskTilhorighet;
            return this;
        }

        public Bruker withDiskresjonskode(String diskresjonskode) {
            this.diskresjonskode = diskresjonskode;
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
            this.navn = kombinerTilFulltNavn(this.fornavn, this.etternavn);
        }
    }
}
