package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {

    SAMPLE_FEATURE("feature.samplerfeature", false),
    PERSON_REST_API("feature.aktiverPersonRestApi", false),
    ENHETER_GEOGRAFISK_TILKNYTNING_API("feature.aktiverEnheterGeografiskTilknytningApi", false),
    NYTT_VISITTKORT("feature.nyttvisittkort", false)
}

