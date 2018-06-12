package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.baseurls

import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON


@Path("/baseurls")
@Produces(APPLICATION_JSON)
class BaseUrlsController {

    @GET
    @Path("/")
    fun hent(): Map<String, Any?> {
        check(visFeature(PERSON_REST_API))

        return mapOf("norg2-frontend" to System.getProperty("server.norg2-frontend.url"),
                "gosys" to System.getProperty("server.gosys.url"),
                "arena" to System.getProperty("server.arena.url"),
                "drek" to System.getProperty("server.drek.url"),
                "aktivitetsplan" to System.getProperty("server.aktivitetsplan.url"),
                "pesys" to System.getProperty("server.pesys.url"),
                "aareg" to System.getProperty("server.aareg.url"),
                "veilarbportefoljeflatefs" to System.getProperty("server.veilarbportefoljeflatefs.url"))

    }

}