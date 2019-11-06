package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.baseurls

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON


@Path("/baseurls")
@Produces(APPLICATION_JSON)
class BaseUrlsController @Inject
constructor(private val unleashService: UnleashService) {

    @GET
    @Path("/")
    fun hent(): Map<String, Any?> {
        return mapOf("baseUrls" to getBaseUrls())
    }

    private fun getBaseUrls(): List<BaseUrl> {
        val baseUrls = ArrayList<BaseUrl>()
        baseUrls.add(BaseUrl(key="norg2-frontend", url = System.getProperty("SERVER_NORG2_FRONTEND_URL")))
        baseUrls.add(BaseUrl(key="gosys", url = System.getProperty("SERVER_GOSYS_URL")))
        baseUrls.add(BaseUrl(key="arena", url = System.getProperty("SERVER_ARENA_URL")))
        baseUrls.add(BaseUrl(key="drek", url = System.getProperty("SERVER_DREK_URL")))
        baseUrls.add(BaseUrl(key="aktivitetsplan", url = System.getProperty("SERVER_AKTIVITETSPLAN_URL")))
        baseUrls.add(BaseUrl(key="pesys", url = System.getProperty("SERVER_PESYS_URL")))
        baseUrls.add(BaseUrl(key="aareg", url = System.getProperty("SERVER_AAREG_URL")))
        baseUrls.add(BaseUrl(key="veilarbportefoljeflatefs", url = System.getProperty("SERVER_VEILARBPORTEFOLJEFLATEFS_URL")))

        return baseUrls
    }

    data class BaseUrl(val key: String = "",
                       val url: String = "")
}