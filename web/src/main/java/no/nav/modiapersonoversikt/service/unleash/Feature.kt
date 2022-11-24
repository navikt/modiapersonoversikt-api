package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_REST_KONTOREGISTER("modiabrukerdialog.rest.kontoregister.switcher"),
    USE_REST_CLIENT_METRICS("modiapersonoversikt.use_rest_client_metrics"),
}
