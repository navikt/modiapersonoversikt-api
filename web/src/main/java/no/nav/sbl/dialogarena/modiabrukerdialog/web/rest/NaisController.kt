package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Path("/internal")
class NaisController {

    @Path("/isReady")
    fun isReady(): Response = Response.ok().build()

    @Path("/isAlive")
    fun isAlive(): Response = Response.ok().build()

}