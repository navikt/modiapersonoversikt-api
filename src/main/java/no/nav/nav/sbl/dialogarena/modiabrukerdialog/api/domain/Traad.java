package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding.NYESTE_FORST;

public class Traad {
    public final String traadId, temagruppe, journalfortTema;
    public final List<Melding> meldinger;

    public Traad(String traadId, Melding... meldinger) {
        this(traadId, asList(meldinger));
    }

    public Traad(String traadId, List<Melding> meldinger) {
        this.traadId = traadId;
        this.meldinger = on(meldinger).collect(NYESTE_FORST);
        Melding forsteMelding = this.meldinger.get(this.meldinger.size() - 1);
        this.temagruppe = forsteMelding.temagruppe;
        this.journalfortTema = forsteMelding.journalfortTema;
    }

}
