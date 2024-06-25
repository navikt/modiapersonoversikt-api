package no.nav.modiapersonoversikt.kafka

import no.nav.personoversikt.common.utils.EnvUtils

data class KafkaSecurityConfig(
    val aivenBootstrapServers: String = EnvUtils.getRequiredConfig("KAFKA_BROKERS"),
    val aivenCredstorePassword: String = EnvUtils.getRequiredConfig("KAFKA_CREDSTORE_PASSWORD"),
    val aivenKeystoreLocation: String = EnvUtils.getRequiredConfig("KAFKA_KEYSTORE_PATH"),
    val aivenSecurityProtocol: String = "SSL",
    val aivenTruststoreLocation: String = EnvUtils.getRequiredConfig("KAFKA_TRUSTSTORE_PATH"),
    val aivenSchemaRegistryUrl: String = EnvUtils.getRequiredConfig("KAFKA_SCHEMA_REGISTRY"),
    val aivenRegistryUser: String = EnvUtils.getRequiredConfig("KAFKA_SCHEMA_REGISTRY_USER"),
    val aivenRegistryPassword: String = EnvUtils.getRequiredConfig("KAFKA_SCHEMA_REGISTRY_PASSWORD"),
)
