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
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService
import no.nav.modiapersonoversikt.rest.enhet.model.EnhetKontaktinformasjon
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rest/enheter")
class EnhetController @Autowired
constructor(
    private val organisasjonEnhetKontaktinformasjonService: OrganisasjonEnhetKontaktinformasjonService,
    private val norgApi: NorgApi,
    private val arbeidsfordeling: ArbeidsfordelingService,
    private val ansattService: AnsattService,
    private val tilgangskontroll: Tilgangskontroll
) {

    @GetMapping("/{id}")
    fun hentMedId(@PathVariable("id") organisasjonsid: String): OrganisasjonEnhetKontaktinformasjon {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Enhet.Kontaktinformasjon, AuditIdentifier.ORGANISASJON_ID to organisasjonsid)) {
                organisasjonEnhetKontaktinformasjonService.hentKontaktinformasjon(organisasjonsid)
            }
    }

    @GetMapping
    fun finnEnhet(@RequestParam("gt") geografiskId: String?, @RequestParam("dkode") diskresjonskode: String?): EnhetKontaktinformasjon {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Enhet.Kontaktinformasjon, AuditIdentifier.GEOGRAFISK_ID to geografiskId, AuditIdentifier.DISKRESJONSKODE to diskresjonskode)) {
                if (geografiskId.isNullOrEmpty() && diskresjonskode.isNullOrEmpty()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "'gt' eller 'dkode' må være spesifisert")
                }

                norgApi
                    .runCatching {
                        finnNavKontor(
                            geografiskTilknytning = geografiskId ?: "",
                            diskresjonskode = diskresjonskode?.let(NorgDomain.DiskresjonsKode::valueOf)
                        )
                    }
                    .map { it.enhetId }
                    .mapCatching { requireNotNull(it) }
                    .map { EnhetKontaktinformasjon(hentMedId(it)) }
                    .getOrElse {
                        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Fant ikke enhetsid for gt: $geografiskId dkode: $diskresjonskode")
                    }
            }
    }

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

    private fun erGyldigEnhet(enhet: NorgDomain.Enhet): Boolean = enhet.status == NorgDomain.EnhetStatus.AKTIV && Integer.parseInt(enhet.enhetId) >= 100
}
