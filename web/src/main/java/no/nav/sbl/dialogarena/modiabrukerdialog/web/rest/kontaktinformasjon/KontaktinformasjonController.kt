package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kontaktinformasjon

import no.nav.dkif.consumer.DkifService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/person/{fnr}/kontaktinformasjon")
@Produces(MediaType.APPLICATION_JSON)
class KontaktinformasjonController @Inject constructor(private val dkifService: DkifService, private val tilgangskontroll: Tilgangskontroll) {

    @GET
    @Path("/")
    fun hentKontaktinformasjon(@PathParam("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get {
                    val response = dkifService.hentDigitalKontaktinformasjon(fnr)

                    mapOf(
                            "epost" to getEpost(response),
                            "mobiltelefon" to getMobiltelefon(response),
                            "reservasjon" to response.digitalKontaktinformasjon.reservasjon
                    )
                }
    }

    private fun getEpost(response: WSHentDigitalKontaktinformasjonResponse): Map<String, Any>? {
        if (response.digitalKontaktinformasjon.epostadresse?.value.isNullOrEmpty()) {
            return null
        }
        return mapOf(
                "value" to response.digitalKontaktinformasjon.epostadresse.value,
                "sistOppdatert" to response.digitalKontaktinformasjon.epostadresse.sistOppdatert
        )
    }

    private fun getMobiltelefon(response: WSHentDigitalKontaktinformasjonResponse): Map<String, Any>? {
        if (response.digitalKontaktinformasjon.mobiltelefonnummer?.value.isNullOrEmpty()) {
            return null
        }
        return mapOf(
                "value" to response.digitalKontaktinformasjon.mobiltelefonnummer.value,
                "sistOppdatert" to response.digitalKontaktinformasjon.mobiltelefonnummer.sistOppdatert
        )
    }

}