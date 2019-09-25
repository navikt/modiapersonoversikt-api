package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.featuretoggle

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

private const val APPLICATION_PREFIX = "modiabrukerdialog."

@Path("/featuretoggle")
class FeatureToggleController @Inject constructor(private val unleashService: UnleashService) {

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    fun hentMedId(@PathParam("id") toggleId: String): Boolean =
            // TODO tilgangsstyring
            unleashService.isEnabled(sjekkPrefix(toggleId))


    fun sjekkPrefix(propertyKey: String): String {
        return if (propertyKey.contains(".")) propertyKey else APPLICATION_PREFIX + propertyKey
    }
}