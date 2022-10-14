package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.personoversikt.test.snapshot.SnapshotExtension
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
    val mapper = PersondataFletter(kodeverk)
    val fnr = "12345678910"

    @BeforeEach
    internal fun setUp() {
        every { kodeverk.hentKodeverk<String, String>(any()) } returns gittKodeverk()
    }

    @Test
    internal fun `skal mappe data fra pdl til Persondata`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                data = testData.copy(
                    personIdent = fnr,
                    persondata = testPerson,
                ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault())
            )
        )
    }

    @Test
    internal fun `skal mappe data fra pdl til Persondata når person er dod`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                data = testData
                    .copy(
                        personIdent = fnr,
                        persondata = testPerson.copy(doedsfall = listOf(HentPersondata.Doedsfall(gittDato("2010-01-02")))),
                        geografiskeTilknytning = PersondataResult.NotRelevant(),
                        navEnhet = PersondataResult.NotRelevant(),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault())
            )
        )
    }

    @Test
    internal fun `skal filtrere ut egenAnsatt fra feiledeSystemer når veileder ikke har tilgang`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                data = testData.copy(
                    personIdent = fnr,
                    persondata = testPerson,
                    dkifData = PersondataResult.Failure(PersondataResult.InformasjonElement.DKIF, Throwable()),
                    erEgenAnsatt = PersondataResult.Failure(
                        PersondataResult.InformasjonElement.EGEN_ANSATT,
                        Throwable()
                    ),
                    harTilgangTilSkjermetPerson = false
                ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault())
            ).feilendeSystemer
        )
    }

    @Test
    internal fun `skal ikke filtrere ut egenAnsatt fra feiledeSystemer når veileder har tilgang`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                data = testData.copy(
                    personIdent = fnr,
                    persondata = testPerson,
                    dkifData = PersondataResult.Failure(PersondataResult.InformasjonElement.DKIF, Throwable()),
                    erEgenAnsatt = PersondataResult.Failure(
                        PersondataResult.InformasjonElement.EGEN_ANSATT,
                        Throwable()
                    ),
                    harTilgangTilSkjermetPerson = true
                ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault())
            ).feilendeSystemer
        )
    }
}
