package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling

enum class Feature private constructor(val propertyKey: String, val defaultValue: Boolean) {

    PERSON_REST_API("feature.aktiverPersonRestApi", false),
    DELVISE_SVAR("visDelviseSvarFunksjonalitet", false)

}
