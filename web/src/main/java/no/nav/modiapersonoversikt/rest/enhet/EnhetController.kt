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
import no.nav.modiapersonoversikt.legacy.api.domain.norg.Ansatt
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/enheter")
class EnhetController @Autowired
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
                ansattService.ansatteForEnhet(AnsattEnhet(enhetId, ""))
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

    @GetMapping("/oppgavebehandlere/foreslatte")
    fun hentBehandlendeEnhet(
        @RequestParam("fnr") fnr: String,
        @RequestParam("temakode") temakode: String,
        @RequestParam("typekode") typekode: String,
        @RequestParam("underkategori") underkategorikode: String?
    ): List<NorgDomain.Enhet> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, Enhet.Foreslatte)) {
                arbeidsfordeling.hentBehandlendeEnheter(
                    brukerIdent = Fnr.of(fnr),
                    fagomrade = temakode,
                    oppgavetype = typekode,
                    underkategori = underkategorikode
                )
            }
    }
}
