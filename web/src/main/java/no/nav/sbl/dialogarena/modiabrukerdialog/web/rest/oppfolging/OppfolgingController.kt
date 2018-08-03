package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppfolging

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/oppfolging/{fnr}")
@Produces(MediaType.APPLICATION_JSON)
class OppfolgingController @Inject constructor(private val service: OppfolgingsinfoService,
                                               private val unleashService: UnleashService) {

    @GET
    @Path("/")
    fun hent(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        val oppfølging = service.hentOppfolgingsinfo(fødselsnummer)

        return mapOf(
                "erUnderOppfølging" to oppfølging.erUnderOppfolging,
                "veileder" to oppfølging.veileder.map {
                    it?.let { mapOf(
                            "ident" to it.ident
                    ) }
                },
                "enhet" to oppfølging.oppfolgingsenhet.map {
                    it?.let { mapOf(
                            "id" to it.enhetId,
                            "navn" to it.enhetNavn,
                            "status" to it.status
                    ) }
                }
        )
    }

}