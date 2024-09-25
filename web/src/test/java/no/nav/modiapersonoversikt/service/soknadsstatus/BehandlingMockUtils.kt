package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Behandling
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Hendelse
import java.time.LocalDateTime
import java.util.*

object BehandlingMockUtils {
    fun createBehandling(): Behandling {
        val behandlingId = "kdhsfke"
        return Behandling(
            behandlingId = behandlingId,
            startTidspunkt = LocalDateTime.parse("2023-06-16T12:40:24"),
            sistOppdatert = LocalDateTime.parse("2023-06-18T12:40:24"),
            sakstema = "AAP",
            behandlingsTema = "AAP",
            behandlingsType = "ae0047",
            status = Behandling.Status.UNDER_BEHANDLING,
            hendelser =
                listOf(
                    createHendelse(behandlingId),
                ),
        )
    }

    fun createHendelse(behandlingId: String = UUID.randomUUID().toString()): Hendelse =
        Hendelse(
            hendelseId = "wjkew12",
            behandlingId = behandlingId,
            hendelseType = Hendelse.HendelseType.OPPRETTET,
            status = Hendelse.Status.UNDER_BEHANDLING,
        )
}
