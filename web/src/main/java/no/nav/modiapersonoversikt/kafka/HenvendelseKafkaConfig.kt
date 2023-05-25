package no.nav.modiapersonoversikt.kafka

import no.nav.modiapersonoversikt.kafka.dto.HenvendelseKafkaDTO
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
open class HenvendelseKafkaConfig {
    @Bean
    open fun henvendelseProducer(
        @Value("\${KAFKA_BROKERS}") kafkaBrokerUrl: String,
        @Value("\${KAFKA_HENVENDELSE_OPPDATERING_TOPIC}") kafkaTopic: String
    ): HenvendelseProducer {
        val props = Properties()
        commonProducerConfig(props = Properties(), kafkaBrokerUrl, "modiabrukerdialog-producer")
        val kafkaProducer = KafkaProducer(props, StringSerializer(), StringSerializer())
        return HenvendelseProducerImpl(
            temaSomSkalPubliseres = listOf("SYK"),
            KafkaPersonoversiktProducerImpl(kafkaProducer, kafkaTopic, HenvendelseKafkaDTO.serializer())
        )
    }
}
