package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Behandling
import java.time.LocalDateTime

object Filter {
    private const val ULOVLIG_PREFIX = "17" // ukjent årsak til dette ulovlige prefixet
    private val ulovligeSakstema = arrayOf("FEI", "SAK", "SAP", "OPP", "YRA", "GEN", "AAR", "KLA", "HEL")
    private val lovligeBehandlingstyper = arrayOf("ae0047", "ae0034", "ae0014", "ae0020", "ae0019", "ae0011", "ae0045")
    private const val SEND_SOKNAD_KVITTERINGSTYPE = "ae0002"
    private const val DOKUMENTINNSENDING_KVITTERINGSTYPE = "ae0001"

    private val behandlingFilters =
        listOf<(Behandling) -> Boolean>(
            ::erLovligBehandling,
            ::harLovligSakstema,
            ::harHendelser,
            ::harLovligBehandlingstype,
            ::harLovligBehandlingsstatus,
            ::harLovligPrefix,
            ::sisteHendelseErIkkeEldreEnn1Ar,
        )

    fun filtrerOgSorterBehandligner(behandlinger: List<Behandling>): List<Behandling> {
        return filtrerBehandlinger(behandlinger = behandlinger).sortedByDescending { it.sistOppdatert }
    }

    private fun filtrerBehandlinger(behandlinger: List<Behandling>): List<Behandling> {
        val result = mutableListOf<Behandling>()
        for (behandling in behandlinger) {
            for (filter in behandlingFilters) {
                if (!filter(behandling)) {
                    return result
                }
            }
            result.add(behandling)
        }
        return result
    }

    private fun erLovligBehandling(behandling: Behandling): Boolean {
        return harLovligStatusPaBehandling(behandling) && harLovligBehandlingstypeEllerAvsluttetKvittering(behandling) &&
            harLovligPrefix(
                behandling,
            )
    }

    internal fun harLovligStatusPaBehandling(behandling: Behandling): Boolean {
        return when (val status = behandling.status) {
            Behandling.Status.UNDER_BEHANDLING -> !erKvitteringstype(behandling.behandlingsType)
            else -> status == Behandling.Status.FERDIG_BEHANDLET // TODO: Denne filtrerer nå bort alle som er avbrutt
        }
    }

    internal fun harLovligBehandlingstypeEllerAvsluttetKvittering(behandling: Behandling): Boolean {
        val behandlingsType = behandling.behandlingsType
        if (behandlingsType != null) {
            return erFerdigUnder1MndSidenEllerInnsendtSoknad(behandlingsType, behandling) ||
                lovligMenUtgaattStatusEllerUnderBehandling(
                    behandlingsType,
                    behandling,
                )
        }

        return false
    }

    internal fun erFerdigUnder1MndSidenEllerInnsendtSoknad(
        type: String,
        behandling: Behandling,
    ): Boolean {
        if (erKvitteringstype(type)) {
            return erAvsluttet(behandling)
        }
        return if (erAvsluttet(behandling)) {
            under1MndSidenFerdistillelse(behandling) && lovligeBehandlingstyper.contains(type)
        } else {
            false
        }
    }

    internal fun erKvitteringstype(type: String?): Boolean {
        if (type == null) return false
        return type == SEND_SOKNAD_KVITTERINGSTYPE || DOKUMENTINNSENDING_KVITTERINGSTYPE == type
    }

    private fun erAvsluttet(behandling: Behandling): Boolean {
        return behandling.status == Behandling.Status.FERDIG_BEHANDLET || behandling.status == Behandling.Status.AVBRUTT
    }

    internal fun lovligMenUtgaattStatusEllerUnderBehandling(
        type: String,
        behandling: Behandling,
    ): Boolean {
        if (erAvsluttet(behandling) && !under1MndSidenFerdistillelse(behandling)) {
            return false
        }
        return lovligeBehandlingstyper.contains(type) && !erAvsluttet(behandling)
    }

    // om nødvendig element mangler, skal ikkje behandlingstypen vises, logikken fiterer sakstemaet vekk
    // alle beahndlinger er ugyldig/utgått (se kode for filterbehandler for uttak av det visbare behandlingssettet).
    private fun under1MndSidenFerdistillelse(behandling: Behandling): Boolean {
        return behandling.sluttTidspunkt?.isAfter(LocalDateTime.now().minusMonths(1)) ?: false
    }

    // filterer ut ulovlige sakstema basert på blacklist
    private fun harLovligSakstema(behandling: Behandling): Boolean {
        return behandling.sakstema !in ulovligeSakstema
    }

    // sak uten behandlinger skal ikke vises (sak med dokumenter skal)
    private fun harHendelser(behandling: Behandling): Boolean {
        return behandling.hendelser?.isNotEmpty() ?: false
    }

    // Er ikke alle behandlingstyper (XXyyyy) som skal taes med
    private fun harLovligBehandlingstype(behandling: Behandling): Boolean {
        return behandling.behandlingsType in lovligeBehandlingstyper
    }

    // alle statuser utenom avbrutt er tillatt
    private fun harLovligBehandlingsstatus(behandling: Behandling): Boolean {
        return behandling.status != Behandling.Status.AVBRUTT
    }

    // Prefix uviss grunn til at 17 er forbudt
    private fun harLovligPrefix(behandling: Behandling): Boolean {
        return !behandling.behandlingId.startsWith(ULOVLIG_PREFIX)
    }

    // filterning av behandlingskjeder mappet til behandling som er ferdig/avsluttet og er over 1 år sidan den blie avslutta
    private fun sisteHendelseErIkkeEldreEnn1Ar(behandling: Behandling): Boolean {
        return if (behandling.status == Behandling.Status.FERDIG_BEHANDLET) {
            val behandlingsDato = behandling.sistOppdatert
            val nowMinus1Mnth = LocalDateTime.now().minusYears(1)
            return behandlingsDato.isAfter(nowMinus1Mnth)
        } else {
            true
        }
    }
}
