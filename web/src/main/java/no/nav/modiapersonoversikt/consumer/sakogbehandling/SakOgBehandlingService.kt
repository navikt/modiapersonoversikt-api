package no.nav.modiapersonoversikt.consumer.sakogbehandling

import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId

interface SakOgBehandlingService {
    fun hentAlleSaker(brukerId: EksternBrukerId): List<Sak>
    fun hentBehandlingskjederGruppertPaTema(brukerId: EksternBrukerId): Map<String, Behandlingskjede>

    class Behandling(
        val tema: String,
        val behandlingsType: String,
        val behandlingsStatus: BehandlingsStatus,
        val behandlingsDato: LocalDateTime,
        val sisteBehandlingRef: String
    ) {
        val prefix: String by lazy { sisteBehandlingRef.substring(0, 2) }
    }

    enum class BehandlingsStatus {
        UNDER_BEHANDLING, FERDIG_BEHANDLET, AVBRUTT
    }

    class Behandlingskjede(
        val tema: String,
        val status: BehandlingsStatus,
        val sistOppdatert: LocalDateTime,
    )
}

class SakOgBehandlingServiceImpl(
    val sakOgBehandlingV1: SakOgBehandlingV1,
    val pdl: PdlOppslagService
) : SakOgBehandlingService {
    companion object {
        val ulovligPrefix = "17"
        val ulovligeSakstema = listOf("FEI", "SAK", "SAP", "OPP", "YRA", "GEN", "AAR", "KLA", "HEL")
        val lovligeBehandlingstyper = listOf("ae0047", "ae0034", "ae0014", "ae0020", "ae0019", "ae0011", "ae0045")
        val log = LoggerFactory.getLogger("SakOgBehandlingServiceImpl")
    }

    override fun hentAlleSaker(brukerId: EksternBrukerId): List<Sak> {
        val aktorId = when (brukerId) {
            is AktorId -> brukerId.get()
            is Fnr -> pdl.hentAktorId(brukerId.get())
            else -> error("Kan bare bruke Fnr/AktorId man fikk ${brukerId::class.simpleName}")
        }
        val response = sakOgBehandlingV1.finnSakOgBehandlingskjedeListe(
            FinnSakOgBehandlingskjedeListeRequest().apply {
                aktoerREF = aktorId
            }
        )

        return response.sak
            .asSequence()
            .filtrerSaker() // Tilsvarerer gamle hentAlleSaker
            .toList()
    }

    override fun hentBehandlingskjederGruppertPaTema(brukerId: EksternBrukerId): Map<String, List<SakOgBehandlingService.Behandlingskjede>> {
        val cutoff = LocalDateTime.now().minusMonths(1)
        return hentAlleSaker(brukerId)
            .asSequence()
            .flatMap { sak -> hentBehandlingerFraKjede(sak) }
            .filter { it.behandlingsType in lovligeBehandlingstyper }
            .filter { it.behandlingsStatus != SakOgBehandlingService.BehandlingsStatus.AVBRUTT }
            .filter { it.prefix != ulovligPrefix }
            .filter {
                if (it.behandlingsStatus == SakOgBehandlingService.BehandlingsStatus.FERDIG_BEHANDLET) {
                    it.behandlingsDato.isAfter(cutoff)
                } else {
                    true
                }
            }
            .sortedByDescending { it.behandlingsDato }
            .map {
                SakOgBehandlingService.Behandlingskjede(
                    tema = it.tema,
                    status = it.behandlingsStatus,
                    sistOppdatert = it.behandlingsDato
                )
            }
    }

    private fun hentBehandlingerFraKjede(sak: Sak): List<SakOgBehandlingService.Behandling> {
        return sak.behandlingskjede.map {
            val behandlingsStatus = when (val status = it.sisteBehandlingsstatus.value) {
                "opprettet" -> SakOgBehandlingService.BehandlingsStatus.UNDER_BEHANDLING
                "avbrutt" -> SakOgBehandlingService.BehandlingsStatus.AVBRUTT
                "avsluttet" -> SakOgBehandlingService.BehandlingsStatus.FERDIG_BEHANDLET
                else -> error("Ukjent behandlingsstatus mottatt: $status")
            }

            val dato = if (behandlingsStatus == SakOgBehandlingService.BehandlingsStatus.FERDIG_BEHANDLET) {
                if (it.slutt == null) {
                    log.warn(
                        """
                        Inkonsistent data fra sak og behandling: Behandling rapporteres som avsluttet uten at kjede har slutt-tid satt.
                        Bruker `sisteBehandlingsoppdatering` som fallback.
                        BehandlingsId: ${it.sisteBehandlingREF}
                        BehandlinsgkjedeId: ${it.behandlingskjedeId}
                        """.trimIndent()
                    )
                }
                it.slutt ?: it.sisteBehandlingsoppdatering
            } else {
                it.start
            }.toGregorianCalendar().time

            SakOgBehandlingService.Behandling(
                tema = sak.sakstema.value,
                sisteBehandlingRef = it.sisteBehandlingREF,
                behandlingsType = it.sisteBehandlingstype.value,
                behandlingsStatus = behandlingsStatus,
                behandlingsDato = LocalDateTime.ofInstant(dato.toInstant(), ZoneId.systemDefault()),
            )
        }
    }

    private fun Sequence<Sak>.filtrerSaker(): Sequence<Sak> {
        return this
            .filter(::harLovligSakstema)
            .filter(::harBehandlinger)
            .filter(::harMinstEnLovligBehandling)
    }

    private fun Sequence<SakOgBehandlingService.Behandling>.filtrerBehandlinger(): Sequence<SakOgBehandlingService.Behandling> {
        val cutoff = LocalDateTime.now().minusMonths(1)
        return this
            .filter(::harLovligBehandlingstype)
            .filter(::harLovligBehandlingsstatus)
            .filter(::harLovligPrefix)
            .filter { sisteBehandlingErIkkeELdreEnn1Maned(cutoff, it) }
            .sortedBy { it.behandlingsDato }
    }

    private fun Sequence<SakOgBehandlingService.Behandling>.tilBehandlingskjeder(): Sequence<SakOgBehandlingService.Behandlingskjede> {
        return this
            .map {
                SakOgBehandlingService.Behandlingskjede(
                    tema = it.tema,
                    status = it.behandlingsStatus,
                    sistOppdatert = it.behandlingsDato
                )
            }
    }

    private fun harLovligSakstema(sak: Sak): Boolean {
        TODO()
    }

    private fun harBehandlinger(sak: Sak): Boolean {
        TODO()
    }

    private fun harMinstEnLovligBehandling(sak: Sak): Boolean {
        TODO()
    }

    private fun harLovligBehandlingstype(behandling: SakOgBehandlingService.Behandling): Boolean {
        return behandling.behandlingsType in lovligeBehandlingstyper
    }

    private fun harLovligBehandlingsstatus(behandling: SakOgBehandlingService.Behandling): Boolean {
        return behandling.behandlingsStatus != SakOgBehandlingService.BehandlingsStatus.AVBRUTT
    }

    private fun harLovligPrefix(behandling: SakOgBehandlingService.Behandling): Boolean {
        return behandling.prefix != ulovligPrefix
    }

    private fun sisteBehandlingErIkkeELdreEnn1Maned(
        cutoff: LocalDateTime,
        behandling: SakOgBehandlingService.Behandling
    ): Boolean {
        return if (behandling.behandlingsStatus == SakOgBehandlingService.BehandlingsStatus.FERDIG_BEHANDLET) {
            behandling.behandlingsDato.isAfter(cutoff)
        } else {
            true
        }
    }
}
