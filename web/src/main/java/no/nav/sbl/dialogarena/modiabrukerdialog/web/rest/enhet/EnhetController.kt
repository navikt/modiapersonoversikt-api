package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.model.EnhetKontaktinformasjon
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/enheter")
class EnhetController @Inject
constructor(private val organisasjonEnhetKontaktinformasjonService: OrganisasjonEnhetKontaktinformasjonService,
            private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service,
            private val arbeidsfordeling: ArbeidsfordelingV1Service,
            private val unleashService: UnleashService) {

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedId(@PathParam("id") organisasjonsid: String): OrganisasjonEnhetKontaktinformasjon {
        return organisasjonEnhetKontaktinformasjonService.hentKontaktinformasjon(organisasjonsid)
    }

    @GET
    @Produces(APPLICATION_JSON)
    fun finnEnhet(@QueryParam("gt") geografiskId: String?, @QueryParam("dkode") diskresjonskode: String?): EnhetKontaktinformasjon {
        if (geografiskId.isNullOrEmpty() && diskresjonskode.isNullOrEmpty()) throw NotFoundException();

        val enhetid = organisasjonEnhetV2Service.finnNAVKontor(geografiskId, diskresjonskode ?: "")
                .map { it.enhetId }
                .orElseThrow { NotFoundException() }

        return EnhetKontaktinformasjon(hentMedId(enhetid))
    }

    @GET
    @Path("/dialog/oppgave/alle")
    @Produces(APPLICATION_JSON)
    fun hentAlleEnheterForOppgave(): List<Map<String, Any?>> {
        val enheter = organisasjonEnhetV2Service.hentAlleEnheter(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE)
        return enheter.filter { erGyldigEnhet(it) }.map {
            mapOf(
                    *hentAnsattEnhet(it)
            )
        }
    }

    @GET
    @Path("/dialog/oppgave/behandle")
    @Produces(APPLICATION_JSON)
    fun hentBehandlendeEnhet(@QueryParam("fnr") fødselsnummer: String,
                             @QueryParam("temakode") temakode: String,
                             @QueryParam("typekode") typekode: String,
                             @QueryParam("underkategorikode") underkategorikode: String?): List<Map<String, Any?>> {
        val enheter = arbeidsfordeling.finnBehandlendeEnhetListe(fødselsnummer, temakode, typekode, underkategorikode)
        return enheter.map {
            mapOf(
                    *hentAnsattEnhet(it)
            )
        }
    }

    private fun hentAnsattEnhet(ansattEnhet: AnsattEnhet): Array<Pair<String, Any?>> =
            arrayOf(Pair("enhetId", ansattEnhet.enhetId), Pair("enhetNavn", ansattEnhet.enhetNavn), Pair("status", ansattEnhet.status))

    private fun erGyldigEnhet(ansattEnhet: AnsattEnhet): Boolean = ansattEnhet.erAktiv() && (ansattEnhet.enhetId as Int) >= 100
}
