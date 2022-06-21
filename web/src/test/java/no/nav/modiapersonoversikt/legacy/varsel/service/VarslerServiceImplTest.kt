package no.nav.modiapersonoversikt.legacy.varsel.service

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import no.nav.modiapersonoversikt.service.varsel.VarslerServiceImpl
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.xml.soap.SOAPFault
import javax.xml.ws.soap.SOAPFaultException

class VarslerServiceImplTest {
    val brukervarselV1 = mockk<BrukervarselV1>()
    val brukernotifikasjonService = mockk<Brukernotifikasjon.Service>()
    val soapfault = mockk<SOAPFault>()

    val varselService: VarslerService = VarslerServiceImpl(brukervarselV1, brukernotifikasjonService)

    @Test
    internal fun `skal ikke tryne hele verden om det skjer soap faults`() {
        every { soapfault.faultString } returns ""
        every { brukervarselV1.hentVarselForBruker(any()) } throws SOAPFaultException(soapfault)
        val varsler = varselService.hentAlleVarsler(Fnr("12345678910"))
        assertThat(varsler).isEmpty()
    }
}
