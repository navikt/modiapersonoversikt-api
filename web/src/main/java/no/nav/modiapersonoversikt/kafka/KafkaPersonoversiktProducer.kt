package no.nav.modiapersonoversikt.kafka

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.UUID

interface KafkaPersonoversiktProducer<MESSAGE_TYPE> {
    fun sendRecord(key: String? = UUID.randomUUID().toString(), message: MESSAGE_TYPE)
}

class KafkaPersonoversiktProducerImpl<MESSAGE_TYPE>(
    private val producer: Producer<String, String>,
    private val topic: String,
    private val messageSerializer: SerializationStrategy<MESSAGE_TYPE>
) : KafkaPersonoversiktProducer<MESSAGE_TYPE> {
    override fun sendRecord(key: String?, message: MESSAGE_TYPE) {
        val encodedMessage = Json.encodeToString(messageSerializer, message)
        producer.send(ProducerRecord(topic, key, encodedMessage))
    }
}
