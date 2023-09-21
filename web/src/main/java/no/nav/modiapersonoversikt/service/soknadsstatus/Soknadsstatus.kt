package no.nav.modiapersonoversikt.service.soknadsstatus

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Soknadsstatus(
    var underBehandling: Int = 0,
    var ferdigBehandlet: Int = 0,
    var avbrutt: Int = 0,
    var sistOppdatert: LocalDateTime? = null
)
