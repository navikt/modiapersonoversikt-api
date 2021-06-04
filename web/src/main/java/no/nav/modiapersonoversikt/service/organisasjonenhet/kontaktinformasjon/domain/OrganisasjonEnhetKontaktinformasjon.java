package no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain;

public class OrganisasjonEnhetKontaktinformasjon {

    private String enhetId;
    private Kontaktinformasjon kontaktinformasjon;
    private String enhetNavn;

    public String getEnhetId() {
        return enhetId;
    }

    public OrganisasjonEnhetKontaktinformasjon withEnhetId(String enhetId){
        this.enhetId = enhetId;
        return this;
    }

    public Kontaktinformasjon getKontaktinformasjon() {
        return kontaktinformasjon;
    }

    public OrganisasjonEnhetKontaktinformasjon withKontaktinformasjon(Kontaktinformasjon kontaktinformasjon) {
        this.kontaktinformasjon = kontaktinformasjon;
        return this;
    }

    public String getEnhetNavn() {
        return enhetNavn;
    }

    public OrganisasjonEnhetKontaktinformasjon withEnhetNavn(String enhetNavn) {
        this.enhetNavn = enhetNavn;
        return this;
    }
}
