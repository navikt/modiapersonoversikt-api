package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import no.nav.sbl.dialogarena.abac.AbacClient
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision
import no.nav.sbl.dialogarena.abac.DenyCause
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.AbacPolicies
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/tilgang")
@Produces(APPLICATION_JSON)
class TilgangController @Inject constructor(private val abacClient: AbacClient) {

    @GET
    @Path("/{fnr}")
    fun harTilgang(@PathParam("fnr") fnr: String): TilgangDTO {
        return abacClient
                .evaluate(AbacPolicies.tilgangTilBruker(fnr))
                .makeResponse()
    }

    @GET
    fun harTilgang(): TilgangDTO {
        return abacClient
                .evaluate(AbacPolicies.tilgangTilModia())
                .makeResponse()
    }

}

class TilgangDTO(val harTilgang: Boolean, val ikkeTilgangArsak: DenyCause?)

internal fun AbacResponse.makeResponse(): TilgangDTO {
    if (this.getBiasedDecision(Decision.Deny) == Decision.Permit) {
        return TilgangDTO(true, null)
    }

    return TilgangDTO(false, this.getCause())
}
