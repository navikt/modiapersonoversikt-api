package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_GRAPHQL_SAF("modiabrukerdialog.bruker-graphql-saf"),
    HENT_BISYS_SAKER("modiabrukerdialog.hent-bisys-saker"),
    SAF_RATE("modiabrukerdialog.science.saf-rate")
}
