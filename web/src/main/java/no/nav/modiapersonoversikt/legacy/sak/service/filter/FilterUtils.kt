package no.nav.modiapersonoversikt.legacy.sak.service.filter

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

object FilterUtils {
    const val OPPRETTET = "opprettet"
    const val AVBRUTT = "avbrutt"
    const val AVSLUTTET = "avsluttet"
    const val SEND_SOKNAD_KVITTERINGSTYPE = "ae0002"
    const val DOKUMENTINNSENDING_KVITTERINGSTYPE = "ae0001"
    const val BEHANDLINGSTATUS_AVSLUTTET = "avsluttet"

    private val LOGGER = LoggerFactory.getLogger(FilterUtils::class.java)

    @JvmStatic
    fun behandlingsDato(wsBehandlingskjede: Behandlingskjede): DateTime {
        val erAvsluttet = erAvsluttet(wsBehandlingskjede)
        val calendar = if (erAvsluttet) {
            wsBehandlingskjede.slutt ?: wsBehandlingskjede.sisteBehandlingsoppdatering
        } else {
            wsBehandlingskjede.start
        }

        return DateTime(calendar.toGregorianCalendar().time)
    }

    @JvmStatic
    fun erKvitteringstype(type: String): Boolean {
        return SEND_SOKNAD_KVITTERINGSTYPE == type || DOKUMENTINNSENDING_KVITTERINGSTYPE == type
    }

    @JvmStatic
    fun erAvsluttet(kjede: Behandlingskjede): Boolean {
        val erAvsluttet = kjede.sisteBehandlingsstatus?.value == BEHANDLINGSTATUS_AVSLUTTET
        if (erAvsluttet && kjede.slutt == null) {
            LOGGER.warn(
                """
                Inkonsistent data fra sak og behandling: Behandling rapporteres som avsluttet uten at kjede har slutt-tid satt.
                Bruker `sisteBehandlingsoppdatering` som fallback.
                BehandlingsId: ${kjede.sisteBehandlingREF}
                BehandlinsgkjedeId: ${kjede.behandlingskjedeId}
                """.trimIndent()
            )
        }
        return erAvsluttet
    }
}
