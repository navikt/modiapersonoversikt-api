package no.nav.sbl.dialogarena.mottaksbehandling.sak;

import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Key;
import org.joda.time.DateTime;

public interface ISak {
    Key<String> sakId = new Key<>("sakId");
    Key<DateTime> opprettetDato = new Key<>("opprettetDato");
    Key<Boolean> generell = new Key<>("generell");
    Key<String> status = new Key<>("status");
}
