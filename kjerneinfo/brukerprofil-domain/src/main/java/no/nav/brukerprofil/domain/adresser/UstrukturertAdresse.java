package no.nav.brukerprofil.domain.adresser;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.common.domain.Periode;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class UstrukturertAdresse implements Serializable {

    private Kodeverdi landkode;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String adresselinje4;
    private Periode postleveringsPeriode;
    private LocalDateTime endringstidspunkt;
    private String endretAv;

    public UstrukturertAdresse() {
    }

    public UstrukturertAdresse(Kodeverdi landkode, String adresselinje1, String adresselinje2, String adresselinje3, String adresselinje4) {
		this.landkode = landkode;
        this.adresselinje1 = adresselinje1;
        this.adresselinje2 = adresselinje2;
        this.adresselinje3 = adresselinje3;
        this.adresselinje4 = adresselinje4;
    }

    public String getAdresselinje1() {
        return adresselinje1;
    }

    public void setAdresselinje1(String adresselinje1) {
        this.adresselinje1 = adresselinje1;
    }

    public String getAdresselinje2() {
        return adresselinje2;
    }

    public void setAdresselinje2(String adresselinje2) {
        this.adresselinje2 = adresselinje2;
    }

    public String getAdresselinje3() {
        return adresselinje3;
    }

    public void setAdresselinje3(String adresselinje3) {
        this.adresselinje3 = adresselinje3;
    }

    public String getAdresselinje4() {
        return adresselinje4;
    }

    public void setAdresselinje4(String adresselinje4) {
        this.adresselinje4 = adresselinje4;
    }

    public Kodeverdi getLandkode() {
        return landkode;
    }

    public void setLandkode(Kodeverdi landkode) {
        this.landkode = landkode;
    }

    public Periode getPostleveringsPeriode() {
        return postleveringsPeriode;
    }

    public void setPostleveringsPeriode(Periode value) {
        this.postleveringsPeriode = value;
    }

    public LocalDateTime getEndringstidspunkt() {
        return endringstidspunkt;
    }

    public void setEndringstidspunkt(LocalDateTime value) {
        this.endringstidspunkt = value;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public void setEndretAv(String value) {
        this.endretAv = value;
    }
}
