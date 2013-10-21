package no.nav.sbl.dialogarena.mottaksbehandling;

import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Key;

public interface ISvar {
    Key<String> behandlingsId = new Key<>("behandlingsId");
    Key<String> tema = new Key<>("tema");
    Key<String> saksid = new Key<>("saksid");
    Key<String> fritekst = new Key<>("fritekst");
    Key<Boolean> sensitiv = new Key<>("sensitiv");
}