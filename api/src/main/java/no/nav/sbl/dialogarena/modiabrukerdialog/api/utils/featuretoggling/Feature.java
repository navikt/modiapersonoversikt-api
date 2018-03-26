package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling;

public enum Feature {

    PERSON_REST_API("feature.aktiverPersonRestApi", false),
    DELVISE_SVAR("visDelviseSvarFunksjonalitet", false);

    public final String propertyKey;
    public final boolean defaultValue;

    Feature(String propertyKey, boolean defaultValue) {
        this.propertyKey = propertyKey;
        this.defaultValue = defaultValue;
    }

}
