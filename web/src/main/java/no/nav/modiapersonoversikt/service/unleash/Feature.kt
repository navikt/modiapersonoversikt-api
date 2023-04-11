package no.nav.modiapersonoversikt.service.unleash

enum class Feature(val propertyKey: String) {
    SAMPLE_FEATURE("feature.samplerfeature"),
    USE_REST_KONTOREGISTER("modiabrukerdialog.rest.kontoregister.switcher"),
    USE_NEW_DIALOG_VISNING("modiapersonoversikt.vis-ny-meldingsvisning"),
    VIS_REVARSLING("modiapersonoversikt.modiapersonoversikt-revarsling-visning")
}
