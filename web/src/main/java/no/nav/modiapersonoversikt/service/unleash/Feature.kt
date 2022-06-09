package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    INTERNAL_ABAC_RATE("modiabrukerdialog.internal.abac.rate"),
    BRUK_CHAT("modiabrukerdialog.bruk_chat")
}
