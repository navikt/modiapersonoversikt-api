package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.GetOppgaverResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.OppgaveJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toOppgaveJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toPutOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.Utils.KONTAKT_NAV
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.Utils.SPORSMAL_OG_SVAR
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.Utils.endretAvEnhet
import org.slf4j.LoggerFactory
import java.util.*

class PlukkOppgaveApi(private val apiClient: OppgaveApi, private val kodeverksmapperService: KodeverksmapperService) {
    private val log = LoggerFactory.getLogger(PlukkOppgaveApi::class.java)

    fun plukkOppgaverFraGsak(temagruppe: Temagruppe?, valgtEnhet: String?): List<OppgaveJsonDTO> {
        requireNotNull(temagruppe)

        val correlationId = UUID.randomUUID().toString()
        val eldsteOppgave = finnOgTilordneEldsteOppgave(correlationId, temagruppe, valgtEnhet)
        if (eldsteOppgave == null) {
            log.warn("Prøvde å tildele eldste oppgave, men fikk tomt svar tilbake")
            return mutableListOf()
        }
        val aktorId = requireNotNull(eldsteOppgave.aktoerId)
        val oppgaverTilknyttetAktor = sokEtterOppgaver(
            correlationId = correlationId,
            temagruppe = temagruppe,
            aktoerId = aktorId,
            limit = 100
        )

        val tildelteOppgaver = tilordneAlleOppgaver(correlationId, oppgaverTilknyttetAktor.oppgaver ?: emptyList())
        tildelteOppgaver.add(eldsteOppgave)

        return tildelteOppgaver
    }

    private fun tilordneAlleOppgaver(correlationId: String, oppgaver: List<OppgaveJsonDTO>): MutableList<OppgaveJsonDTO> {
        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val tildelteOppgaver = mutableListOf<OppgaveJsonDTO>()
        for (oppgave in oppgaver) {
            try {
                val tildeltOppgave = apiClient
                    .endreOppgave(
                        xminusCorrelationMinusID = correlationId,
                        id = requireNotNull(oppgave.id),
                        putOppgaveRequestJsonDTO = oppgave.copy(
                            tilordnetRessurs = ident
                        ).toPutOppgaveRequestJsonDTO()
                    )
                tildelteOppgaver.add(tildeltOppgave.toOppgaveJsonDTO())
            } catch (e: Exception) {
                log.warn("Kunne ikke tildele oppgave", e)
            }
        }

        return tildelteOppgaver
    }

    private fun finnOgTilordneEldsteOppgave(
        correlationId: String,
        temagruppe: Temagruppe?,
        valgtEnhet: String?
    ): OppgaveJsonDTO? {
        for (i in 0 until 5) {
            try {
                return finnEldsteOppgave(correlationId, temagruppe, valgtEnhet)
            } catch (e: Exception) {
                log.warn("Feil ved uthenting av eldste oppgave", e)
            }
        }
        return null
    }

    private fun finnEldsteOppgave(
        correlationId: String,
        temagruppe: Temagruppe?,
        valgtEnhet: String?
    ): OppgaveJsonDTO {
        requireNotNull(temagruppe)
        requireNotNull(valgtEnhet)

        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val response = sokEtterOppgaver(correlationId, temagruppe)

        val eldsteOppgave = (response.oppgaver ?: emptyList())
            .minBy {
                requireNotNull(it.opprettetTidspunkt) {
                    "Opprettet tidspunkt kan ikke være null, ved tildeling av eldste oppgave."
                }
            }

        requireNotNull(eldsteOppgave) {
            "Fant ingen oppgave å tildele"
        }
        requireNotNull(eldsteOppgave.aktoerId) {
            "Fant oppgave uten aktørId. "
        }

        return apiClient.endreOppgave(
            correlationId,
            requireNotNull(eldsteOppgave.id),
            eldsteOppgave.copy(
                tilordnetRessurs = ident,
                endretAvEnhetsnr = endretAvEnhet(temagruppe, valgtEnhet)
            ).toPutOppgaveRequestJsonDTO()
        ).toOppgaveJsonDTO()
    }

    private fun sokEtterOppgaver(
        correlationId: String,
        temagruppe: Temagruppe,
        aktoerId: String? = null,
        limit: Long = 20
    ): GetOppgaverResponseJsonDTO {
        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val behandling = kodeverksmapperService.mapUnderkategori(temagruppe.underkategori)
        val oppgaveType = kodeverksmapperService.mapOppgavetype(SPORSMAL_OG_SVAR)
        val aktoerIdList = if (aktoerId == null) null else listOf(aktoerId)

        return apiClient.finnOppgaver(
            xminusCorrelationMinusID = correlationId,
            aktoerId = aktoerIdList,
            statuskategori = "AAPEN",
            tema = listOf(KONTAKT_NAV),
            oppgavetype = listOf(oppgaveType),
            tildeltRessurs = false,
            ikkeTidligereTilordnetRessurs = ident,
            behandlingstema = behandling.map { it.behandlingstema }.orElse(null),
            behandlingstype = behandling.map { it.behandlingstype }.orElse(null),
            sorteringsfelt = "OPPRETTET_TIDSPUNKT",
            sorteringsrekkefolge = "ASC",
            limit = limit
        )
    }
}
