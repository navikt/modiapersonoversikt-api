package no.nav.modiapersonoversikt.kafka

import no.nav.personoversikt.common.utils.EnvUtils

data class KafkaSecurityConfig(
    val aivenCredstorePassword: String = EnvUtils.getRequiredConfig("KAFKA_CREDSTORE_PASSWORD"),
    val aivenKeystoreLocation: String = EnvUtils.getRequiredConfig("KAFKA_KEYSTORE_PATH"),
    val aivenSecurityProtocol: String = EnvUtils.getRequiredConfig("KAFKA_SECURITY_PROTOCOL:SSL"),
    val aivenTruststoreLocation: String = EnvUtils.getRequiredConfig("KAFKA_TRUSTSTORE_PATH"),
    val aivenSchemaRegistryUrl: String = EnvUtils.getRequiredConfig("KAFKA_SCHEMA_REGISTRY"),
)
