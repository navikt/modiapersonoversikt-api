package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Ansatt
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.model.EnhetKontaktinformasjon
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/enheter")
class EnhetController @Inject
constructor(private val organisasjonEnhetKontaktinformasjonService: OrganisasjonEnhetKontaktinformasjonService,
            private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service,
            private val arbeidsfordeling: ArbeidsfordelingV1Service,
            private val ansattService: AnsattService,
            private val tilgangskontroll: Tilgangskontroll) {

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedId(@PathParam("id") organisasjonsid: String): OrganisasjonEnhetKontaktinformasjon {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get {
                    organisasjonEnhetKontaktinformasjonService.hentKontaktinformasjon(organisasjonsid)
                }
    }

    @GET
    @Produces(APPLICATION_JSON)
    fun finnEnhet(@QueryParam("gt") geografiskId: String?, @QueryParam("dkode") diskresjonskode: String?): EnhetKontaktinformasjon {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get {
                    if (geografiskId.isNullOrEmpty() && diskresjonskode.isNullOrEmpty()) throw NotFoundException();

                    val enhetid = organisasjonEnhetV2Service.finnNAVKontor(geografiskId, diskresjonskode ?: "")
                            .map { it.enhetId }
                            .orElseThrow { NotFoundException() }

                    EnhetKontaktinformasjon(hentMedId(enhetid))
                }
    }

    @GET
    @Path("/{enhetId}/ansatte")
    @Produces(APPLICATION_JSON)
    fun hentAnsattePaaEnhet(@PathParam("enhetId") enhetId: String): List<Ansatt> {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get {
                    ansattService.ansatteForEnhet(AnsattEnhet(enhetId, ""))
                }
    }

    @GET
    @Path("/oppgavebehandlere/alle")
    @Produces(APPLICATION_JSON)
    fun hentAlleEnheterForOppgave(): List<Map<String, Any?>> {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get {
                    val enheter = organisasjonEnhetV2Service.hentAlleEnheter(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE)
                    enheter.filter { erGyldigEnhet(it) }.map {
                        mapOf(
                                *hentAnsattEnhet(it)
                        )
                    }
                }
    }

    @GET
    @Path("/oppgavebehandlere/foreslatte")
    @Produces(APPLICATION_JSON)
    fun hentBehandlendeEnhet(@QueryParam("fnr") fnr: String,
                             @QueryParam("temakode") temakode: String,
                             @QueryParam("typekode") typekode: String,
                             @QueryParam("underkategorikode") underkategorikode: String?): List<Map<String, Any?>> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get {
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
