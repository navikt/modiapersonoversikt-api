package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.baseurls

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.util.EnvironmentUtils
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
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        return mapOf("baseUrls" to getBaseUrls())
    }

    private fun getBaseUrls(): List<BaseUrl> {
        val baseUrls = ArrayList<BaseUrl>()
        baseUrls.add(BaseUrl(key="norg2-frontend", url = System.getProperty("server.norg2-frontend.url")))
        baseUrls.add(BaseUrl(key="gosys", url = System.getProperty("server.gosys.url")))
        baseUrls.add(BaseUrl(key="arena", url = System.getProperty("server.arena.url")))
        baseUrls.add(BaseUrl(key="drek", url = System.getProperty("server.drek.url")))
        baseUrls.add(BaseUrl(key="aktivitetsplan", url = System.getProperty("server.aktivitetsplan.url")))
        baseUrls.add(BaseUrl(key="pesys", url = System.getProperty("server.pesys.url")))
        baseUrls.add(BaseUrl(key="aareg", url = System.getProperty("server.aareg.url")))
        baseUrls.add(BaseUrl(key="veilarbportefoljeflatefs", url = System.getProperty("server.veilarbportefoljeflatefs.url")))

        return baseUrls
    }

    data class BaseUrl(val key: String = "",
                       val url: String = "")
}