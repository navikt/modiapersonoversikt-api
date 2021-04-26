package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain;

public class Apningstid {

    private Ukedag ukedag;
    private Klokkeslett apentFra;
    private Klokkeslett apentTil;

    public Ukedag getUkedag() {
        return ukedag;
    }

    public Apningstid withUkedag(Ukedag ukedag) {
        this.ukedag = ukedag;
        return this;
    }

    public Klokkeslett getApentFra() {
        return apentFra;
    }

    public Apningstid withApentFra(Klokkeslett apentFra) {
        this.apentFra = apentFra;
        return this;
    }

    public Klokkeslett getApentTil() {
        return apentTil;
    }

    public Apningstid withApentTil(Klokkeslett apentTil) {
        this.apentTil = apentTil;
        return this;
    }
}
