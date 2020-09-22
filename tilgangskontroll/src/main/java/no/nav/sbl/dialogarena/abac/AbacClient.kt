package no.nav.sbl.dialogarena.abac

import no.nav.common.rest.client.RestClient
import okhttp3.Credentials
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable

class AbacException(message: String) : RuntimeException(message)
data class AbacClientConfig(
        val username: String,
        val password: String,
        val endpointUrl: String
)

private infix fun Int.inRange(range: Pair<Int, Int>): Boolean = this >= range.first && this < range.second
val abacLogger: Logger = LoggerFactory.getLogger(AbacClient::class.java)

open class AbacClient(val config: AbacClientConfig) {
    private val basicCredential = Credentials.basic(config.username, config.password)
    private val client : OkHttpClient = RestClient.baseClient().newBuilder()
            .authenticator { _, response ->
                response
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", basicCredential)
                        .build()
            }
            .build()

    @Cacheable("abacClientCache")
    open fun evaluate(request: AbacRequest): AbacResponse {
        val requestJson = JsonMapper.serialize(request)
        val httpRequest = okhttp3.Request.Builder()
                .url(config.endpointUrl)
                .post(RequestBody.create(MediaType.parse("application/xacml+json"), requestJson))
                .build()
        val response = client.newCall(httpRequest).execute()

        if (response.code() inRange Pair(500, 600)) {
            abacLogger.warn("ABAC returned: ${response.code()} ${response.message()}")
            throw AbacException("An error has occured calling ABAC: ${response.message()}")
        } else if (response.code() inRange Pair(400, 500)) {
            abacLogger.warn("ABAC returned: ${response.code()} ${response.message()}")
            throw AbacException("An error has occured calling ABAC: ${response.code()}")
        }

        val responseJson = response.body()?.string()!!
        return JsonMapper.deserialize(responseJson, AbacResponse::class.java)
    }
}
