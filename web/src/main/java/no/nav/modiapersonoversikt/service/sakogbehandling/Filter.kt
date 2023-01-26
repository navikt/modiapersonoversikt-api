package no.nav.modiapersonoversikt.service.sakogbehandling

import no.nav.modiapersonoversikt.service.sakogbehandling.FilterUtils.erAvsluttet
import no.nav.modiapersonoversikt.service.sakogbehandling.FilterUtils.erKvitteringstype
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandling
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus
import no.nav.modiapersonoversikt.utils.isNotNullOrEmpty
import no.nav.personoversikt.common.utils.KotlinUtils.and
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak
import org.joda.time.DateTime
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.xml.datatype.DatatypeConfigurationException
import javax.xml.datatype.DatatypeFactory

object Filter {
    private const val ULOVLIG_PREFIX = "17" // ukjent årsak til dette ulovlige prefixet
    private val ulovligeSakstema = arrayOf("FEI", "SAK", "SAP", "OPP", "YRA", "GEN", "AAR", "KLA", "HEL")
    private val lovligeBehandlingstyper = arrayOf("ae0047", "ae0034", "ae0014", "ae0020", "ae0019", "ae0011", "ae0045")

    @JvmStatic
    fun filtrerSaker(saker: List<Sak>): List<Sak> {
        return saker
            .filter(::harLovligSakstema)
            .filter(::harBehandlinger)
            .filter(::harMinstEnLovligBehandling)
    }

    @JvmStatic
    fun filtrerBehandlinger(behandlinger: List<Behandling>): List<Behandling> {
        return behandlinger
            .asSequence()
            .filter(::harLovligBehandlingstype)
            .filter(::harLovligBehandlingsstatus)
            .filter(::harLovligPrefix)
            .filter(::sisteBehandlingErIkkeEldreEnn1Maned)
            .sortedByDescending { it.behandlingDato }
            .toList()
    }

    private fun erLovligBehandling(wsBehandlingskjede: Behandlingskjede) = ::harLovligStatusPaBehandling
        .and(::harLovligBehandlingstypeEllerAvsluttetKvittering)
        .and(::harLovligPrefix)
        .invoke(wsBehandlingskjede)

    private fun harLovligStatusPaBehandling(kjede: Behandlingskjede): Boolean {
        return when (val status = kjede.sisteBehandlingsstatus.value) {
            null -> false
            FilterUtils.OPPRETTET -> !erKvitteringstype(kjede.sisteBehandlingstype.value)
            else -> status == FilterUtils.AVSLUTTET
        }
    }
    private fun harLovligBehandlingstypeEllerAvsluttetKvittering(kjede: Behandlingskjede): Boolean {
        val type = kjede.sisteBehandlingstype.value
        return erFerdigsUnder1MndSidanEllerInnsendtSoknad(type, kjede) || lovligMenUtgaatStatusEllerUnderBehandling(
            type,
            kjede
        )
    }

    private fun erFerdigsUnder1MndSidanEllerInnsendtSoknad(type: String, kjede: Behandlingskjede): Boolean {
        if (erKvitteringstype(type)) {
            return erAvsluttet(kjede) && under1MndSidenFerdigstillelse(kjede)
        }
        return if (erAvsluttet(kjede)) {
            under1MndSidenFerdigstillelse(kjede) && lovligeBehandlingstyper.contains(
                type
            )
        } else {
            false
        }
    }

    private fun lovligMenUtgaatStatusEllerUnderBehandling(type: String, kjede: Behandlingskjede): Boolean {
        if (erAvsluttet(kjede) && !under1MndSidenFerdigstillelse(kjede)) {
            return false
        }
        return lovligeBehandlingstyper.contains(type) && !erAvsluttet(kjede)
    }

    // om nødvendig element mangler, skal ikkje behandlingstypen vises, logikken fiterer sakstemaet vekk
    // alle beahndlinger er ugyldig/utgått (se kode for filterbehandler for uttak av det visbare behandlingssettet).
    private fun under1MndSidenFerdigstillelse(kjede: Behandlingskjede): Boolean {
        if (kjede.sisteBehandlingsoppdatering != null) {
            try {
                val sisteDato = kjede.sisteBehandlingsoppdatering
                val now = LocalDate.now().minus(1, ChronoUnit.MONTHS)
                val xgcMonthAgo = DatatypeFactory
                    .newInstance()
                    .newXMLGregorianCalendarDate(now.year, now.monthValue, now.dayOfMonth, 0)
                val sisteDatoIMilliSec = sisteDato.toGregorianCalendar().timeInMillis.toDouble()
                val mndSidanIMilliSec = xgcMonthAgo.toGregorianCalendar().timeInMillis.toDouble()
                return sisteDatoIMilliSec >= mndSidanIMilliSec
            } catch (e: DatatypeConfigurationException) {
                e.printStackTrace()
            }
        }
        return false
    }

    // filterer ut ulovlige sakstema basert på blacklist
    private fun harLovligSakstema(wsSak: Sak) = wsSak.sakstema.value !in ulovligeSakstema

    // sak uten behandlinger skal ikke vises (sak med dokumenter skal)
    private fun harBehandlinger(wsSak: Sak) = wsSak.behandlingskjede.isNotNullOrEmpty()

    // vil retunere alle behandlinger om en i kjeden er lovlig
    private fun harMinstEnLovligBehandling(wsSak: Sak) = wsSak.behandlingskjede.any(::erLovligBehandling)

    // Er ikke alle behandlingstyper (XXyyyy) som skal taes med
    private fun harLovligBehandlingstype(behandling: Behandling) = behandling.behandlingsType in lovligeBehandlingstyper

    // alle statuser utenom avbrutt er tillatt
    private fun harLovligBehandlingsstatus(behandling: Behandling) = behandling.behandlingsStatus != BehandlingsStatus.AVBRUTT

    // prefix er to første tall av sisteBehandlingREF, og 17 er ulovlig av uviss grunn
    private fun harLovligPrefix(behandling: Behandling) = behandling.prefix != ULOVLIG_PREFIX

    // Prefix uviss grunn til at 17 er forbudt
    private fun harLovligPrefix(kjede: Behandlingskjede) = kjede.sisteBehandlingREF.startsWith(ULOVLIG_PREFIX).not()

    // filterning av behandlingskjeder mappet til behandling som er ferdig/avsluttet og er over 1 måned sidan den blie avslutta
    private fun sisteBehandlingErIkkeEldreEnn1Maned(behandling: Behandling): Boolean {
        return if (behandling.behandlingsStatus == BehandlingsStatus.FERDIG_BEHANDLET) {
            val behandlingDato = behandling.behandlingDato
            val nowMinus1Mnd = DateTime.now().minusMonths(1)
            behandlingDato.millis > nowMinus1Mnd.millis
        } else {
            true
        }
    }
}
