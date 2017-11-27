package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain;

import java.util.ArrayList;
import java.util.List;

public class Apningstider {

    private final List<Apningstid> apningstider = new ArrayList<>();

    public List<Apningstid> getApningstider() {
        return this.apningstider;
    }

    public Apningstider withApningstid(List<Apningstid> apningstider) {
        this.apningstider.clear();
        this.apningstider.addAll(apningstider);
        return this;
    }
}
