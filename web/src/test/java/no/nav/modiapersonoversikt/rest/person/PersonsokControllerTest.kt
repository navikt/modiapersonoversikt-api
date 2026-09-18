package no.nav.modiapersonoversikt.rest.person

import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.KjoennType
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Matrikkeladresse
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Person
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Telefonnummer
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.*
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Bostedsadresse as PdlBostedsadresse
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Kjoenn as PdlKjoenn

/**
 * Doesn't actually test PersonsokController – too much going on there to
 * sensibly fake all the dependencies. Does test a few of its components.
 */
class PersonsokControllerTest {
    @Nested
    inner class PersonResponseMapper {
        @JvmField
        @RegisterExtension
        val snapshot = SnapshotExtension()

        @Test
        internal fun `should map pdl response`() {
            val person =
                PersonSearchHit(
                    score = 1.0,
                    person =
                        Person(
                            navn =
                                listOf(
                                    Navn(
                                        fornavn = "fornavn",
                                        mellomnavn = "mellomnavn",
                                        etternavn = "etternavn",
                                        originaltNavn = null,
                                    ),
                                ),
                            kjoenn =
                                listOf(
                                    PdlKjoenn(
                                        KjoennType.KVINNE,
                                    ),
                                ),
                            utenlandskIdentifikasjonsnummer =
                                listOf(
                                    UtenlandskIdentifikasjonsnummer(
                                        identifikasjonsnummer = "987654-987",
                                        utstederland = "SWE",
                                        opphoert = false,
                                    ),
                                ),
                            folkeregisteridentifikator =
                                listOf(
                                    Folkeregisteridentifikator(
                                        identifikasjonsnummer = "12345678910",
                                        status = "AKTIV",
                                        type = "FNR",
                                    ),
                                ),
                            kontaktadresse =
                                listOf(
                                    Kontaktadresse(
                                        vegadresse =
                                            Vegadresse(
                                                husbokstav = "Z",
                                                husnummer = "10",
                                                bruksenhetsnummer = null,
                                                adressenavn = "Supervegen",
                                                kommunenummer = "654321",
                                                postnummer = "1234",
                                                bydelsnummer = null,
                                                tilleggsnavn = null,
                                            ),
                                        postboksadresse = null,
                                        postadresseIFrittFormat = null,
                                        utenlandskAdresse = null,
                                        utenlandskAdresseIFrittFormat = null,
                                    ),
                                ),
                            bostedsadresse =
                                listOf(
                                    PdlBostedsadresse(
                                        matrikkeladresse =
                                            Matrikkeladresse(
                                                bruksenhetsnummer = "123101",
                                                tilleggsnavn = "Supergården",
                                                postnummer = "1234",
                                                kommunenummer = "654321",
                                            ),
                                        vegadresse = null,
                                        utenlandskAdresse = null,
                                        ukjentBosted = null,
                                    ),
                                ),
                            telefonnummer =
                                listOf(
                                    Telefonnummer(
                                        nummer = "000000",
                                        landskode = "47",
                                    ),
                                ),
                        ),
                )

            snapshot.assertMatches(lagPersonResponse(person))
        }
    }

