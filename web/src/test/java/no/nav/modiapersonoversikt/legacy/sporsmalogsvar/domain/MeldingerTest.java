package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.domain;

import no.nav.modiapersonoversikt.api.domain.Person;
import no.nav.modiapersonoversikt.api.domain.henvendelse.Fritekst;
import no.nav.modiapersonoversikt.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.api.domain.henvendelse.Meldingstype;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import org.joda.time.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeldingerTest {

    public static final String TRAAD_ID_1 = "traad-id-1";

    @Test
    @DisplayName("Slår sammen meldinger med samme tråd-id til en tråd")
    void slarSammenMeldingerTilTrader() {
        Meldinger meldinger = new Meldinger(Arrays.asList(
                new Melding()
                        .withId(TRAAD_ID_1)
                        .withTraadId(TRAAD_ID_1)
                        .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                        .withFerdigstiltDato(DateTime.now()),
                new Melding()
                        .withId("id2")
                        .withTraadId(TRAAD_ID_1)
                        .withType(Meldingstype.SVAR_SKRIFTLIG)
                        .withFerdigstiltDato(DateTime.now())
        ));

        assertEquals(1, meldinger.getTraader().size());
    }

    @Test
    @DisplayName("Skiller ut meldinger med forskjellige tråd-ider ut i flere tråder")
    void skillerUtMeldingerITraader() {
        Meldinger meldinger = new Meldinger(Arrays.asList(
                new Melding()
                        .withId(TRAAD_ID_1)
                        .withTraadId(TRAAD_ID_1)
                        .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                        .withFerdigstiltDato(DateTime.now()),
                new Melding()
                        .withId("traad-id-2")
                        .withTraadId("traad-id-2")
                        .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                        .withFerdigstiltDato(DateTime.now())
        ));

        assertEquals(2, meldinger.getTraader().size());
    }

    @Test
    @DisplayName("Ingen meldinger gir tomt tråd-objekt")
    void traaderUtenMeldinger() {
        Meldinger meldinger = new Meldinger(Collections.emptyList());

        assertEquals(true, meldinger.erUtenMeldinger());
    }

    @Nested()
    class DelsvarTest {

        @Test
        @DisplayName("Slår sammen 3 meldinger, hvorav en er et delsvar, til 2 meldinger")
        void slarSammenIkkeAvsluttedeDelsvar() {
            Meldinger meldinger = new Meldinger(mockMeldingskjedeMedDelsvar());

            assertEquals(2, meldinger.getTraad(TRAAD_ID_1).get().getMeldinger().size());
        }

        @Test
        @DisplayName("Slår sammen tråd hvor det er et delsvar fulgt av to svar til 3 meldinger")
        void slarSammenMedToSvar() {
            Meldinger meldinger = new Meldinger(mockMeldingskjedeMedDelsvarOgToSvar());

            assertEquals(3, meldinger.getTraad(TRAAD_ID_1).get().getMeldinger().size());
        }

        @Test
        @DisplayName("Slår friteksten fra delsvaret inn i det avsluttende svaret")
        void slarSammenFritekst() {
            Meldinger meldinger = new Meldinger(mockMeldingskjedeMedDelsvar());

            Melding avsluttendeSvar = getAvsluttendeSvar(meldinger);
            assertEquals(2, avsluttendeSvar.getFriteksterMedEldsteForst().size());
        }

        private List<Melding> mockMeldingskjedeMedDelsvar() {
            return Arrays.asList(
               new Melding()
                       .withId(TRAAD_ID_1)
                       .withTraadId(TRAAD_ID_1)
                       .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                       .withFerdigstiltDato(DateTime.now()),
               new Melding()
                       .withId("id2")
                       .withTraadId(TRAAD_ID_1)
                       .withType(Meldingstype.DELVIS_SVAR_SKRIFTLIG)
                       .withFerdigstiltDato(DateTime.now())
                       .withFritekst(new Fritekst("Jeg svarer med et delsvar!", new Person("John", "Johnsen"), DateTime.now())),
               new Melding()
                       .withId("id3")
                       .withTraadId(TRAAD_ID_1)
                       .withType(Meldingstype.SVAR_SKRIFTLIG)
                       .withFerdigstiltDato(DateTime.now())
                       .withFritekst(new Fritekst("Jeg svarer med et endelig svar!", new Person("Anne", "Saksbehandler"), DateTime.now()))
            );
        }

        private List<Melding> mockMeldingskjedeMedDelsvarOgToSvar() {
            return Arrays.asList(
                    new Melding()
                            .withId(TRAAD_ID_1)
                            .withTraadId(TRAAD_ID_1)
                            .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                            .withFerdigstiltDato(DateTime.now()),
                    new Melding()
                            .withId("id2")
                            .withTraadId(TRAAD_ID_1)
                            .withType(Meldingstype.DELVIS_SVAR_SKRIFTLIG)
                            .withFerdigstiltDato(DateTime.now())
                            .withFritekst(new Fritekst("Jeg svarer med et delsvar!", new Person("John", "Johnsen"), DateTime.now())),
                    new Melding()
                            .withId("id3")
                            .withTraadId(TRAAD_ID_1)
                            .withType(Meldingstype.SVAR_SKRIFTLIG)
                            .withFerdigstiltDato(DateTime.now())
                            .withFritekst(new Fritekst("Jeg svarer med et endelig svar!", new Person("Anne", "Saksbehandler"), DateTime.now())),
                    new Melding()
                            .withId("id4")
                            .withTraadId(TRAAD_ID_1)
                            .withType(Meldingstype.SVAR_SKRIFTLIG)
                            .withFerdigstiltDato(DateTime.now())
                            .withFritekst(new Fritekst("Jeg svarer med enda et endelig svar!", new Person("Anne", "Saksbehandler"), DateTime.now()))
            );
        }

        private Melding getAvsluttendeSvar(Meldinger traader) {
            return traader.getTraad(TRAAD_ID_1).get().getMeldinger().stream()
                    .filter(Melding::erSvarSkriftlig)
                    .findFirst()
                    .get();
        }

    }

}