package no.nav.kjerneinfo.domain.person;

public class Stedsadresse extends Adresselinje {

    private String adressestring;

    @Override
    public String getAdresselinje() {
        return adressestring;
    }

    public void setAdressestring(String adressestring) {
        this.adressestring = adressestring;
    }
}
