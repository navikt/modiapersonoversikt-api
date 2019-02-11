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
        baseUrls.add(BaseUrl(key="norg2-frontend", url = EnvironmentUtils.getRequiredProperty("server.norg2-frontend.url")))
        baseUrls.add(BaseUrl(key="gosys", url = EnvironmentUtils.getRequiredProperty("server.gosys.url")))
        baseUrls.add(BaseUrl(key="arena", url = EnvironmentUtils.getRequiredProperty("server.arena.url")))
        baseUrls.add(BaseUrl(key="drek", url = EnvironmentUtils.getRequiredProperty("server.drek.url")))
        baseUrls.add(BaseUrl(key="aktivitetsplan", url = EnvironmentUtils.getRequiredProperty("server.aktivitetsplan.url")))
        baseUrls.add(BaseUrl(key="pesys", url = EnvironmentUtils.getRequiredProperty("server.pesys.url")))
        baseUrls.add(BaseUrl(key="aareg", url = EnvironmentUtils.getRequiredProperty("server.aareg.url")))
        baseUrls.add(BaseUrl(key="veilarbportefoljeflatefs", url = EnvironmentUtils.getRequiredProperty("server.veilarbportefoljeflatefs.url")))

        return baseUrls
    }

    data class BaseUrl(val key: String = "",
                       val url: String = "")
}