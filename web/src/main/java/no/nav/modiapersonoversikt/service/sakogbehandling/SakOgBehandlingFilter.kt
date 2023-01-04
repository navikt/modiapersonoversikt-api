package no.nav.modiapersonoversikt.service.sakogbehandling

import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus
import no.nav.modiapersonoversikt.utils.DateUtils.toLocalDate
import no.nav.modiapersonoversikt.utils.isNotNullOrEmpty
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object SakOgBehandlingFilter {
    private val log = LoggerFactory.getLogger(SakOgBehandlingFilter::class.java)
    const val OPPRETTET = "opprettet"
    const val AVBRUTT = "avbrutt"
    const val AVSLUTTET = "avsluttet"
    const val SEND_SOKNAD_KVITTERINGSTYPE = "ae0002"
    const val DOKUMENTINNSENDING_KVITTERINGSTYPE = "ae0001"
    const val ULOVLIG_PREFIX = "17" // ukjent årsak til dette ulovlige prefixet
    private val ulovligeSakstema = arrayOf("FEI", "SAK", "SAP", "OPP", "YRA", "GEN", "AAR", "KLA", "HEL")
    private val lovligeBehandlingstyper = arrayOf("ae0047", "ae0034", "ae0014", "ae0020", "ae0019", "ae0011", "ae0045")
    private val KVITTERINGSTYPER = arrayOf(DOKUMENTINNSENDING_KVITTERINGSTYPE, SEND_SOKNAD_KVITTERINGSTYPE)

    @JvmStatic
    fun filtrerSaker(saker: List<Sak>): List<Sak> {
        return saker
            .filter { it.behandlingskjede.isNotNullOrEmpty() }
            .filter { it.sakstema.value !in ulovligeSakstema }
            .filter { it.behandlingskjede.any(::erLovligBehandling) }
    }

    @JvmStatic
    fun filtrerBehandlinger(kjeder: List<Behandlingskjede>): List<Behandlingskjede> {
        val enManedSiden = LocalDate.now().minus(1, ChronoUnit.MONTHS)
        return kjeder
            .asSequence()
            .filter { it.sisteBehandlingREF.startsWith(ULOVLIG_PREFIX).not() }
            .filter { it.sisteBehandlingstype?.value in lovligeBehandlingstyper }
            .filter { it.sisteBehandlingsstatus?.value != AVBRUTT }
            .filter {
                if (it.sisteBehandlingsstatus?.value == AVSLUTTET) {
                    val date = it.slutt ?: it.sisteBehandlingsoppdatering
                    toLocalDate(date).isAfter(enManedSiden)
                } else {
                    true
                }
            }
            .toList()
    }

    @JvmStatic
    fun behandlingsdato(kjede: Behandlingskjede): LocalDate {
        val avsluttet = erAvsluttet(kjede)
        val date = if (avsluttet) {
            kjede.slutt ?: kjede.sisteBehandlingsoppdatering
        } else {
            kjede.start
        }
        return toLocalDate(date)
    }

    @JvmStatic
    fun behandlingsstatus(kjede: Behandlingskjede): BehandlingsStatus {
        val behandlignsstatus = kjede.sisteBehandlingsstatus
        val status = behandlignsstatus?.value ?: error("Ukjent behandlingsstatus mottatt: $behandlignsstatus")
        return when (status) {
            AVSLUTTET -> BehandlingsStatus.FERDIG_BEHANDLET
            OPPRETTET -> BehandlingsStatus.UNDER_BEHANDLING
            AVBRUTT -> BehandlingsStatus.AVBRUTT
            else -> error("Ukjent behandlingsstatus mottatt: $behandlignsstatus")
        }
    }

    private fun erLovligBehandling(behandling: Behandlingskjede): Boolean {
        val enManedSiden = LocalDate.now().minus(1, ChronoUnit.MONTHS)
        val status = behandling.sisteBehandlingsstatus?.value ?: return false
        val type = behandling.sisteBehandlingstype?.value
        val erAvsluttet = status == AVSLUTTET
        val erKvitteringstype = type in KVITTERINGSTYPER
        val nyligOppdatert = toLocalDate(behandling.sisteBehandlingsoppdatering).isAfter(enManedSiden)
        val erlovligBehandlingstype = type in lovligeBehandlingstyper

        val harLovligStatusPaBehandling = when (status) {
            OPPRETTET -> !erKvitteringstype
            AVSLUTTET -> true
            else -> false
        }

        val harLovligPrefix = behandling.sisteBehandlingREF.startsWith(ULOVLIG_PREFIX).not()

        val erLovligGenerelt = (
            harLovligStatusPaBehandling &&
                nyligOppdatert &&
                harLovligPrefix
            )
        val erLovligtype = (erAvsluttet && erKvitteringstype) || erlovligBehandlingstype

        return erLovligGenerelt && erLovligtype
    }

    fun erAvsluttet(kjede: Behandlingskjede): Boolean {
        val behandlignsstatus = kjede.sisteBehandlingsstatus
        val status = behandlignsstatus?.value ?: error("Ukjent behandlingsstatus mottatt: $behandlignsstatus")
        val erAvsluttet = status == AVSLUTTET
        if (erAvsluttet && kjede.slutt == null) {
            log.warn(
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
