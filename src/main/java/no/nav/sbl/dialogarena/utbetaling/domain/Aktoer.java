package no.nav.sbl.dialogarena.utbetaling.domain;

@SuppressWarnings("all")
public class Aktoer {
    public enum AktoerType {
        PERSON, SAMHANDLER, ORGANISASJON
    }

    AktoerType aktoerType;
    String aktoerId;
    String navn;

    String diskresjonskode;

    public AktoerType getAktoerType() {
        return aktoerType;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public String getNavn() {
        return navn;
    }

    public String getDiskresjonskode() {
        return diskresjonskode;
    }

}
