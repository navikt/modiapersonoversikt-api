package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash

enum class Feature(val propertyKey: String, val defaultValue: Boolean) {
    SAMPLE_FEATURE("feature.samplerfeature", false),
    NY_BACKEND("modiabrukerdialog.ny-backend", false),
    ARBEIDSFORDELING_REST("modiabrukerdialog.arbeidsfordeling-rest", false),
    BIDRAG_SAK_HACK("modiabrukerdialog.bidrag-sak-hack", false)
}
