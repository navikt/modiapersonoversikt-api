package no.nav.modiapersonoversikt.rest.person

import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.KjoennType
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentidenter.Identliste
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.HentPersonBolkResult
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Matrikkeladresse
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Person
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Telefonnummer
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.PolicyWithAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollInstance
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.*
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Bostedsadresse as PdlBostedsadresse
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.Kjoenn as PdlKjoenn

/**
 * PersonResponseMapper og PdlKriterierMapper tester enkeltdeler av
 * PersonsokController. PersonsokControllerV4Test tester selve controlleren
 * ende-til-ende med en fake PdlOppslagService og en tilgangskontroll som
 * alltid tillater kallet.
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

    @Nested
    inner class PersonsokControllerV4Test {
        private val minimalPersonSearchHit =
            PersonSearchHit(
                score = 1.0,
                person =
                    Person(
                        navn = listOf(Navn(fornavn = "Fornavn", mellomnavn = null, etternavn = "Etternavn", originaltNavn = null)),
                        kjoenn = emptyList(),
                        utenlandskIdentifikasjonsnummer = emptyList(),
                        folkeregisteridentifikator =
                            listOf(Folkeregisteridentifikator(identifikasjonsnummer = "12345678910", status = "AKTIV", type = "FNR")),
                        kontaktadresse = emptyList(),
                        bostedsadresse = emptyList(),
                        telefonnummer = emptyList(),
                    ),
            )

        private val requestV4 =
            PersonsokRequestV3(
                enhet = "0219",
                navn = null,
                fornavn = null,
                etternavn = null,
                utenlandskID = null,
                alderFra = null,
                alderTil = null,
                fodselsdatoFra = "1990-01-01",
                fodselsdatoTil = "1990-01-31",
                kjonn = null,
                adresse = null,
                telefonnummer = null,
            )

        private fun lagController(sokPersonFn: (List<PdlKriterie>, Int, Int) -> PdlOppslagService.PdlSokResultat) =
            PersonsokController(
                pdlOppslagService = FakePdlOppslagService(sokPersonFn),
                tilgangskontroll = AllowAllTilgangskontroll,
            )

        @Test
        internal fun `v4 returnerer treff og paginerings-metadata fra pdl`() {
            var mottattPageNumber: Int? = null
            var mottattResultsPerPage: Int? = null
            val controller =
                lagController { _, pageNumber, resultsPerPage ->
                    mottattPageNumber = pageNumber
                    mottattResultsPerPage = resultsPerPage
                    PdlOppslagService.PdlSokResultat(
                        hits = listOf(minimalPersonSearchHit),
                        pageNumber = 2,
                        totalHits = 120,
                        totalPages = 3,
                    )
                }

            val respons = controller.sokPdlV4(requestV4.copy(pageNumber = 2, resultsPerPage = 50))

            assertThat(respons.treff).hasSize(1)
            assertThat(respons.pageNumber).isEqualTo(2)
            assertThat(respons.totalHits).isEqualTo(120)
            assertThat(respons.totalPages).isEqualTo(3)
            assertThat(mottattPageNumber).isEqualTo(2)
            assertThat(mottattResultsPerPage).isEqualTo(50)
        }

        @Test
        internal fun `v4 avviser pageNumber mindre enn 1`() {
            val controller = lagController { _, _, _ -> throw AssertionError("skal ikke kalle pdl ved ugyldig input") }

            val ex =
                assertThrows(ResponseStatusException::class.java) {
                    controller.sokPdlV4(requestV4.copy(pageNumber = 0))
                }
            assertThat(ex.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }

        @Test
        internal fun `v4 avviser resultsPerPage mindre enn 1`() {
            val controller = lagController { _, _, _ -> throw AssertionError("skal ikke kalle pdl ved ugyldig input") }

            val ex =
                assertThrows(ResponseStatusException::class.java) {
                    controller.sokPdlV4(requestV4.copy(resultsPerPage = 0))
                }
            assertThat(ex.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }

        @Test
        internal fun `v4 avviser resultsPerPage over pdl sitt maks`() {
            val controller = lagController { _, _, _ -> throw AssertionError("skal ikke kalle pdl ved ugyldig input") }

            val ex =
                assertThrows(ResponseStatusException::class.java) {
                    controller.sokPdlV4(requestV4.copy(resultsPerPage = PdlOppslagService.MAKS_RESULTATER_PER_SIDE + 1))
                }
            assertThat(ex.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }

        @Test
        internal fun `v3 ignorerer paginering fra klienten og bruker faste verdier`() {
            var mottattPageNumber: Int? = null
            var mottattResultsPerPage: Int? = null
            val controller =
                lagController { _, pageNumber, resultsPerPage ->
                    mottattPageNumber = pageNumber
                    mottattResultsPerPage = resultsPerPage
                    PdlOppslagService.PdlSokResultat(hits = emptyList(), pageNumber = 1, totalHits = 0, totalPages = 0)
                }

            controller.sokPdlV3(requestV4.copy(pageNumber = 5, resultsPerPage = 90))

            assertThat(mottattPageNumber).isEqualTo(1)
            assertThat(mottattResultsPerPage).isEqualTo(30)
        }
    }
}

private class FakePdlOppslagService(
    private val sokPersonFn: (List<PdlKriterie>, Int, Int) -> PdlOppslagService.PdlSokResultat,
) : PdlOppslagService {
    override fun sokPerson(
        kriterier: List<PdlKriterie>,
        pageNumber: Int,
        resultsPerPage: Int,
    ): PdlOppslagService.PdlSokResultat = sokPersonFn(kriterier, pageNumber, resultsPerPage)

    override fun hentPersondata(fnr: String): HentPersondata.Result? = null

    override fun hentTredjepartspersondata(fnrs: List<String>): List<HentPersonBolkResult> = emptyList()

    override fun hentGeografiskTilknyttning(fnr: String): String? = null

    override fun hentIdenter(fnr: String): Identliste? = null

    override fun hentFolkeregisterIdenter(fnr: String): Identliste? = null

    override fun hentAktorId(fnr: String): String? = null

    override fun hentFnr(aktorid: String): String? = null
}

private object AllowAllTilgangskontroll : Tilgangskontroll {
    private val instance =
        object : TilgangskontrollInstance {
            override fun <S> get(
                audit: Audit.AuditDescriptor<in S>,
                block: () -> S,
            ): S = block()

            override fun getDecision(): Decision = Decision.Permit()

            override fun check(policy: PolicyWithAttributes): TilgangskontrollInstance = this
        }

    override fun check(policy: PolicyWithAttributes): TilgangskontrollInstance = instance
}
