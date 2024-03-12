package no.nav.modiapersonoversikt.service.soknadsstatus

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.datetime.LocalDateTime
import no.nav.modiapersonoversikt.utils.LocalDateTimeAsStringSerializer

data class Soknadsstatus(
    var underBehandling: Int = 0,
    var ferdigBehandlet: Int = 0,
    var avbrutt: Int = 0,
    @JsonSerialize(using = LocalDateTimeAsStringSerializer::class)
    var sistOppdatert: LocalDateTime? = null,
)
