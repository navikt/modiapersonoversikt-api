package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

@SuppressWarnings("all")
public interface Konto {
    Key<String> kontonummer = new Key<>("KONTONUMMER");
    Key<String> kontotype = new Key<>("KONTOTYPE");
}
