package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;

public class Traad {
    public final String traadId, temagruppe, journalfortTema, statusKlasse, statusTekst;
    public final List<Melding> meldinger;
    public final DateTime dato;
    public final int antallMeldingerIOpprinneligTraad;

    public Traad(String traadId, int antallMeldingerIOpprinneligTraad, List<Melding> meldinger) {
        this.traadId = traadId;
        this.antallMeldingerIOpprinneligTraad = antallMeldingerIOpprinneligTraad;
        this.meldinger = on(meldinger).collect(Melding.NYESTE_FORST);
        Melding forsteMelding = this.meldinger.get(this.meldinger.size() - 1);
        Melding sisteMelding = this.meldinger.get(0);
        this.temagruppe = forsteMelding.temagruppeNavn;
        this.journalfortTema = forsteMelding.journalfortTemanavn;
        this.dato = sisteMelding.opprettetDato;
        this.statusKlasse = sisteMelding.statusKlasse;
        this.statusTekst = sisteMelding.statusTekst;
    }

    public static final Transformer<Traad, DateTime> DATO = new Transformer<Traad, DateTime>() {
        @Override
        public DateTime transform(Traad traad) {
            return traad.dato;
        }
    };

    public static final Comparator<Traad> NYESTE_FORST = reverseOrder(compareWith(DATO));

}
