package no.nav.sbl.dialogarena.sporsmalogsvar;

import java.util.List;
import no.nav.modig.pagelet.spi.ResourceReferences;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;


public class Resources implements ResourceReferences {

    @Override
    public List<CssResourceReference> getCssResourceReferences() {
        return asList(new CssResourceReference(Resources.class, "stylesheets/innboks.css"));
    }

    @Override
    public List<JavaScriptResourceReference> getJavaScriptResourceReferences() {
        return emptyList();
    }

    @Override
    public List<Class<?>> getSpringConfiguration() {
        return emptyList();
//        return asList(new Class<?>[] {BesvareSporsmalConfig.class});
    }

}
