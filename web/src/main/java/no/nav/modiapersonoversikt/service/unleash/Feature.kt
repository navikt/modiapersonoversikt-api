package no.nav.modiapersonoversikt.service.unleash

enum class Feature(
    val propertyKey: String,
) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    UTVIDET_UTBETALINGS_SPORRING("modiapersonoversikt.utvidet-utbetalings-sporring"),
    SKATTEETATEN_INNKREVING_API("modiapersonoversikt.skatteetaten-innkreving-api"),
    SKATTEETATEN_INNKREVING_API_MOCK("modiapersonoversikt.skatteetaten-innkreving-api-mock"),
    LOG_REQUEST_BODY("modiapersonoversikt.log-request-body"),
    LOG_RESPONSE_BODY("modiapersonoversikt.log-response-body"),
    TJENESTEKALL_LOGGING("modiapersonoversikt.tjenestekall-logging"),
}
