package no.nav.modiapersonoversikt.api.domain.oppfolgingsinfo.rest;

public class Oppfolgingsenhet {
    private String navn;
    private String enhetId;

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public void setEnhetId(String enhetId) {
        this.enhetId = enhetId;
    }
}
