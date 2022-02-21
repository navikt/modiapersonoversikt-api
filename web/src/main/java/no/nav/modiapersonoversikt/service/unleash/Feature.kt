package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_GRAPHQL_SAF("modiabrukerdialog.bruker-graphql-saf"),
    HENT_BISYS_SAKER("modiabrukerdialog.hent-bisys-saker"),
    PDL_PERSONSOK_RATE("modiabrukerdialog.science.pdl-personsok-rate"),
    SAF_RATE("modiabrukerdialog.science.saf-rate")
}
