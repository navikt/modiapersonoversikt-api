package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag;

public class AdvokatSomAdressat {
    private PersonNavn kontaktperson;
    private String organisasjonsnavn;
    private String organisasjonsnummer;

    public PersonNavn getKontaktperson() {
        return kontaktperson;
    }

    public void setKontaktperson(PersonNavn kontaktperson) {
        this.kontaktperson = kontaktperson;
    }

    public String getOrganisasjonsnavn() {
        return organisasjonsnavn;
    }

    public void setOrganisasjonsnavn(String organisasjonsnavn) {
        this.organisasjonsnavn = organisasjonsnavn;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public void setOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }
}
