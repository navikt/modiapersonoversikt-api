package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling;

public enum Feature {

    DELVISE_SVAR("visDelviseSvarFunksjonalitet", false),
    PLEIEPENGER("visPleiepenger", true),
    ENDRE_NAVN("visEndreNavnFunksjonalitet", false);

    public final String propertyKey;
    public final boolean defaultValue;

    Feature(String propertyKey, boolean defaultValue) {
        this.propertyKey = propertyKey;
        this.defaultValue = defaultValue;
    }

}
