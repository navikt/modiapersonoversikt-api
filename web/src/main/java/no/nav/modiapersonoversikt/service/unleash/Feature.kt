package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_SALESFORCE_DIALOG("modiabrukerdialog.bruker-salesforce-dialoger"),
    STENG_STO("modiabrukerdialog.salesforce.steng-sto"),
    HENT_BISYS_SAKER("modiabrukerdialog.hent-bisys-saker")
}
