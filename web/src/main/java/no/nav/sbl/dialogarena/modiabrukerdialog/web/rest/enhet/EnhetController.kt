package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.model.EnhetKontaktinformasjon

import javax.inject.Inject
import javax.ws.rs.*

import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/enheter")
class EnhetController @Inject
constructor(private val organisasjonEnhetKontaktinformasjonService: OrganisasjonEnhetKontaktinformasjonService,
            private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service) {

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedId(@PathParam("id") organisasjonsid: String): OrganisasjonEnhetKontaktinformasjon {
        return organisasjonEnhetKontaktinformasjonService.hentKontaktinformasjon(organisasjonsid)
    }

    @GET
    @Path("/geo/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedGeoTilk(@PathParam("id") geografiskId: String, @QueryParam("dkode") diskresjonskode: String?): EnhetKontaktinformasjon {

        check(visFeature(Feature.ENHETER_GEOGRAFISK_TILKNYTNING_API))

        val enhetid = organisasjonEnhetV2Service.finnNAVKontor(geografiskId, diskresjonskode ?: "")
                .map { it.enhetId }
                .orElseThrow{ NotFoundException() }

        return EnhetKontaktinformasjon(hentMedId(enhetid))
    }

}
