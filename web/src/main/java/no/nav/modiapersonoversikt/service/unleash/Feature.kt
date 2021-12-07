package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_SALESFORCE_DIALOG("modiabrukerdialog.bruker-salesforce-dialoger"),
    USE_PDL_PERSONSOK("modiabrukerdialog.bruker-pdl-personsok"),
    HENT_BISYS_SAKER("modiabrukerdialog.hent-bisys-saker")
}
