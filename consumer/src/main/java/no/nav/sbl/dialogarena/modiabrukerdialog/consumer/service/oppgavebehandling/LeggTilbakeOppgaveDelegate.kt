package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.OppgaveJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.stream.Collectors

open class LeggTilbakeOppgaveDelegate(
        val restOppgaveBehandlingService: RestOppgaveBehandlingServiceImpl,
        val arbeidsfordelingService: ArbeidsfordelingV1Service
    ) {

    private val log = LoggerFactory.getLogger(LeggTilbakeOppgaveDelegate::class.java)

    fun leggTilbake(oppgave: OppgaveJsonDTO, request: LeggTilbakeOppgaveRequest) {
        validerTilgang(oppgave)
        markerOppgaveSomLagtTilbake(oppgave, request)
        if (temagrupeErSatt(request.nyTemagruppe)) {
            oppdaterForNyTemagruppe(oppgave, request.nyTemagruppe)
        }
        lagreOppgave(oppgave, request)
    }

    private fun validerTilgang(oppgave: OppgaveJsonDTO) {
        val innloggetSaksbehandler = SubjectHandler.getIdent().orElseThrow { RuntimeException("Fant ikke ident") }
        if (innloggetSaksbehandler != oppgave.tilordnetRessurs) {
            val feilmelding = ("Innlogget saksbehandler " + innloggetSaksbehandler
                    + " har ikke tilgang til oppgave " + oppgave.id
                    + ". Oppgavens ansvarlige id er satt til : " + oppgave.tilordnetRessurs + ".")
            throw ResponseStatusException(HttpStatus.FORBIDDEN, feilmelding)
        }
    }

    private fun markerOppgaveSomLagtTilbake(oppgave: OppgaveJsonDTO, request: LeggTilbakeOppgaveRequest) {
        oppgave.tilordnetRessurs?.replace(oppgave.tilordnetRessurs!!, "")
        oppgave.beskrivelse?.replace(oppgave.beskrivelse!!, lagNyBeskrivelse(oppgave, request))
    }

    private fun lagNyBeskrivelse(oppgave: OppgaveJsonDTO, request: LeggTilbakeOppgaveRequest): String {
        return restOppgaveBehandlingService.leggTilBeskrivelse(oppgave.beskrivelse, request.beskrivelse,
                request.saksbehandlersValgteEnhet)
    }

    private fun temagrupeErSatt(temagruppe: Temagruppe?): Boolean {
        return temagruppe != null
    }

    private fun oppdaterForNyTemagruppe(oppgave: OppgaveJsonDTO, temagruppe: Temagruppe) {
        oppgave.tilordnetRessurs?.replace(oppgave.tilordnetRessurs!!, getAnsvarligEnhet(oppgave, temagruppe))
        oppgave.temagruppe?.replace(oppgave.temagruppe!!, temagruppe.name)
    }

    private fun getAnsvarligEnhet(oppgave: OppgaveJsonDTO, temagruppe: Temagruppe): String {
        val enheter = finnBehandlendeEnhetListe(oppgave, temagruppe).stream()
                .map { enhet: AnsattEnhet -> enhet.enhetId }
                .collect(Collectors.toList())
        return if (enheter.isEmpty()) oppgave.tilordnetRessurs!! else enheter[0]
    }

    private fun finnBehandlendeEnhetListe(oppgave: OppgaveJsonDTO, temagruppe: Temagruppe): List<AnsattEnhet> {
        return try {
            arbeidsfordelingService.finnBehandlendeEnhetListe(oppgave.tilordnetRessurs,
                    oppgave.tema,
                    oppgave.oppgavetype,
                    underkategoriKode_arbeidsfordelingOverstyrt(temagruppe))
        } catch (e: Exception) {
            log.error("Henting av behandlende enhet feilet", e)
            emptyList()
        }
    }

    private fun lagreOppgave(oppgave: OppgaveJsonDTO, request: LeggTilbakeOppgaveRequest) {
        try {
            restOppgaveBehandlingService.lagreOppgave(oppgave, request.nyTemagruppe, request.saksbehandlersValgteEnhet)
        } catch (lagreOppgaveOptimistiskLasing: LagreOppgaveOptimistiskLasing) {
            throw RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing)
        }
    }

    companion object {
        private fun underkategoriKode_arbeidsfordelingOverstyrt(temagruppe: Temagruppe): String {
            return if (temagruppe == Temagruppe.OKSOS) {
                underkategoriKode(Temagruppe.ANSOS)
            } else underkategoriKode(temagruppe)
        }

        private fun underkategoriKode(temagruppe: Temagruppe): String {
            return temagruppe.toString() + "_KNA"
        }
    }
}