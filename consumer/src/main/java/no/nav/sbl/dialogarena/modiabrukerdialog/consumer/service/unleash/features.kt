package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {
    SAMPLE_FEATURE("feature.samplerfeature", false),
    NYTT_VISITTKORT("modiabrukerdialog.nytt-visittkort", false),
    NY_BRUKERPROFIL("modiabrukerdialog.ny-brukerprofil", false),
    SVAKSYNT_MODUS("modiabrukerdialog.svaksyntmodus", false),
    NY_UTBETALING("modiabrukerdialog.ny-utbetalinger", false),
    NY_SAKSOVERSIKT("modiabrukerdialog.ny-saksoversikt", false),
    TOOLTIP("modiabrukerdialog.tooltip", false)
}
