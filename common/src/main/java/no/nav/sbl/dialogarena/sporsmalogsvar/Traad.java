package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static no.nav.modig.lang.collections.IterUtils.by;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.containedIn;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;

/**
 * En tråd med eksisterende dialog ({@link Melding}er), samt mulighet
 * for å legge inn nye svar ({@link Svar}).
 */
public class Traad implements Serializable {

    public boolean erSensitiv;
    private Tema tema;
    private Svar svar = new Svar();
    private List<Melding> dialog = emptyList();


    public Traad(Tema tema, String svarBehandlingId) {
        this.tema = tema;
        this.svar = new Svar();
        this.svar.behandlingId = svarBehandlingId;
        this.svar.tema = tema;
    }

    public Tema getTema() {
        return tema;
    }

    public List<Melding> getDialog() {
        return unmodifiableList(dialog);
    }

    public int getAntallMeldinger() {
        return dialog.size();
    }

    public Melding getSisteMelding() {
        return on(dialog).head().orNull();
    }

    public List<Melding> getTidligereDialog() {
        return dialog.size() == 1 ? Collections.<Melding>emptyList() : on(dialog).tail().collect();
    }

    public Svar getSvar() {
        return svar;
    }

    public void leggTil(Melding melding) {
        leggTil(optional(melding));
    }

    public void leggTil(Iterable<Melding> meldinger) {
        Iterable<Melding> nyeMeldinger = on(meldinger).filter(not(containedIn(dialog)));
        dialog = on(dialog).append(nyeMeldinger).collect(by(Melding.SENDT_DATO).descending());
    }

    @Override
    public String toString() {
        boolean tomDialog = dialog.isEmpty();
        return (tomDialog ? "tom " : "") +
               (erSensitiv ? "sensitiv tråd" : "tråd") +
               " ang. " + tema +
               (tomDialog ? "" : ". " + dialog);
    }



    /**
     * Mutable dataobjekt som brukes ved innlegging av svar fra saksbehandler.
     * Når svaret er ferdig kan {@link #ferdigSvar()} kalles for å få et
     * immutable {@link Melding}-objekt for inkludering i dialogen i tråden.
     */
    public static class Svar implements Serializable {
        public String behandlingId,
                      fritekst;
        public Tema tema;
    }

    public void ferdigSvar() {
        tema = svar.tema;
        Melding nyMelding = new Melding(svar.behandlingId, UTGAENDE, DateTime.now(), svar.fritekst);
        leggTil(nyMelding);
        svar = new Svar();
        svar.tema = tema;
    }


}
