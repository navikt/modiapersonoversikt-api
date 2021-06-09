package no.nav.personsok.domain;

import no.nav.personsok.domain.enums.Diskresjonskode;
import no.nav.personsok.domain.enums.PersonstatusType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Person implements Serializable {

    private String fodselsnummer;

    private String fornavn;

    private String mellomnavn;

    private String etternavn;

    private String sammensattNavn;

    private List<Adresse> adresser;

    private String kommunenr;

    private Diskresjonskode diskresjonskodePerson;

    private String bankkontoNorge;

    private Kodeverkstype personstatus;

    public Person() {
    }

    public Person(String fodselsnummer, String fornavn, String mellomnavn, String etternavn,
            List<Adresse> adresser, String kommunenr) {
        this.fodselsnummer = fodselsnummer;
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
        this.adresser = adresser;
        this.kommunenr = kommunenr;
    }

    public String getNavn() {

        String navn = "";
        if (isNotBlank(etternavn)) {
            navn = etternavn + ", " + fornavn + (mellomnavn != null ? " " + mellomnavn : "");
        } else if (isNotBlank(sammensattNavn)) {
            navn = sammensattNavn;
        }
        return navn;
    }

    public String getFodselsnummer() {
        return fodselsnummer;
    }

    public String getFodselsdato() {
        return fodselsnummer == null || fodselsnummer.isEmpty() ? ""
                : fodselsnummer.substring(4, 6) + fodselsnummer.substring(3, 5) + fodselsnummer.substring(0, 2);
    }

    public void setFodselsnummer(String fodselsnummer) {
        this.fodselsnummer = fodselsnummer;
    }

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public String getMellomnavn() {
        return mellomnavn;
    }

    public void setMellomnavn(String mellomnavn) {
        this.mellomnavn = mellomnavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public void setEtternavn(String etternavn) {
        this.etternavn = etternavn;
    }

    public List<Adresse> getAdresser() {
        if (adresser == null) {
            adresser = new ArrayList<>();
        }
        return adresser;
    }

    public void setAdresser(List<Adresse> adresser) {
        this.adresser = adresser;
    }

    public String getKommunenr() {
        return kommunenr;
    }

    public void setKommunenr(String kommunenr) {
        this.kommunenr = kommunenr;
    }

    public Diskresjonskode getDiskresjonskodePerson() {
        return diskresjonskodePerson;
    }

    public void setDiskresjonskodePerson(Diskresjonskode diskresjonskodePerson) {
        this.diskresjonskodePerson = diskresjonskodePerson;
    }

    public String getBankkontoNorge() {
        return bankkontoNorge;
    }

    public void setBankkontoNorge(String bankkontoNorge) {
        this.bankkontoNorge = bankkontoNorge;
    }

    public Kodeverkstype getPersonstatus() {
        return personstatus;
    }

    public void setPersonstatus(Kodeverkstype personstatus) {
        this.personstatus = personstatus;
    }

    public String getPersonstatusTegn() {
        if (personstatus != null && personstatus.getKode() != null && personstatus.getKode().matches(PersonstatusType.DOED.toString())) {
            return "(død)";
        }
        return null;
    }

    public String getSammensattNavn() {
        return sammensattNavn;
    }

    public void setSammensattNavn(String sammensattNavn) {
        this.sammensattNavn = sammensattNavn;
    }
}
