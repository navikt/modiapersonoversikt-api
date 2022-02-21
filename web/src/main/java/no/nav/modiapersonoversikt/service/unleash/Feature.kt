package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_SALESFORCE_DIALOG("modiabrukerdialog.bruker-salesforce-dialoger"),
    USE_GRAPHQL_SAF("modiabrukerdialog.bruker-graphql-saf"),
    HENT_BISYS_SAKER("modiabrukerdialog.hent-bisys-saker"),
    SF_HENVENDELSE_RATE("modiabrukerdialog.science.sf-henvendelse-rate"),
    SAF_RATE("modiabrukerdialog.science.saf-rate")
}
