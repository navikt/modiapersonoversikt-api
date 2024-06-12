package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

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
}
