package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.HentPersonSikkerhetsbegrensning

import javax.inject.Inject
import javax.ws.rs.*

import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/enheter")
class EnhetController @Inject
constructor(private val oekservice: OrganisasjonEnhetKontaktinformasjonService,
            private val oe2service: OrganisasjonEnhetV2Service) {

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedId(@PathParam("id") organisasjonsid: String): OrganisasjonEnhetKontaktinformasjon {
        return oekservice.hentKontaktinformasjon(organisasjonsid)
    }

    @GET
    @Path("/geo/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedGeoTilk(@PathParam("id") geografiskId: String, @QueryParam("dkode") diskresjonskode: String): OrganisasjonEnhetKontaktinformasjon {

        check(visFeature(Feature.ENHETER_GEOGRAFISK_TILKNYTNING_API))

        val enhetid = oe2service.finnNAVKontor(geografiskId, diskresjonskode)
                .map { it.enhetId }
                .orElseThrow{ NotFoundException() }

        return hentMedId(enhetid)
    }

}
