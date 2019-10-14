package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag;

public class Adressat {
    private AdvokatSomAdressat advokatSomAdressat;
    private OrganisasjonSomAdressat organisasjonSomAdressat;
    private KontaktpersonMedIdNummerSomAdressat kontaktpersonMedIdNummerSomAdressat;
    private KontaktpersonUtenIdNummerSomAdressat kontaktpersonUtenIdNummerSomAdressat;

    public AdvokatSomAdressat getAdvokatSomAdressat() {
        return advokatSomAdressat;
    }

    public void setAdvokatSomAdressat(AdvokatSomAdressat advokatSomAdressat) {
        this.advokatSomAdressat = advokatSomAdressat;
    }

    public OrganisasjonSomAdressat getOrganisasjonSomAdressat() {
        return organisasjonSomAdressat;
    }

    public void setOrganisasjonSomAdressat(OrganisasjonSomAdressat organisasjonSomAdressat) {
        this.organisasjonSomAdressat = organisasjonSomAdressat;
    }

    public KontaktpersonMedIdNummerSomAdressat getKontaktpersonMedIdNummerSomAdressat() {
        return kontaktpersonMedIdNummerSomAdressat;
    }

    public void setKontaktpersonMedIdNummerSomAdressat(KontaktpersonMedIdNummerSomAdressat kontaktpersonMedIdNummerSomAdressat) {
        this.kontaktpersonMedIdNummerSomAdressat = kontaktpersonMedIdNummerSomAdressat;
    }

    public KontaktpersonUtenIdNummerSomAdressat getKontaktpersonUtenIdNummerSomAdressat() {
        return kontaktpersonUtenIdNummerSomAdressat;
    }

    public void setKontaktpersonUtenIdNummerSomAdressat(KontaktpersonUtenIdNummerSomAdressat kontaktpersonUtenIdNummerSomAdressat) {
        this.kontaktpersonUtenIdNummerSomAdressat = kontaktpersonUtenIdNummerSomAdressat;
    }
}
