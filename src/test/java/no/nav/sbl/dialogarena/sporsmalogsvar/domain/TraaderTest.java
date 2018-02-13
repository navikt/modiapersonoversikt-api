package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import org.joda.time.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraaderTest {

    public static final String TRAAD_ID_1 = "traad-id-1";

    @Test
    @DisplayName("Slår sammen meldinger med samme tråd-id til en tråd")
    void slarSammenMeldingerTilTrader() {
        Traader traader = new Traader(Arrays.asList(
                new Melding()
                        .withId(TRAAD_ID_1)
                        .withTraadId(TRAAD_ID_1)
                        .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                        .withOpprettetDato(DateTime.now()),
                new Melding()
                        .withId("id2")
                        .withTraadId(TRAAD_ID_1)
                        .withType(Meldingstype.SVAR_SKRIFTLIG)
                        .withOpprettetDato(DateTime.now())
        ));

        assertEquals( 1, traader.getTraader().size());
    }

    @Test
    @DisplayName("Skiller ut meldinger med forskjellige tråd-ider ut i flere tråder")
    void skillerUtMeldingerITraader() {
        Traader traader = new Traader(Arrays.asList(
                new Melding()
                        .withId(TRAAD_ID_1)
                        .withTraadId(TRAAD_ID_1)
                        .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                        .withOpprettetDato(DateTime.now()),
                new Melding()
                        .withId("traad-id-2")
                        .withTraadId("traad-id-2")
                        .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                        .withOpprettetDato(DateTime.now())
        ));

        assertEquals( 2, traader.getTraader().size());
    }

    @Test
    @DisplayName("Ingen meldinger gir tom traader")
    void traaderUtenMeldinger() {
        Traader traader = new Traader(Collections.emptyList());

        assertEquals( true, traader.erUtenMeldinger());
    }

    @Nested()
    class DelsvarTest {

        @Test
        @DisplayName("Slår sammen 3 meldinger, hvorav en er et delsvar, til 2 meldinger")
        void slarSammenIkkeAvsluttedeDelsvar() {
            Traader traader = new Traader(mockMeldingskjedeMedDelsvar());

            assertEquals(2, traader.getTraader().get(TRAAD_ID_1).size());
        }

        @Test
        @DisplayName("Slår sammen tråd hvor det er et delsvar fulgt av to svar til 3 meldinger")
        void slarSammenMedToSvar() {
            Traader traader = new Traader(mockMeldingskjedeMedDelsvarOgToSvar());

            assertEquals(3, traader.getTraader().get(TRAAD_ID_1).size());
        }

        @Test
        @DisplayName("Slår friteksten fra delsvaret inn i det avsluttende svaret")
        void slarSammenFritekst() {
            Traader traader = new Traader(mockMeldingskjedeMedDelsvar());

            Melding avsluttendeSvar = getAvsluttendeSvar(traader);
            assertEquals(2, avsluttendeSvar.getFriteksterMedEldsteForst().size());
        }

        private List<Melding> mockMeldingskjedeMedDelsvar() {
            return Arrays.asList(
               new Melding()
                       .withId(TRAAD_ID_1)
                       .withTraadId(TRAAD_ID_1)
                       .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                       .withOpprettetDato(DateTime.now()),
               new Melding()
                       .withId("id2")
                       .withTraadId(TRAAD_ID_1)
                       .withType(Meldingstype.DELVIS_SVAR_SKRIFTLIG)
                       .withOpprettetDato(DateTime.now())
                       .withFritekst(new Fritekst("Jeg svarer med et delsvar!", new Person("John", "Johnsen"), DateTime.now())),
               new Melding()
                       .withId("id3")
                       .withTraadId(TRAAD_ID_1)
                       .withType(Meldingstype.SVAR_SKRIFTLIG)
                       .withOpprettetDato(DateTime.now())
                       .withFritekst(new Fritekst("Jeg svarer med et endelig svar!", new Person("Anne", "Saksbehandler"), DateTime.now()))
            );
        }

        private List<Melding> mockMeldingskjedeMedDelsvarOgToSvar() {
            return Arrays.asList(
                    new Melding()
                            .withId(TRAAD_ID_1)
                            .withTraadId(TRAAD_ID_1)
                            .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                            .withOpprettetDato(DateTime.now()),
                    new Melding()
                            .withId("id2")
                            .withTraadId(TRAAD_ID_1)
                            .withType(Meldingstype.DELVIS_SVAR_SKRIFTLIG)
                            .withOpprettetDato(DateTime.now())
                            .withFritekst(new Fritekst("Jeg svarer med et delsvar!", new Person("John", "Johnsen"), DateTime.now())),
                    new Melding()
                            .withId("id3")
                            .withTraadId(TRAAD_ID_1)
                            .withType(Meldingstype.SVAR_SKRIFTLIG)
                            .withOpprettetDato(DateTime.now())
                            .withFritekst(new Fritekst("Jeg svarer med et endelig svar!", new Person("Anne", "Saksbehandler"), DateTime.now())),
                    new Melding()
                            .withId("id4")
                            .withTraadId(TRAAD_ID_1)
                            .withType(Meldingstype.SVAR_SKRIFTLIG)
                            .withOpprettetDato(DateTime.now())
                            .withFritekst(new Fritekst("Jeg svarer med enda et endelig svar!", new Person("Anne", "Saksbehandler"), DateTime.now()))
            );
        }

        private Melding getAvsluttendeSvar(Traader traader) {
            return traader.getTraader().get(TRAAD_ID_1).stream()
                    .filter(Melding::erSvarSkriftlig)
                    .findFirst()
                    .get();
        }

    }

}