package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Ansatt
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.model.EnhetKontaktinformasjon
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Enhet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rest/enheter")
class EnhetController @Autowired
constructor(
    private val organisasjonEnhetKontaktinformasjonService: OrganisasjonEnhetKontaktinformasjonService,
    private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service,
    private val arbeidsfordeling: ArbeidsfordelingV1Service,
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

                val enhetid = organisasjonEnhetV2Service.finnNAVKontor(geografiskId, diskresjonskode ?: "")
                    .map { it.enhetId }
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Fant ikke enhetsid for gt: $geografiskId dkode: $diskresjonskode") }

                EnhetKontaktinformasjon(hentMedId(enhetid))
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
    fun hentAlleEnheterForOppgave(): List<Map<String, Any?>> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Enhet.OppgaveBehandlere)) {
                val enheter = organisasjonEnhetV2Service.hentAlleEnheter(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE)
                enheter.filter { erGyldigEnhet(it) }.map {
                    mapOf(
                        *hentAnsattEnhet(it)
                    )
                }
            }
    }

    @GetMapping("/oppgavebehandlere/foreslatte")
    fun hentBehandlendeEnhet(
        @RequestParam("fnr") fnr: String,
        @RequestParam("temakode") temakode: String,
        @RequestParam("typekode") typekode: String,
        @RequestParam("underkategori") underkategorikode: String?
    ): List<Map<String, Any?>> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, Enhet.Foreslatte)) {
                val enheter = arbeidsfordeling.finnBehandlendeEnhetListe(fnr, temakode, typekode, underkategorikode)
                enheter.map {
                    mapOf(
                        *hentAnsattEnhet(it)
                    )
                }
            }
    }

    private fun hentAnsattEnhet(ansattEnhet: AnsattEnhet): Array<Pair<String, Any?>> =
        arrayOf(Pair("enhetId", ansattEnhet.enhetId), Pair("enhetNavn", ansattEnhet.enhetNavn), Pair("status", ansattEnhet.status))

    private fun erGyldigEnhet(ansattEnhet: AnsattEnhet): Boolean = ansattEnhet.erAktiv() && Integer.parseInt(ansattEnhet.enhetId) >= 100
}
