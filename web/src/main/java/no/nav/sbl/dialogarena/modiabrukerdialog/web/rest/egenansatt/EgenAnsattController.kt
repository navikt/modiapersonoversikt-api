package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.egenansatt

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/egenansatt/{fnr}")
class EgenAnsattController @Inject constructor(private val egenAnsattService: EgenAnsattService, private val unleashService: UnleashService)  {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun erEgenAnsatt(@PathParam("fnr") fødselsnummer: String): Map<String, Boolean> {

        return mapOf("erEgenAnsatt" to egenAnsattService.erEgenAnsatt(fødselsnummer))
    }
}


