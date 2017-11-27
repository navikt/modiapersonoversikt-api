package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain;

import java.time.LocalDateTime;

public class Apningstid {

    private Ukedag ukedag;
    private Klokkeslett apentFra;

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
}
