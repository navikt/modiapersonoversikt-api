package no.nav.modiapersonoversikt.utils

import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient

class DownstreamApi(
    val cluster: String,
    val namespace: String,
    val application: String,
) {
    companion object {
        @JvmStatic
        fun parse(value: String): DownstreamApi {
            val parts = value.split(":")
            check(parts.size == 3) { "DownstreamApi string must contain 3 parts" }

            val cluster = parts[0]
            val namespace = parts[1]
            val application = parts[2]

            return DownstreamApi(cluster = cluster, namespace = namespace, application = application)
        }
    }
}

private fun DownstreamApi.tokenscope(): String = "api://$cluster.$namespace.$application/.default"

fun MachineToMachineTokenClient.createMachineToMachineToken(api: DownstreamApi): String {
    return this.createMachineToMachineToken(api.tokenscope())
}

fun OnBehalfOfTokenClient.exchangeOnBehalfOfToken(
    api: DownstreamApi,
    accesstoken: String,
): String {
    return this.exchangeOnBehalfOfToken(api.tokenscope(), accesstoken)
}

interface BoundedMachineToMachineTokenClient {
    fun createMachineToMachineToken(): String
}

interface BoundedOnBehalfOfTokenClient {
    fun exchangeOnBehalfOfToken(accesstoken: String): String
}

fun MachineToMachineTokenClient.bindTo(api: DownstreamApi) =
    object : BoundedMachineToMachineTokenClient {
        override fun createMachineToMachineToken() = createMachineToMachineToken(api.tokenscope())
    }

fun OnBehalfOfTokenClient.bindTo(api: DownstreamApi) =
    object : BoundedOnBehalfOfTokenClient {
        override fun exchangeOnBehalfOfToken(accesstoken: String) = exchangeOnBehalfOfToken(api.tokenscope(), accesstoken)
    }
