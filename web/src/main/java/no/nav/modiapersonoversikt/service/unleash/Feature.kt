package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_SALESFORCE_DIALOG("modiabrukerdialog.bruker-salesforce-dialoger"),
    HENT_BISYS_SAKER("modiabrukerdialog.hent-bisys-saker"),
    PDL_PERSONSOK_RATE("modiabrukerdialog.science.pdl-personsok-rate"),
    SF_HENVENDELSE_RATE("modiabrukerdialog.science.sf-henvendelse-rate")
}
