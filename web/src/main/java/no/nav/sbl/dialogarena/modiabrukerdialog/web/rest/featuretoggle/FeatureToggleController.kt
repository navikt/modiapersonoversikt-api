package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.featuretoggle

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashService
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

private const val applicationPrefix = "modiabrukerdialog."

@Path("/featuretoggle")
class FeatureToggleController @Inject constructor(private val unleashService: UnleashService) {

    val logger = LoggerFactory.getLogger(FeatureToggleController::class.java)

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedId(@PathParam("id") toggleId: String): Map<String, Boolean> {
        return mapOf(applicationPrefix + toggleId to unleashService.isEnabled(applicationPrefix + toggleId))
    }
}