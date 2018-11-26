package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppfolging

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoApiService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType

@Path("/oppfolging/{fnr}")
@Produces(MediaType.APPLICATION_JSON)
class OppfolgingController @Inject constructor(private val service: OppfolgingsinfoApiService,
                                               private val unleashService: UnleashService) {

    @GET
    @Path("/")
    fun hent(@PathParam("fnr") fødselsnummer: String, @Context req: HttpServletRequest): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        val oppfølging = service.hentOppfolgingsinfo(fødselsnummer, req)

        return mapOf(
                "erUnderOppfølging" to oppfølging.erUnderOppfolging,
                "veileder" to hentVeileder(oppfølging.veileder),
                "enhet" to hentEnhet(oppfølging.oppfolgingsenhet)
        )
    }

    private fun hentVeileder(veileder: Optional<Saksbehandler>): Map<String, Any?>? {
        return if(veileder.isPresent) {
            mapOf(
                    "ident" to veileder.get().ident
            )
        } else {
            null
        }
    }

    private fun hentEnhet(enhet: Optional<AnsattEnhet>): Map<String, Any?>? {
        return if(enhet.isPresent) {
            mapOf(
                    "id" to enhet.get().enhetId,
                    "navn" to enhet.get().enhetNavn,
                    "status" to enhet.get().status
            )
        } else {
            null
        }
    }

}