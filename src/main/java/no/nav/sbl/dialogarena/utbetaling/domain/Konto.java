package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

public interface Konto {
    public Key<String> kontonummer = new Key<>("KONTONUMMER");
    public Key<String> kontotype = new Key<>("KONTOTYPE");
}
