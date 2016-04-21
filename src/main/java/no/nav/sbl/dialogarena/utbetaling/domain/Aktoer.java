package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

@SuppressWarnings("all")
public interface Aktoer {
    public enum AktoerType {
        PERSON, SAMHANDLER, ORGANISASJON
    }

    Key<AktoerType> aktoerType = new Key<>("AKTOER_TYPE");
    Key<String> aktoerId = new Key<>("AKTOER_ID");
    Key<String> navn = new Key<>("NAVN");

    Key<String> diskresjonskode = new Key<>("DISKRESJONSKODE");
}
