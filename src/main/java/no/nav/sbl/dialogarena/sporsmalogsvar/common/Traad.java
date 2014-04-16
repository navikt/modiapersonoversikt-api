package no.nav.sbl.dialogarena.sporsmalogsvar.common;

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
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.common.melding.Meldingstype.UTGAENDE;

/**
 * En tråd med eksisterende dialog ({@link Melding}er), samt mulighet
 * for å legge inn nye svar ({@link Svar}).
 */
public class Traad implements Serializable {

    public boolean erSensitiv;

    private String tema;

    private Svar svar = new Svar();
    private List<Melding> dialog = emptyList();
    public Traad(String tema, String svarBehandlingId) {
        this.tema = tema;
        this.svar = new Svar();
        this.svar.behandlingId = svarBehandlingId;
        this.svar.tema = tema;
    }


    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
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


    public static class Svar implements Serializable {
        public String behandlingId,
                      tema,
                      fritekst;
    }

    public void ferdigSvar() {
        tema = svar.tema;
        Melding nyMelding = new Melding(svar.behandlingId, UTGAENDE, DateTime.now(), svar.fritekst);
        leggTil(nyMelding);
        svar = new Svar();
        svar.tema = tema;
    }


}
