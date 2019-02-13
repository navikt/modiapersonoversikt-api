package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

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
            private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service, private val unleashService: UnleashService) {

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

}
