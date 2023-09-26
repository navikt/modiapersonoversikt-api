package no.nav.modiapersonoversikt.service.soknadsstatus

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.apis.SoknadsstatusControllerApi
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Behandling
import no.nav.modiapersonoversikt.service.soknadsstatus.BehandlingMockUtils.createBehandling
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SoknadsstatusServiceTest {
    private val soknadsstatusApi = mockk<SoknadsstatusControllerApi>()
    private val oboTokenClient: BoundedOnBehalfOfTokenClient = mockk()
    private val service = SoknadsstatusServiceImpl(oboTokenClient, soknadsstatusApi)

    @Test
    fun `tre behandlinger skal gi liste med tre elementer`() {
        val behandlinger = listOf(
            createBehandling(),
            createBehandling().copy(behandlingId = "beha", sakstema = "123456", status = Behandling.Status.UNDER_BEHANDLING),
            createBehandling().copy(behandlingId = "hsfe", sakstema = "4321")
        )

        every { soknadsstatusApi.hentAlleBehandlinger(any(), true) } returns behandlinger

        val basertPaaTema = service.hentBehandlingerGruppertPaaTema("ident")
        assertEquals(3, basertPaaTema.size)
    }
}
