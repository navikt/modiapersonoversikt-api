package no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<Apningstid> getApningstid(Ukedag ukedag) {
        return apningstider.stream().filter(apningstid -> apningstid.getUkedag() == ukedag).findFirst();
    }
}
