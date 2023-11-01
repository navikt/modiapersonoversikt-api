package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_REST_KONTOREGISTER("modiapersonoversikt-api-rest-kontoregister"),
    UTVIDET_UTBETALINGS_SPORRING("modiapersonoversikt.utvidet-utbetalings-sporring"),
}
