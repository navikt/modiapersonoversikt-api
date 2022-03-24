package no.nav.modiapersonoversikt.legacy.api.domain

data class Behandling(
    val behandlingstema: String?,
    val behandlingstype: String?
)

fun String.parseV2BehandlingString(): Behandling {
    if (this.isBlank()) {
        return Behandling(null, null)
    }
    val temaOgType = this.split(":").map {
        it.ifEmpty { null }
    }
    require(temaOgType.size == 2) {
        "Behandling krever formatet '<tema>:<type>', men fikk '$this'"
    }

    return Behandling(
        temaOgType[0],
        temaOgType[1]
    )
}
