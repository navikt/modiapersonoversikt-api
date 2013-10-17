package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import org.joda.time.DateTime;

import java.io.Serializable;
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

    private String tema;
    private boolean sensitiv;
    private Svar svar = new Svar();
    private List<Melding> dialog = emptyList();


    public Traad(String tema) {
        this.tema = tema;
        this.svar = new Svar();
        this.svar.tema = tema;
    }

    public boolean erSensitiv() {
        return sensitiv;
    }

    public String getTema() {
        return tema;
    }

    public List<Melding> getDialog() {
        return unmodifiableList(dialog);
    }

    public Melding getSisteMelding() {
        return on(dialog).head().orNull();
    }

    public List<Melding> getTidligereDialog() {
        return on(dialog).tail().collect();
    }

    public Svar getSvar() {
        return svar;
    }



    public void leggTil(Melding melding) {
        leggTil(optional(melding));
    }

    public void leggTil(Iterable<Melding> meldinger) {
        Iterable<Melding> nyeMeldinger = on(meldinger).filter(not(containedIn(dialog)));
        this.dialog = on(dialog).append(nyeMeldinger).collect(by(Melding.SENDT_DATO).descending());
    }





    /**
     * Mutable dataobjekt som brukes ved innlegging av svar fra saksbehandler.
     * Når svaret er ferdig kan {@link #getFerdigSvar()} kalles for å få et
     * immutable {@link Melding}-objekt for inkludering i dialogen i tråden.
     */
    public static class Svar implements Serializable {
        public boolean sensitiv;
        public String behandlingId,
                      tema,
                      fritekst;
    }

    public void ferdigSvar() {
        sensitiv = svar.sensitiv;
        tema = svar.tema;
        Melding nyMelding = new Melding(svar.behandlingId, UTGAENDE, DateTime.now(), svar.fritekst);
        nyMelding.tidligereHenvendelse.setObject(false);
        leggTil(nyMelding);
        svar = new Svar();
    }


}
