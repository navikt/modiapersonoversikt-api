package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag;

public class KontaktpersonUtenIdNummerSomAdressat {
    private String foedselsdato;
    private PersonNavn navn;

    public String getFoedselsdato() {
        return foedselsdato;
    }

    public void setFoedselsdato(String foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public PersonNavn getNavn() {
        return navn;
    }

    public void setNavn(PersonNavn navn) {
        this.navn = navn;
    }
}
