package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Doedsfall
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Metadata
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.persondata.PersondataFletter
import no.nav.modiapersonoversikt.service.persondata.PersondataResult
import no.nav.personoversikt.common.logging.TjenestekallLogg
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class PersondataFletterTest {
    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    val kodeverk: EnhetligKodeverk.Service = mockk()
    val mapper = PersondataFletter(kodeverk, TjenestekallLogg)
    val fnr = "12345678910"

    @BeforeEach
    internal fun setUp() {
        every { kodeverk.hentKodeverk<String, String>(any()) } returns gittKodeverk()
    }

    @Test
    internal fun `skal mappe data fra pdl til Persondata`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                data =
                    testData.copy(
                        personIdent = fnr,
                        persondata = testPerson,
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            ),
        )
    }

    @Test
    internal fun `skal mappe data fra pdl til Persondata når person er dod`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                data =
                    testData
                        .copy(
                            personIdent = fnr,
                            persondata =
                                testPerson.copy(
                                    doedsfall =
                                        listOf(
                                            Doedsfall(
                                                gittDato("2010-01-02"),
                                                metadata =
                                                    Metadata(
                                                        master = "Freg",
                                                        endringer = emptyList(),
                                                    ),
                                            ),
                                        ),
                                ),
                            geografiskeTilknytning = PersondataResult.NotRelevant(),
                            navEnhet = PersondataResult.NotRelevant(),
                        ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            ),
        )
    }

    @Test
    internal fun `skal filtrere ut egenAnsatt fra feiledeSystemer når veileder ikke har tilgang`() {
        snapshot.assertMatches(
            mapper
                .flettSammenData(
                    data =
                        testData.copy(
                            personIdent = fnr,
                            persondata = testPerson,
                            krrData = PersondataResult.Failure(PersondataResult.InformasjonElement.DKIF, Throwable()),
                            erEgenAnsatt =
                                PersondataResult.Failure(
                                    PersondataResult.InformasjonElement.EGEN_ANSATT,
                                    Throwable(),
                                ),
                            harTilgangTilSkjermetPerson = false,
                        ),
                    clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
                ).feilendeSystemer,
        )
    }

    @Test
    internal fun `skal ikke filtrere ut egenAnsatt fra feiledeSystemer når veileder har tilgang`() {
        snapshot.assertMatches(
            mapper
                .flettSammenData(
                    data =
                        testData.copy(
                            personIdent = fnr,
                            persondata = testPerson,
                            krrData = PersondataResult.Failure(PersondataResult.InformasjonElement.DKIF, Throwable()),
                            erEgenAnsatt =
                                PersondataResult.Failure(
                                    PersondataResult.InformasjonElement.EGEN_ANSATT,
                                    Throwable(),
                                ),
                            harTilgangTilSkjermetPerson = true,
                        ),
                    clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
                ).feilendeSystemer,
        )
    }

    @Test
    internal fun `Publikumsmottak med samme poststed som brukers bostedpoststed skal være først i lista`() {
        val mottak3 =
            NorgDomain.Publikumsmottak(
                besoksadresse =
                    NorgDomain.Gateadresse(
                        gatenavn = "Testgate",
                        husnummer = "1",
                        husbokstav = null,
                        postnummer = "0010",
                        poststed = "Oslo",
                    ),
                apningstider = emptyList(),
            )

        val mottak2 =
            NorgDomain.Publikumsmottak(
                besoksadresse =
                    NorgDomain.Gateadresse(
                        gatenavn = "Bergensgate",
                        husnummer = "2",
                        husbokstav = null,
                        postnummer = "1532",
                        poststed = "Test",
                    ),
                apningstider = emptyList(),
            )

        val mottak1 =
            NorgDomain.Publikumsmottak(
                besoksadresse =
                    NorgDomain.Gateadresse(
                        gatenavn = "Random navn",
                        husnummer = "20",
                        husbokstav = null,
                        postnummer = "1444",
                        poststed = "TestPoststed",
                    ),
                apningstider = emptyList(),
            )

        val enhetKontaktinfo =
            NorgDomain.EnhetKontaktinformasjon(
                enhet =
                    NorgDomain.Enhet(
                        enhetId = "0123",
                        enhetNavn = "NAV Test",
                        oppgavebehandler = true,
                        status = NorgDomain.EnhetStatus.AKTIV,
                    ),
                overordnetEnhet = EnhetId("0001"),
                publikumsmottak = listOf(mottak2, mottak1, mottak3),
            )

        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        navEnhet =
                            PersondataResult.Success(
                                name = PersondataResult.InformasjonElement.NORG_NAVKONTOR,
                                value = enhetKontaktinfo,
                            ),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        snapshot.assertMatches(result)
    }
}
