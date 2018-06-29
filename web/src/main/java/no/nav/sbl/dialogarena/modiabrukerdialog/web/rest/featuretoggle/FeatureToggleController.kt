package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.featuretoggle

import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import org.slf4j.LoggerFactory
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/featuretoggle")
class FeatureToggleController {

    val logger = LoggerFactory.getLogger(FeatureToggleController::class.java)

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedId(@PathParam("id") toggleId: String): Map<String, Boolean> {
        val feature = Feature.values()
                .findLast { it -> it.propertyKey == toggleId }

        if (feature != null) {
            return mapOf(toggleId to visFeature(feature))
        } else {
            logger.warn("Featuretoggle med id $toggleId ikke funnet. Defaulter til false.")
            return mapOf(toggleId to false)
        }
    }
}