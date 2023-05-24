package no.nav.modiapersonoversikt.kafka.dto
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class HenvendelseKafkaDTO(
    val fnr: String,
    val tema: String?,
    val tidspunkt: Instant
)
