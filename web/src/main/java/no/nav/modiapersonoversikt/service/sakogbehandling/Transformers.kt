package no.nav.modiapersonoversikt.service.sakogbehandling

import no.nav.modiapersonoversikt.service.sakogbehandling.FilterUtils.behandlingsDato
import no.nav.modiapersonoversikt.service.sakogbehandling.FilterUtils.erKvitteringstype
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandling
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsType
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Behandlingstyper
import org.joda.time.DateTime

object Transformers {
    @JvmStatic
    fun tilBehandling(wsBehandlingskjede: Behandlingskjede): Behandling {
        var behandling =
            Behandling()
                .withBehandlingsType(wsBehandlingskjede.sisteBehandlingstype.value)
                .withBehandlingsDato(behandlingsDato(wsBehandlingskjede))
                .withOpprettetDato(DateTime(wsBehandlingskjede.start.toGregorianCalendar().time))
                .withPrefix(wsBehandlingskjede.sisteBehandlingREF.substring(0, 2))
                .withBehandlingsId(wsBehandlingskjede.sisteBehandlingREF)
                .withBehandlingStatus(behandlingsStatus(wsBehandlingskjede))
                .withBehandlingKvittering(kvitteringstype(wsBehandlingskjede.sisteBehandlingstype))
        val behandlingstema = wsBehandlingskjede.behandlingstema
        if (behandlingstema != null) {
            behandling = behandling.withBehandlingsTema(behandlingstema.value)
        }
        return behandling
    }

    private fun kvitteringstype(sisteBehandlingstype: Behandlingstyper): BehandlingsType {
        return if (erKvitteringstype(sisteBehandlingstype.value)) BehandlingsType.KVITTERING else BehandlingsType.BEHANDLING
    }

    private fun behandlingsStatus(wsBehandlingskjede: Behandlingskjede): BehandlingsStatus {
        if (wsBehandlingskjede.sisteBehandlingsstatus != null) {
            return when (wsBehandlingskjede.sisteBehandlingsstatus.value) {
                FilterUtils.AVSLUTTET -> BehandlingsStatus.FERDIG_BEHANDLET
                FilterUtils.OPPRETTET -> BehandlingsStatus.UNDER_BEHANDLING
                FilterUtils.AVBRUTT -> BehandlingsStatus.AVBRUTT
                else -> throw RuntimeException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.sisteBehandlingsstatus.value)
            }
        }
        throw RuntimeException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.sisteBehandlingsstatus.value)
    }
}
