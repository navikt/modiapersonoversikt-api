package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.modig.pagelet.spi.ResourceReferences;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareSporsmalConfig;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;


public class Resources implements ResourceReferences {

    @Override
    public List<CssResourceReference> getCssResourceReferences() {
        return emptyList();
    }

    @Override
    public List<JavaScriptResourceReference> getJavaScriptResourceReferences() {
        return emptyList();
    }

    @Override
    public List<Class<?>> getSpringConfiguration() {
        return asList(new Class<?>[] {BesvareSporsmalConfig.class});
    }

}
