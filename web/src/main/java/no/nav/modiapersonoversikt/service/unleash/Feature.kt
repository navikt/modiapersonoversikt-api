package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    UTVIDET_UTBETALINGS_SPORRING("modiapersonoversikt.utvidet-utbetalings-sporring"),
    TMS_EVENT_API_UPDATE("modiapersonoversikt.tms.api.update"),
}
