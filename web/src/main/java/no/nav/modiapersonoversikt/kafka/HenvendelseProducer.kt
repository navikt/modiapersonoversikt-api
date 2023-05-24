package no.nav.modiapersonoversikt.kafka

import kotlinx.datetime.toKotlinInstant
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.kafka.dto.HenvendelseKafkaDTO
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

interface HenvendelseProducer {
    fun sendHenvendelseUpdate(henvendelse: HenvendelseDTO)
}

class HenvendelseProducerImpl(
    kafkaBrokerUrl: String,
    kafkaTopic: String,
    private val temaSomSkalPubliseres: List<String>
) : HenvendelseProducer {
    private val producer: KafkaPersonoversiktProducer<HenvendelseKafkaDTO>

    init {
        val props = Properties()
        commonProducerConfig(props = Properties(), kafkaBrokerUrl, "modiabrukerdialog-producer")
        val kafkaProducer = KafkaProducer(props, StringSerializer(), StringSerializer())
        producer = KafkaPersonoversiktProducerImpl(kafkaProducer, kafkaTopic, HenvendelseKafkaDTO.serializer())
    }

    override fun sendHenvendelseUpdate(henvendelse: HenvendelseDTO) {
        val message = HenvendelseKafkaDTO(
            fnr = henvendelse.fnr,
            tema = henvendelse.gjeldendeTema,
            tidspunkt = henvendelse.opprettetDato.toInstant().toKotlinInstant()
        )
        if (temaSomSkalPubliseres.contains(message.tema)) {
            producer.sendRecord(message = message)
        }
    }
}
