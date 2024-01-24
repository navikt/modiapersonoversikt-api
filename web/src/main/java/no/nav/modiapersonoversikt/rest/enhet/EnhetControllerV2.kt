package no.nav.modiapersonoversikt.rest.enhet

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.OppgaveBehandlerFilter
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Enhet
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.service.ansattservice.domain.AnsattEnhet
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/v2/enheter")
class EnhetControllerV2 @Autowired
constructor(
    private val norgApi: NorgApi,
    private val arbeidsfordeling: ArbeidsfordelingService,
    private val ansattService: AnsattService,
    private val tilgangskontroll: Tilgangskontroll
) {
    @GetMapping("/{enhetId}/ansatte")
    fun hentAnsattePaaEnhet(@PathVariable("enhetId") enhetId: String): List<Ansatt> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Enhet.Ansatte, AuditIdentifier.ENHET_ID to enhetId)) {
                ansattService.ansatteForEnhet(
                    AnsattEnhet(
                        enhetId,
                        ""
                    )
                )
            }
    }

    @GetMapping("/oppgavebehandlere/alle")
    fun hentAlleEnheterForOppgave(): List<NorgDomain.Enhet> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Enhet.OppgaveBehandlere)) {
                norgApi.hentEnheter(
                    enhetId = null,
                    oppgaveBehandlende = OppgaveBehandlerFilter.KUN_OPPGAVEBEHANDLERE,
                    enhetStatuser = listOf(NorgDomain.EnhetStatus.AKTIV)
                )
            }
    }

    @PostMapping("/oppgavebehandlere/v2/foreslatte")
    fun hentBehandlendeEnhet(@RequestBody request: BehandlendeEnhetRequest): List<NorgDomain.Enhet> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(request.fnr)))
            .get(Audit.describe(READ, Enhet.Foreslatte)) {
                arbeidsfordeling.hentBehandlendeEnheter(
                    brukerIdent = Fnr.of(request.fnr),
                    fagomrade = request.temakode,
                    oppgavetype = request.typekode,
                    underkategori = request.underkategorikode
                )
            }
    }
}

data class BehandlendeEnhetRequest(
    val fnr: String,
    val temakode: String,
    val typekode: String,
    val underkategorikode: String?
)
