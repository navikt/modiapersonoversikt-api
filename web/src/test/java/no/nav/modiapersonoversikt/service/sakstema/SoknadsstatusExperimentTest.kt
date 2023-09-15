package no.nav.modiapersonoversikt.service.sakstema

import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.soknadsstatus.Soknadsstatus
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SoknadsstatusExperimentTest {

    @Test
    fun `skal finne ut at svar fra SoB og soknadsstatus er lik`() {
        val tema = listOf("SAK", "SAK1", "SAK2")
        val behandlingskjede = createBehandlingskjede(tema)
        val soknadsstatus = createSoknadsstatus(tema)
        val res = Experiment.compareSakogbehandlingAndSoknadsstatus(behandlingskjede, soknadsstatus)
        assertTrue(res)
    }

    @Test
    fun `skal finne ut at eksperiment har et mer tema enn kontroll`() {
        val tema = mutableListOf("SAK", "SAK1", "SAK2")
        val behandlingskjede = createBehandlingskjede(tema)
        tema.add("SAK3")
        val soknadsstatus = createSoknadsstatus(tema)
        val res = Experiment.compareSakogbehandlingAndSoknadsstatus(behandlingskjede, soknadsstatus)
        assertFalse(res)
    }

    @Test
    fun `skal finne ut at kontroll har et mer tema enn eksperiment`() {
        val tema = mutableListOf("SAK", "SAK1", "SAK2")
        val soknadsstatus = createSoknadsstatus(tema)
        tema.add("SAK3")
        val behandlingskjede = createBehandlingskjede(tema)
        val res = Experiment.compareSakogbehandlingAndSoknadsstatus(behandlingskjede, soknadsstatus)
        assertFalse(res)
    }

    @Test
    fun `skal finne ut at kontroll har ulik status på et tema`() {
        val tema = mutableListOf("SAK", "SAK1", "SAK2")
        val behandlingskjede = createBehandlingskjede(tema).toMutableMap()
        behandlingskjede["SAK1"] = listOf(
            Behandlingskjede().withStatus(BehandlingsStatus.AVBRUTT).withSistOppdatert(
                LocalDateTime.now()
            )
        )
        val soknadsstatus = createSoknadsstatus(tema)
        val res = Experiment.compareSakogbehandlingAndSoknadsstatus(behandlingskjede, soknadsstatus)
        assertFalse(res)
    }

    @Test
    fun `skal finne ut at eksperiment har ulik status på et tema`() {
        val tema = mutableListOf("SAK", "SAK1", "SAK2")
        val behandlingskjede = createBehandlingskjede(tema)
        val soknadsstatus = createSoknadsstatus(tema).toMutableMap()
        soknadsstatus["SAK"] = Soknadsstatus(avbrutt = 1)
        val res = Experiment.compareSakogbehandlingAndSoknadsstatus(behandlingskjede, soknadsstatus)
        assertFalse(res)
    }

    @Test
    fun `skal reportere ulik når eksperiment er null`() {
        val tema = mutableListOf("SAK", "SAK1", "SAK2")
        val behandlingskjede = createBehandlingskjede(tema)
        val res = Experiment.compareSakogbehandlingAndSoknadsstatus(behandlingskjede, null)
        assertFalse(res)
    }

    private fun createSoknadsstatus(saksTema: List<String>): Map<String, Soknadsstatus> {
        return saksTema.fold(mutableMapOf()) { map, tema ->
            map[tema] = Soknadsstatus(underBehandling = 1, ferdigBehandlet = 1, avbrutt = 1)
            map
        }
    }

    private fun createBehandlingskjede(saksTema: List<String>): Map<String, List<Behandlingskjede>> {
        return saksTema.fold(mutableMapOf()) { map, tema ->
            map[tema] = listOf(
                Behandlingskjede().withStatus(BehandlingsStatus.FERDIG_BEHANDLET).withSistOppdatert(LocalDateTime.now()),
                Behandlingskjede().withStatus(BehandlingsStatus.UNDER_BEHANDLING).withSistOppdatert(LocalDateTime.now()),
                Behandlingskjede().withStatus(BehandlingsStatus.AVBRUTT).withSistOppdatert(LocalDateTime.now())
            )
            map
        }
    }
}
