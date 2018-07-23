package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {

    SAMPLE_FEATURE("feature.samplerfeature", false),
    NYTT_VISITTKORT("modiabrukerdialog.nytt-visittkort", false),
    NY_BRUKERPROFIL("modiabrukerdialog.ny-brukerprofil", false),

}

