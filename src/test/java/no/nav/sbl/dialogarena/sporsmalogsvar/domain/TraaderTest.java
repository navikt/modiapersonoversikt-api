package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraaderTest {

    @Test
    @DisplayName("Slår sammen meldinger med samme tråd-id til en tråd")
    void slarSammenMeldingerTilTrader() {
        Traader traader = new Traader(Arrays.asList(
                new Melding().withId("traad-id-1").withTraadId("traad-id-1"),
                new Melding().withId("id2").withTraadId("traad-id-1")
        ));

        assertEquals(1, traader.getTraader().size());
    }

    @Test
    @DisplayName("Skiller ut meldinger med forskjellige tråd-ider ut i flere tråder")
    void skillerUtMeldingerITraader() {
        Traader traader = new Traader(Arrays.asList(
                new Melding().withId("traad-id-1").withTraadId("traad-id-1"),
                new Melding().withId("traad-id-2").withTraadId("traad-id-2")
        ));

        assertEquals(2, traader.getTraader().size());
    }

    @Test
    @DisplayName("Ingen meldinger gir tomt tråd-objekt")
    void traaderUtenMeldinger() {
        Traader traader = new Traader(Collections.emptyList());

        assertEquals(true, traader.erUtenMeldinger());
    }

}