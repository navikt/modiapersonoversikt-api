package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling;

public enum Feature {

    DELVISE_SVAR("visDelviseSvarFunksjonalitet", false);

    public final String propertyKey;
    public final boolean defaultValue;

    Feature(String propertyKey, boolean defaultValue) {
        this.propertyKey = propertyKey;
        this.defaultValue = defaultValue;
    }

}
