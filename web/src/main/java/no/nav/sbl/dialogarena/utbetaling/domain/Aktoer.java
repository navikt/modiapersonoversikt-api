package no.nav.sbl.dialogarena.utbetaling.domain;

import java.io.Serializable;

@SuppressWarnings("all")
public class Aktoer implements Serializable {
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


    public Aktoer withAktoerId(String aktoerId) {
        this.aktoerId = aktoerId;
        return this;
    }


    public Aktoer withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public Aktoer withAktoerType(AktoerType aktoerType) {
        this.aktoerType = aktoerType;
        return this;
    }


    public Aktoer withDiskresjonskode(String diskresjonskode) {
        this.diskresjonskode = diskresjonskode;
        return this;
    }


}
