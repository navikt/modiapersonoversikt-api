package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.reverseOrder;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;

public class Traad {
    public final String traadId, temagruppe, journalfortTema, statusKlasse, meldingStatus;
    public final List<Melding> meldinger;
    public final DateTime dato;

    public Traad(String traadId, Melding... meldinger) {
        this(traadId, asList(meldinger));
    }

    public Traad(String traadId, List<Melding> meldinger) {
        this.traadId = traadId;
        this.meldinger = on(meldinger).collect(Melding.NYESTE_FORST);
        Melding forsteMelding = this.meldinger.get(this.meldinger.size() - 1);
        Melding sisteMelding = this.meldinger.get(0);
        this.temagruppe = forsteMelding.temagruppe;
        this.journalfortTema = forsteMelding.journalfortTema;
        this.dato = sisteMelding.opprettetDato;
        this.statusKlasse = VisningUtils.lagStatusIkonKlasse(meldinger.get(0));
        this.meldingStatus = VisningUtils.lagMeldingStatusTekstKey(meldinger.get(0));
    }

    public static final Transformer<Traad, DateTime> DATO = new Transformer<Traad, DateTime>() {
        @Override
        public DateTime transform(Traad traad) {
            return traad.dato;
        }
    };

    public static final Comparator<Traad> NYESTE_FORST = reverseOrder(compareWith(DATO));

}
