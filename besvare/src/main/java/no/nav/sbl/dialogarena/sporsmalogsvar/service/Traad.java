package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import java.io.Serializable;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static no.nav.modig.lang.collections.IterUtils.by;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.containedIn;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.option.Optional.optional;

/**
 * En tråd med eksisterende dialog ({@link Melding}er), samt mulighet
 * for å legge inn nye svar ({@link Svar}).
 */
public class Traad implements Serializable {

    public String tema;
    public boolean sensitiv;
    private Svar svar = new Svar();
    private List<Melding> dialog = emptyList();

    public List<Melding> getDialog() {
        return unmodifiableList(dialog);
    }

    public Traad merge(Traad traad) {
        this.leggTil(traad.dialog);
        return this;
    }

    public void leggTil(Melding melding) {
        leggTil(optional(melding));
    }

    public void leggTil(Iterable<Melding> meldinger) {
        Iterable<Melding> nyeMeldinger = on(meldinger).filter(not(containedIn(dialog)));
        this.dialog = on(dialog).append(nyeMeldinger).collect(by(Melding.SENDT_DATO).descending());
    }

    public Melding getSisteMelding() {
        return on(dialog).head().orNull();
    }

    public List<Melding> getTidligereDialog() {
        return on(dialog).tail().collect();
    }

    public void ferdigSvar() {
        leggTil(svar.getFerdigSvar());
        this.sensitiv = svar.sensitiv;
        svar = new Svar();
    }
}
