package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {
    SAMPLE_FEATURE("feature.samplerfeature", false),
    NY_BRUKERPROFIL("modiabrukerdialog.ny-brukerprofil", true),
    SVAKSYNT_MODUS("modiabrukerdialog.svaksyntmodus", false),
    NY_UTBETALING("modiabrukerdialog.ny-utbetalinger", false),
    NY_SAKSOVERSIKT("modiabrukerdialog.ny-saksoversikt", false),
    NY_PLEIEPENGER("modiabrukerdialog.ny-pleiepenger", false),
    NY_SYKEPENGER("modiabrukerdialog.ny-sykepenger", false),
    NY_OPPFOLGING("modiabrukerdialog.ny-oppfolging", false)
}
