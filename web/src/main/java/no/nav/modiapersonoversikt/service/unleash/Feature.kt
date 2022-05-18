package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    INTERNAL_ABAC_RATE("modiabrukerdialog.internal.abac.rate"),
    SVAR_LUKKER_DIALOG("modiabrukerdialog.svar.lukker.dialog"),
}
