package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag;

public class PersonNavn {
    private String fornavn;
    private String mellomnavn;
    private String etternavn;

    public PersonNavn() {}

    public PersonNavn(String fornavn, String mellomnavn, String etternavn) {
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
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
}
