package no.nav.sbl.dialogarena.abac

import no.nav.sbl.rest.RestUtils
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature.basic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import javax.ws.rs.ClientErrorException
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity.entity

class AbacException(message: String) : RuntimeException(message)
data class AbacClientConfig(
        val username: String,
        val password: String,
        val endpointUrl: String
)

private infix fun Int.inRange(range: Pair<Int, Int>): Boolean = this >= range.first && this < range.second
private val log: Logger = LoggerFactory.getLogger(AbacClient::class.java)

open class AbacClient(val config: AbacClientConfig) {
    private val client : Client = RestUtils.createClient().register(basic(config.username, config.password))

    @Cacheable("abacClientCache")
    open fun evaluate(request: AbacRequest): AbacResponse {
        val requestJson = JsonMapper.serialize(request)
        val response = client.target(config.endpointUrl)
                .request()
                .post(entity(requestJson, "application/xacml+json"))

        if (response.status inRange Pair(500, 600)) {
            log.warn("ABAC returned: ${response.status} ${response.statusInfo.reasonPhrase}")
            throw AbacException("An error has occured calling ABAC: ${response.statusInfo.reasonPhrase}")
        } else if (response.status inRange Pair(400, 500)) {
            log.warn("ABAC returned: ${response.status} ${response.statusInfo.reasonPhrase}")
            throw ClientErrorException("An error has occured calling ABAC:", response.status)
        }

        val responseJson = response.readEntity(String::class.java)
        return JsonMapper.deserialize(responseJson, AbacResponse::class.java)
    }
}
