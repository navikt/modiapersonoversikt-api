package no.nav.modiapersonoversikt.service.unleash

enum class Feature(
    val propertyKey: String,
) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    UTVIDET_UTBETALINGS_SPORRING("modiapersonoversikt.utvidet-utbetalings-sporring"),
    LOG_REQUEST_BODY("modiapersonoversikt.log-request-body"),
    LOG_RESPONSE_BODY("modiapersonoversikt.log-response-body"),
    TJENESTEKALL_LOGGING("modiapersonoversikt.tjenestekall-logging"),
    VIS_14A_VEDTAK("modiapersonoversikt.vis-siste-14a-vedtak"),
}
