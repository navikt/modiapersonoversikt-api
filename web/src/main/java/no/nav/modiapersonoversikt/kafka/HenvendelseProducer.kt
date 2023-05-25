package no.nav.modiapersonoversikt.kafka

import kotlinx.datetime.toKotlinInstant
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.kafka.dto.HenvendelseKafkaDTO
import no.nav.personoversikt.common.logging.Logging
import java.util.*

interface HenvendelseProducer {
    fun sendHenvendelseUpdate(henvendelse: HenvendelseDTO)
}

class HenvendelseProducerImpl(
    private val temaSomSkalPubliseres: List<String>,
    private val producer: KafkaPersonoversiktProducer<HenvendelseKafkaDTO>
) : HenvendelseProducer {

    override fun sendHenvendelseUpdate(henvendelse: HenvendelseDTO) {
        val tema = henvendelse.gjeldendeTema ?: return

        val message = HenvendelseKafkaDTO(
            fnr = henvendelse.fnr,
            tema = tema,
            tidspunkt = henvendelse.opprettetDato.toInstant().toKotlinInstant()
        )
        if (temaSomSkalPubliseres.contains(message.tema)) {
            try {
                producer.sendRecord(message = message)
            } catch (e: Exception) {
                Logging.secureLog.error("Klarte ikke å sende henvendelse melding: $message", e)
            }
        }
    }
}
