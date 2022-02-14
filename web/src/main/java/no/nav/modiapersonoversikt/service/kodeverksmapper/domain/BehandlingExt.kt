package no.nav.modiapersonoversikt.service.kodeverksmapper.domain

fun Behandling.asV2BehandlingString(): String {
    return "${this.behandlingstema ?: ""}:${this.behandlingstype ?: ""}"
}

fun String.parseV2BehandlingString(): Behandling {
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
