package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.organisasjon;

public class Organisasjon {

    private String navn;

    public Organisasjon withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public String getNavn() {
        return navn;
    }

}