    @Nested
    inner class PdlKriterierMapper {
        private val clock: Clock =
            Clock.fixed(
                Instant.parse("2020-12-02T12:00:00.00Z"),
                ZoneId.systemDefault(),
            )

        @Test
        internal fun `regner ut tidligste dato basert på alderFra`() {
            val kriterier =
                requestV3
                    .copy(alderFra = 30)
                    .tilPdlKriterier(clock)

            assertThat(kriterier).contains(PdlKriterie(PdlFelt.FODSELSDATO_TIL, "1990-12-02", searchHistorical = PdlSokeOmfang.GJELDENDE))
        }

        @Test
        internal fun `regner ut seneste dato basert på alderTil`() {
            val kriterier =
                requestV3
                    .copy(alderTil = 32)
                    .tilPdlKriterier(clock)

            assertThat(kriterier).contains(PdlKriterie(PdlFelt.FODSELSDATO_FRA, "1987-12-03", searchHistorical = PdlSokeOmfang.GJELDENDE))
        }

        @Test
        internal fun `mapper kjønn til pdl-format`() {
            val mann =
                requestV3
                    .copy(kjonn = "M")
                    .tilPdlKriterier(clock)

            assertThat(mann).contains(PdlKriterie(PdlFelt.KJONN, "MANN", searchHistorical = PdlSokeOmfang.GJELDENDE))

            val kvinne =
                requestV3
                    .copy(kjonn = "K")
                    .tilPdlKriterier(clock)

            assertThat(kvinne).contains(PdlKriterie(PdlFelt.KJONN, "KVINNE", searchHistorical = PdlSokeOmfang.GJELDENDE))

            val ukjent =
                requestV3
                    .copy(kjonn = "U")
                    .tilPdlKriterier(clock)

            assertThat(ukjent).contains(PdlKriterie(PdlFelt.KJONN, null, searchHistorical = PdlSokeOmfang.GJELDENDE))
        }

        @Test
        internal fun `mapper adresse til pdl-format`() {
            val kriterier =
                requestV3
                    .copy(
                        adresse = "Gatenavn 1 A 0100",
                    ).tilPdlKriterier(clock)

            assertThat(kriterier).contains(PdlKriterie(PdlFelt.ADRESSE, "Gatenavn 1 A 0100", searchHistorical = PdlSokeOmfang.GJELDENDE))
        }

        @Test
        internal fun `mapper navn til pdl-format`() {
            val kriterier =
                requestV3
                    .copy(
                        navn = "Fornavn Etternavn",
                    ).tilPdlKriterier(clock)

            assertThat(
                kriterier,
            ).contains(PdlKriterie(PdlFelt.NAVN, "Fornavn Etternavn", searchHistorical = PdlSokeOmfang.HISTORISK_OG_GJELDENDE))
        }

        @Test
        internal fun `mapper fornavn til pdl-format`() {
            val kriterier =
                requestV3
                    .copy(fornavn = "Fornavn")
                    .tilPdlKriterier(clock)

            assertThat(
                kriterier,
            ).contains(PdlKriterie(PdlFelt.FORNAVN, "Fornavn", searchHistorical = PdlSokeOmfang.HISTORISK_OG_GJELDENDE))
            assertThat(kriterier.filter { it.felt == PdlFelt.ETTERNAVN }).isEmpty()
        }

        @Test
        internal fun `mapper etternavn til pdl-format`() {
            val kriterier =
                requestV3
                    .copy(etternavn = "Etternavn")
                    .tilPdlKriterier(clock)

            assertThat(
                kriterier,
            ).contains(PdlKriterie(PdlFelt.ETTERNAVN, "Etternavn", searchHistorical = PdlSokeOmfang.HISTORISK_OG_GJELDENDE))
            assertThat(kriterier.filter { it.felt == PdlFelt.FORNAVN }).isEmpty()
        }

        @Test
        internal fun `mapper både fornavn og etternavn til to kriterier`() {
            val kriterier =
                requestV3
                    .copy(fornavn = "Fornavn", etternavn = "Etternavn")
                    .tilPdlKriterier(clock)

            assertThat(kriterier).contains(
                PdlKriterie(PdlFelt.FORNAVN, "Fornavn", searchHistorical = PdlSokeOmfang.HISTORISK_OG_GJELDENDE),
                PdlKriterie(PdlFelt.ETTERNAVN, "Etternavn", searchHistorical = PdlSokeOmfang.HISTORISK_OG_GJELDENDE),
            )
        }

        @Test
        internal fun `tomme navnefelt gir ingen navnekriterier`() {
            val kriterier =
                requestV3
                    .copy(fornavn = "", etternavn = "   ")
                    .tilPdlKriterier(clock)

            assertThat(kriterier.filter { it.felt == PdlFelt.FORNAVN || it.felt == PdlFelt.ETTERNAVN }).isEmpty()
        }

        private val requestV3 =
            PersonsokRequestV3(
                enhet = null,
                navn = null,
                fornavn = null,
                etternavn = null,
                utenlandskID = null,
                alderFra = null,
                alderTil = null,
                fodselsdatoFra = null,
                fodselsdatoTil = null,
                kjonn = null,
                adresse = null,
                telefonnummer = null,
            )
    }
}
