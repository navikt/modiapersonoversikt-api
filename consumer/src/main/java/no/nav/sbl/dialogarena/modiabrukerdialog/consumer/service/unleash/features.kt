package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {
    SAMPLE_FEATURE("feature.samplerfeature", false),
    SVAKSYNT_MODUS("modiabrukerdialog.svaksyntmodus", false),
    NY_SAKSOVERSIKT("modiabrukerdialog.ny-saksoversikt", false),
    NY_PLEIEPENGER("modiabrukerdialog.ny-pleiepenger", false),
    NY_SYKEPENGER("modiabrukerdialog.ny-sykepenger", false),
    NY_OPPFOLGING("modiabrukerdialog.ny-oppfolging", false),
    NY_FORELDREPENGER("modiabrukerdialog.ny-foreldrepenger", false),
    NAIS_GOSYS_LENKE("modiabrukerdialog.gosys-nais-lenke", false),
    NY_VARSEL("modiabrukerdialog.ny-varsel", false)
}
