package no.nav.sbl.dialogarena.mottaksbehandling;

import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Key;

// CHECKSTYLE:OFF
public interface ISvar {
    Key<String> behandlingsId = new Key<>("behandlingsId");
    Key<Tema> tema = new Key<>("tema");
    Key<String> saksid = new Key<>("saksid");
    Key<String> fritekst = new Key<>("fritekst");
    Key<Boolean> sensitiv = new Key<>("sensitiv");
}
// CHECKSTYLE:ON