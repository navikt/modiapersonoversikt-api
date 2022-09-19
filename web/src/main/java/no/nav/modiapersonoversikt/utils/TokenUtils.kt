package no.nav.modiapersonoversikt.utils

import no.nav.common.token_client.client.MachineToMachineTokenClient

class DownstreamApi(
    val cluster: String,
    val namespace: String,
    val application: String,
) { companion object }

private fun DownstreamApi.tokenscope(): String = "api://$cluster.$namespace.$application/.default"

fun MachineToMachineTokenClient.createMachineToMachineToken(api: DownstreamApi): String {
    return this.createMachineToMachineToken(api.tokenscope())
}

interface BoundedMachineToMachineTokenClient {
    fun createMachineToMachineToken(): String
}
fun MachineToMachineTokenClient.bindTo(api: DownstreamApi) = object : BoundedMachineToMachineTokenClient {
    override fun createMachineToMachineToken() = createMachineToMachineToken(api.tokenscope())
}
