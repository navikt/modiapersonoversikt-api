package no.nav.modiapersonoversikt.kafka

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import java.util.*

private fun aivenSecurityProps(
    props: Properties,
    kafkaEnvironment: KafkaSecurityConfig,
) {
    props[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = kafkaEnvironment.aivenSecurityProtocol
    props[SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG] = ""
    props[SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG] = "jks"
    props[SslConfigs.SSL_KEYSTORE_TYPE_CONFIG] = "PKCS12"
    props[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] = kafkaEnvironment.aivenTruststoreLocation
    props[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] = kafkaEnvironment.aivenCredstorePassword
    props[SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG] = kafkaEnvironment.aivenKeystoreLocation
    props[SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG] = kafkaEnvironment.aivenCredstorePassword
    props[SslConfigs.SSL_KEY_PASSWORD_CONFIG] = kafkaEnvironment.aivenCredstorePassword
}

fun commonProducerConfig(props: Properties, brokerUrl: String, clientId: String) {
    props[ProducerConfig.ACKS_CONFIG] = "all"
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = brokerUrl
    props[ProducerConfig.CLIENT_ID_CONFIG] = clientId
    aivenSecurityProps(
        props,
        KafkaSecurityConfig()
    )
}
