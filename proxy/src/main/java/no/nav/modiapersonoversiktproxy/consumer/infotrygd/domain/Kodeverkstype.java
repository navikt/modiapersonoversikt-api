package no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Kodeverkstype implements Serializable {
    private String kode;
    private String termnavn;

    public Kodeverkstype() {
    }

    public Kodeverkstype(String kode, String termnavn) {
        this.kode = kode;
        this.termnavn = termnavn;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getTermnavn() {
        return isBlank(termnavn) ? kode : termnavn;
    }

    public void setTermnavn(String termnavn) {
        this.termnavn = termnavn;
    }

    @Override
    public String toString() {
        return getTermnavn();
    }
}
