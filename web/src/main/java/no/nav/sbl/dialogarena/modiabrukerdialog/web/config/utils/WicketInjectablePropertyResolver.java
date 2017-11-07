package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils;

import no.nav.modig.content.PropertyResolver;

import javax.inject.Inject;

public class WicketInjectablePropertyResolver {

    @Inject
    private PropertyResolver propertyResolver;

    public String getProperty(String property) {
        return propertyResolver.getProperty(property);
    }
}
