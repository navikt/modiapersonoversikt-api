package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    HENT_BISYS_SAKER("modiabrukerdialog.hent-bisys-saker"),
    BRUK_SKJERMET_PERSON("modiabrukerdialog.bruk-skjermet-person")
}
