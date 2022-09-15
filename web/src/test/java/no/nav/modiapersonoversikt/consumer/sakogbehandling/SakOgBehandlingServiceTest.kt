package no.nav.modiapersonoversikt.consumer.sakogbehandling

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.AktorId
import no.nav.modiapersonoversikt.legacy.sak.mock.MockCreationUtil
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SakOgBehandlingServiceTest {
    val sakOgBehandling: SakOgBehandlingV1 = mockk()
    val pdlOppslagService: PdlOppslagService = mockk()
    val sakOgBehandlingService = SakOgBehandlingServiceImpl(sakOgBehandling, pdlOppslagService)

    @Test
    internal fun `tre ws-saker skal gi liste med tre elementer`() {
        val saker = listOf(
            MockCreationUtil.createWSSak(),
            MockCreationUtil.createWSSak(),
            MockCreationUtil.createWSSak(),
        )

        every { sakOgBehandling.finnSakOgBehandlingskjedeListe(any()) } returns FinnSakOgBehandlingskjedeListeResponse()
            .apply { this.sak.addAll(saker) }

        Assertions.assertThat(sakOgBehandlingService.hentAlleSaker(AktorId("12345678910"))).hasSize(3)
    }
}
