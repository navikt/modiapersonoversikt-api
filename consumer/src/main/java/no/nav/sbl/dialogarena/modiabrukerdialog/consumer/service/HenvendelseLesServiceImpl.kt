package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service

import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.common.oidc.SystemUserTokenProvider
import no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.SYSTEMUSER_PASSWORD
import no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.SYSTEMUSER_USERNAME
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.SECURITY_TOKEN_SERVICE_BASEURL
import no.nav.sbl.rest.RestUtils
import no.nav.sbl.util.EnvironmentUtils.getRequiredProperty
import no.nav.sbl.util.EnvironmentUtils.resolveSrvUserPropertyName

class HenvendelseLesServiceImpl : HenvendelseLesService {
    private val baseUrl: String = getRequiredProperty("HENVENDELSE_LES_API_URL")
    private val systemTokenProvider = SystemUserTokenProvider(
            SECURITY_TOKEN_SERVICE_BASEURL,
            getRequiredProperty(SYSTEMUSER_USERNAME, resolveSrvUserPropertyName()),
            getRequiredProperty(SYSTEMUSER_PASSWORD, resolveSrvUserPropertyName()),
            RestUtils.createClient()
    )

    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        val queryparams = byggQueryparams(
                "fnr" to fnr,
                "id" to behandlingsIder
        )
        return fetch("$baseUrl/behandlingsider?$queryparams")
    }

    override fun alleHenvendelseIderTilhorerBruker(fnr: String, henvendelseIder: List<String>): Boolean {
        val queryparams = byggQueryparams(
                "fnr" to fnr,
                "id" to henvendelseIder
        )
        return fetch("$baseUrl/henvendelseider?$queryparams")
    }

    private inline fun <reified T : Any> fetch(url: String): T {
        val token = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { RuntimeException("Fant ikke OIDC-token") }
        return RestUtils.withClient {
            it
                    .target(url)
                    .request()
                    .header("Authorization", "Bearer $token")
                    .header("SystemAuthorization", "Bearer ${systemTokenProvider.systemUserAccessToken}")
                    .get(T::class.java)
        }
    }

    private fun byggQueryparams(vararg pairs: Pair<String, Any>): String {
        return pairs
                .flatMap { pair ->
                    if (pair.second is Iterable<*>) {
                        (pair.second as Iterable<*>).map { Pair(pair.first, it.toString()) }
                    } else {
                        listOf(Pair(pair.first, pair.second.toString()))
                    }
                }
                .map { "${it.first}=${it.second}" }
                .joinToString("&")
    }
}
