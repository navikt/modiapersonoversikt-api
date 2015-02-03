package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

public interface Aktoer {
    public Key<String> aktoerId = new Key<>("AKTOER_ID");
    public Key<String> navn = new Key<>("NAVN");
}
