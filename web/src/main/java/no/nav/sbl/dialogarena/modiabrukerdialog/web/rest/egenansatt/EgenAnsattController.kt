package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.egenansatt

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/egenansatt/{fnr}")
class EgenAnsattController @Inject constructor(
        private val egenAnsattService: EgenAnsattService,
        private val tilgangskontroll: Tilgangskontroll
) {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun erEgenAnsatt(@PathParam("fnr") fnr: String): Map<String, Boolean> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get {
                    mapOf("erEgenAnsatt" to egenAnsattService.erEgenAnsatt(fnr))
                }
    }
}


