package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_REST_KONTOREGISTER("modiabrukerdialog.rest.kontoregister"),
    REST_UTBETALING_EXPERIMENT_RATE("modiabrukerdialog.rest.experiment.utbetaling.rate"),
}
