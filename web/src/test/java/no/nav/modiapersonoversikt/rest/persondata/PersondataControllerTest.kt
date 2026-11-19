package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.PolicyWithAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollInstance
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import no.nav.modiapersonoversikt.service.persondata.Persondata
import no.nav.modiapersonoversikt.service.persondata.PersondataService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

internal class PersondataControllerTest {
    private val service = mockk<PersondataService>()
    private val tilgang = mockk<TilgangskontrollInstance>()
    private val controller = PersondataController(service, tilgang, mockk())
    private val request = FnrRequest("00000000000")
    private val result = mockk<Persondata.Data>()

    @Test
    fun `sjekker hovedpersonens tilgang for oppdrag og returnerer tillatt respons`() {
        val policy = slot<PolicyWithAttributes>()
        every { tilgang.check(capture(policy)) } returns tilgang
        every { tilgang.get(any<Audit.AuditDescriptor<Persondata.Data>>(), any()) } answers {
            secondArg<() -> Persondata.Data>()()
        }
        every { service.hentPerson(request.fnr) } returns result

        assertEquals(result, controller.hentPersondata(request))
        val forventet = Policies.tilgangTilBruker(Fnr(request.fnr))
        assertEquals(forventet.policy, policy.captured.policy)
        assertEquals(
            request.fnr,
            policy.captured.attributes
                .single()
                .provide(mockk())
                .toString(),
        )
        verify(exactly = 1) { service.hentPerson(request.fnr) }
    }

    @Test
    fun `avslag for hovedpersonen starter ikke oppslag av relasjoner`() {
        every { tilgang.check(any()) } returns tilgang
        every { tilgang.get(any<Audit.AuditDescriptor<Persondata.Data>>(), any()) } throws
            ResponseStatusException(HttpStatus.FORBIDDEN)

        assertThrows(ResponseStatusException::class.java) { controller.hentPersondata(request) }
        verify(exactly = 0) { service.hentPerson(any()) }
    }
}
