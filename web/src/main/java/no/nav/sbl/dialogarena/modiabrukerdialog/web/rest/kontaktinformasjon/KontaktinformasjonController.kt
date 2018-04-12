package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kontaktinformasjon

import no.nav.dkif.consumer.DkifService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("/kontaktinformasjon/{fnr}")
class KontaktinformasjonController @Inject constructor(private val dkifService: DkifService) {

    @GET
    @Path("/")
    fun hentKontaktinformasjon(@PathParam("fnr") fødselsnummer: String): Map<String, Any> {
        check(visFeature(Feature.PERSON_REST_API))

        val response = dkifService.hentDigitalKontaktinformasjon(fødselsnummer)

        return mapOf(
                "epost" to mapOf(
                        "value" to response.digitalKontaktinformasjon.epostadresse?.value,
                        "sistOppdatert" to response.digitalKontaktinformasjon.epostadresse?.sistOppdatert
                ),
                "mobiltelefon" to mapOf(
                        "value" to response.digitalKontaktinformasjon.mobiltelefonnummer?.value,
                        "sistOppdatert" to response.digitalKontaktinformasjon.mobiltelefonnummer?.sistOppdatert
                ),
                "reservasjon" to response.digitalKontaktinformasjon.reservasjon
        )
    }

}