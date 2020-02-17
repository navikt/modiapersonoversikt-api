package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {
    SAMPLE_FEATURE("feature.samplerfeature", false),
    SVAKSYNT_MODUS("modiabrukerdialog.svaksyntmodus", false),
    NAIS_GOSYS_LENKE("modiabrukerdialog.gosys-nais-lenke", false),
    NY_VARSEL("modiabrukerdialog.ny-varsel", false),
    NY_FRONTEND("modiabrukerdialog.ny-frontend", false),
    PDL_BRUKERPROFIL("modiabrukerdialog.pdl-ny-brukerprofil", true),
    ARBEIDSFORDELING_REST("modiabrukerdialog.arbeidsfordeling-rest", false)
}
