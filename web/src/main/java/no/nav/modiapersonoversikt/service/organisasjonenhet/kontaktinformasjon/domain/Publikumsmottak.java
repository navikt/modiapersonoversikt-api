package no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain;

public class Publikumsmottak {

    private Gateadresse besoeksadresse;
    private Apningstider apningstider;

    public Gateadresse getBesoeksadresse() {
        return besoeksadresse;
    }

    public Publikumsmottak withBesoeksadresse(Gateadresse besoeksadresse) {
        this.besoeksadresse = besoeksadresse;
        return this;
    }

    public Apningstider getApningstider() {
        return apningstider;
    }

    public Publikumsmottak withApningstider(Apningstider apningstider) {
        this.apningstider = apningstider;
        return this;
    }
}
