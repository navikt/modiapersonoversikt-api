package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.predicate.AdresseUtils;

public class UstrukturertAdresse extends Adresselinje {
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String adresselinje4;

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

    @Override
    public String getAdresselinje() {
        return AdresseUtils.spaceAppend(adresselinje1, adresselinje2, adresselinje3, adresselinje4);
    }
}
