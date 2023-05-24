package no.nav.modiapersonoversikt.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class HenvendelseKafkaConfig {
    @Bean
    open fun henvendelseProducer(
        @Value("\${KAFKA_BROKERS}") kafkaBrokerUrl: String,
        @Value("\${KAFKA_HENVENDELSE_OPPDATERING_TOPIC}") kafkaTopic: String
    ) = HenvendelseProducerImpl(kafkaBrokerUrl, kafkaTopic)
}
