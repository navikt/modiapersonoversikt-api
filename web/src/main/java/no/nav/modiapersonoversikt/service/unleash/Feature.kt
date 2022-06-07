package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    BRUK_ARENA_SAK_VEDTAK_SERVICE("modiabrukerdialog.bruk_arenaSakVedtakService"),
    INTERNAL_ABAC_RATE("modiabrukerdialog.internal.abac.rate"),
}
