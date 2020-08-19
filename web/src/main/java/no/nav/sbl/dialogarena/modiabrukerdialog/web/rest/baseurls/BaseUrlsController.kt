package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.baseurls

import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit.Companion.skipAuditLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/baseurls")
class BaseUrlsController @Autowired
constructor(private val tilgangskontroll: Tilgangskontroll) {

    @GetMapping("/")
    fun hent(): Map<String, Any?> {
        return tilgangskontroll.check(Policies.tilgangTilModia).get(skipAuditLog()) {
            mapOf("baseUrls" to getBaseUrls())
        }
    }

    private fun getBaseUrls(): List<BaseUrl> {
        val baseUrls = ArrayList<BaseUrl>()
        baseUrls.add(BaseUrl(key = "norg2-frontend", url = EnvironmentUtils.getRequiredProperty("SERVER_NORG2_FRONTEND_URL")))
        baseUrls.add(BaseUrl(key = "gosys", url = EnvironmentUtils.getRequiredProperty("SERVER_GOSYS_URL")))
        baseUrls.add(BaseUrl(key = "arena", url = EnvironmentUtils.getRequiredProperty("SERVER_ARENA_URL")))
        baseUrls.add(BaseUrl(key = "drek", url = EnvironmentUtils.getRequiredProperty("SERVER_DREK_URL")))
        baseUrls.add(BaseUrl(key = "aktivitetsplan", url = EnvironmentUtils.getRequiredProperty("SERVER_AKTIVITETSPLAN_URL")))
        baseUrls.add(BaseUrl(key = "pesys", url = EnvironmentUtils.getRequiredProperty("SERVER_PESYS_URL")))
        baseUrls.add(BaseUrl(key = "aareg", url = EnvironmentUtils.getRequiredProperty("SERVER_AAREG_URL")))
        baseUrls.add(BaseUrl(key = "veilarbportefoljeflatefs", url = EnvironmentUtils.getRequiredProperty("SERVER_VEILARBPORTEFOLJEFLATEFS_URL")))
        baseUrls.add(BaseUrl(key = "personforvalter", url = EnvironmentUtils.getRequiredProperty("PERSONFORVALTER_URL")))

        return baseUrls
    }

    data class BaseUrl(val key: String = "",
                       val url: String = "")
}
