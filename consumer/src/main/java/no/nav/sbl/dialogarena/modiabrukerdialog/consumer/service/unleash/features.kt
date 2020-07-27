package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash

enum class Feature(val propertyKey: String, val defaultValue: Boolean) {
    SAMPLE_FEATURE("feature.samplerfeature", false),
    INFOMELDING("modiabrukerdialog.infomelding", false)
}
