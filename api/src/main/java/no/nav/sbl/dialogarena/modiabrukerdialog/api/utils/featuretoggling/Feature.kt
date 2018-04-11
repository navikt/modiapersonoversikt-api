package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {

    PERSON_REST_API("feature.aktiverPersonRestApi", false),
    ENHETER_GEOGRAFISK_TILKNYTNING_API("feature.aktiverEnheterGeografiskTilknytningApi", false),
    DELVISE_SVAR("visDelviseSvarFunksjonalitet", false)

}
