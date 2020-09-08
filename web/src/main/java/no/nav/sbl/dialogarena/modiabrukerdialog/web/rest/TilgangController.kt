package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.abac.AbacClient
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision
import no.nav.sbl.dialogarena.abac.DenyCause
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.AbacPolicies
import java.text.ParseException
import java.util.*
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

    @GET
    @Path("/auth")
    fun authIntropection(): AuthIntropectionDTO {
        return SubjectHandler.getSsoToken()
                .map(SsoToken::getExpirationDate)
                .orElse(AuthIntropectionDTO.INVALID)
    }

}

class TilgangDTO(val harTilgang: Boolean, val ikkeTilgangArsak: DenyCause?)
class AuthIntropectionDTO(val expirationDate: Long) {
    companion object {
        val INVALID = AuthIntropectionDTO(-1)
    }
}

internal fun SsoToken.getExpirationDate(): AuthIntropectionDTO {
    val exp: Any? = this.attributes["exp"]
    return when(exp) {
        null -> AuthIntropectionDTO.INVALID
        is Date -> AuthIntropectionDTO(exp.time)
        is Number -> AuthIntropectionDTO(exp.toLong() * 1000) // Er epoch-seconds, men vil ha epoch-ms
        else -> throw ParseException("The \"exp\" claim is not a Date", 0)
    }
}

internal fun AbacResponse.makeResponse(): TilgangDTO {
    if (this.getBiasedDecision(Decision.Deny) == Decision.Permit) {
        return TilgangDTO(true, null)
    }

    return TilgangDTO(false, this.getCause())
}
