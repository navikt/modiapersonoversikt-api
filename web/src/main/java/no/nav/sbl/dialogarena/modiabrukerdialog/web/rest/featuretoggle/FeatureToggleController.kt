package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.featuretoggle

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

private const val APPLICATION_PREFIX = "modiabrukerdialog."
private val logger: Logger = LoggerFactory.getLogger(FeatureToggleController::class.java)

@Path("/featuretoggle")
class FeatureToggleController @Inject constructor(private val unleashService: UnleashService) {

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedId(@PathParam("id") toggleId: String): Map<String, Boolean> {
        val feature = getFeature(APPLICATION_PREFIX + toggleId)

        if(feature == null) {
            logger.warn("Featuretoggle-enum ikke funnet: " + APPLICATION_PREFIX + toggleId)
            return mapOf(APPLICATION_PREFIX + toggleId to false)
        }
        return mapOf(APPLICATION_PREFIX + toggleId to unleashService.isEnabled(feature))
    }

    fun getFeature(propertyKey: String) : Feature? {
        return Feature.values().find { it -> propertyKey.equals(it.propertyKey) }
    }
}