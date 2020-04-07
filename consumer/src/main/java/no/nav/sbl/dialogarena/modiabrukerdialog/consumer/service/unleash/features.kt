package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {
    SAMPLE_FEATURE("feature.samplerfeature", false),
    SVAKSYNT_MODUS("modiabrukerdialog.svaksyntmodus", false),
    NY_FRONTEND("modiabrukerdialog.ny-frontend", false),
    NY_BACKEND("modiabrukerdialog.ny-backend", false),
    ARBEIDSFORDELING_REST("modiabrukerdialog.arbeidsfordeling-rest", false)
}
