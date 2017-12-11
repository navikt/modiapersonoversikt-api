package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain;

public class Gateadresse {
    private String gatenavn;
    private String husnummer;
    private String husbokstav;

    public String getGatenavn() {
        return gatenavn;
    }

    public Gateadresse withGatenavn(String gatenavn) {
        this.gatenavn = gatenavn;
        return this;
    }

    public String getHusnummer() {
        return husnummer;
    }

    public String getHusbokstav() {
        return husbokstav;
    }

    public Gateadresse withHusnummer(String husnummer) {
        this.husnummer = husnummer;
        return this;
    }

    public Gateadresse withHusbokstav(String husbokstav) {
        this.husbokstav = husbokstav;
        return this;
    }
}
