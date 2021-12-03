package no.nav.modiapersonoversikt.rest.person

import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService.PdlSokbareFelt
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService.SokKriterier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class PersonsokControllerTest {
    @Nested
    inner class PdlKriterierMapper {
        val clock = Clock.fixed(
            Instant.parse("2020-12-02T12:00:00.00Z"),
            ZoneId.systemDefault()
        )

        @Test
        internal fun `samler navne-felt til ett felt`() {
            val kriterier = request
                .copy(fornavn = "Fornavn", etternavn = "Etternavn")
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(SokKriterier(PdlSokbareFelt.NAVN, "Fornavn Etternavn"))
        }

        @Test
        internal fun `filtrerer bort navne-felt som er null`() {
            val bareFornavn = request
                .copy(fornavn = "Fornavn")
                .tilPdlKriterier(clock)
            val bareEtternavn = request
                .copy(etternavn = "Etternavn")
                .tilPdlKriterier(clock)

            assertThat(bareFornavn).contains(SokKriterier(PdlSokbareFelt.NAVN, "Fornavn"))
            assertThat(bareEtternavn).contains(SokKriterier(PdlSokbareFelt.NAVN, "Etternavn"))
        }

        @Test
        internal fun `samler adresse-felt til ett felt`() {
            val kriterier = request
                .copy(
                    gatenavn = "Gatenavn",
                    husnummer = 1,
                    husbokstav = "A",
                    postnummer = "0100"
                )
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(SokKriterier(PdlSokbareFelt.ADRESSE, "Gatenavn 1 A 0100"))
        }

        @Test
        internal fun `regner ut tidligste dato basert på alderFra`() {
            val kriterier = request
                .copy(alderFra = 30)
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(SokKriterier(PdlSokbareFelt.FODSELSDATO_TIL, "1990-12-02"))
        }

        @Test
        internal fun `regner ut seneste dato basert på alderTil`() {
            val kriterier = request
                .copy(alderTil = 32)
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(SokKriterier(PdlSokbareFelt.FODSELSDATO_FRA, "1987-12-03"))
        }

        @Test
        internal fun `mapper kjønn til pdl-format`() {
            val mann = request
                .copy(kjonn = "M")
                .tilPdlKriterier(clock)

            assertThat(mann).contains(SokKriterier(PdlSokbareFelt.KJONN, "MANN"))

            val kvinne = request
                .copy(kjonn = "K")
                .tilPdlKriterier(clock)

            assertThat(kvinne).contains(SokKriterier(PdlSokbareFelt.KJONN, "KVINNE"))

            val ukjent = request
                .copy(kjonn = "U")
                .tilPdlKriterier(clock)

            assertThat(ukjent).contains(SokKriterier(PdlSokbareFelt.KJONN, null))
        }

        val request = PersonsokRequest(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
}
