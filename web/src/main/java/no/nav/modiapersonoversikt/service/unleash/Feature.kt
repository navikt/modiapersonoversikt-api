package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_REST_KONTOREGISTER("modiabrukerdialog.rest.kontoregister.switcher"),
    SEND_HENVENDELSE_TO_KAFKA("modiapersonoversikt.henvendelse_kafka")
}
