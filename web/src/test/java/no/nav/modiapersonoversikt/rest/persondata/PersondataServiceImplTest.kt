package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.service.persondata.PersondataResult
import no.nav.modiapersonoversikt.service.persondata.PersondataServiceImpl
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

internal class PersondataServiceImplTest {
    private val ugyldigGT = "0301"
    private val gyldigGT = "030101"

    private val norgApi: NorgApi = mockk()
    private val persondataServiceImpl =
        PersondataServiceImpl(
            norgApi = norgApi,
            pdl = mockk(),
            krrService = mockk(),
            kontonummerService = mockk(),
            skjermedePersonerApi = mockk(),
            oppfolgingService = mockk(),
            policyEnforcementPoint = mockk(),
            kodeverk = mockk(),
            representasjonApi = mockk(),
            pdlFullmakt = mockk(),
            tjenestekallLogger = TjenestekallLogg,
        )

    @Test
    internal fun `skal filtrere vekk ugyldig gt`() {
        val navEnhet =
            persondataServiceImpl.hentNavEnhetFraNorg(
                adressebeskyttelse = emptyList(),
                geografiskeTilknytning = PersondataResult.of(ugyldigGT),
            )

        assertTrue(navEnhet is PersondataResult.NotRelevant<*>)
        verify(exactly = 0) { norgApi.finnNavKontor(any(), any()) }
        verify(exactly = 0) { norgApi.hentKontaktinfo(any()) }
    }

    @Test
    internal fun `skal gi navEnhet`() {
        every { norgApi.finnNavKontor(any(), any())?.enhetId } returns "0123"
        every { norgApi.hentKontaktinfo(EnhetId("0123")) } returns gittNavKontorEnhet()

        val navEnhet =
            persondataServiceImpl.hentNavEnhetFraNorg(
                adressebeskyttelse = emptyList(),
                geografiskeTilknytning = PersondataResult.of(gyldigGT),
            )

        assertTrue(navEnhet is PersondataResult.Success<*>)
        verify(exactly = 1) { norgApi.finnNavKontor(any(), any()) }
        verify(exactly = 1) { norgApi.hentKontaktinfo(any()) }
    }

    @Test
    internal fun `fullmektige fra begge kilder skal slaas opp som tredjeparter uten duplikater`() {
        val pdl = PersondataResult.of(listOf(pdlFullmaktPerson))
        val repr = PersondataResult.of(listOf(fullmaktPerson, fullmaktPerson.copy(fullmektig = "99999999999")))

        assertEquals(listOf("55555666000", "99999999999"), persondataServiceImpl.finnFullmektigIdenter(pdl, repr))
    }

    @Test
    internal fun `repr-fullmektiger skal slaas opp selv om pdl-fullmakt feiler`() {
        val pdl =
            PersondataResult.Failure<List<no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.models.FullmaktDto>>(
                PersondataResult.InformasjonElement.FULLMAKT,
                IllegalStateException("PDL fullmakt nede"),
            )
        val repr = PersondataResult.of(listOf(fullmaktPerson))

        assertEquals(listOf("55555666000"), persondataServiceImpl.finnFullmektigIdenter(pdl, repr))
    }
}
