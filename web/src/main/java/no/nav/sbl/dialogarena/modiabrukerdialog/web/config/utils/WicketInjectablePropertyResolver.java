package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils;

import no.nav.modig.content.ContentRetriever;

import javax.inject.Inject;
import javax.inject.Named;

public class WicketInjectablePropertyResolver {

    @Inject
    @Named("propertyResolver")
    private ContentRetriever propertyResolver;

    public String getProperty(String property) {
        return propertyResolver.hentTekst(property);
    }
}
