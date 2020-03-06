package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgave

import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.sbl.rest.RestUtils
import javax.ws.rs.client.Client
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION




private open fun <T> gjorSporring(url: String, targetClass: Class<T>): T {
    val ssoToken = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { RuntimeException("Fant ikke OIDC-token") }
    return RestUtils.withClient { client: Client ->
        client
                .target(url)
                .request()
                .header(AUTHORIZATION, "Bearer $ssoToken")[targetClass]
    }
}