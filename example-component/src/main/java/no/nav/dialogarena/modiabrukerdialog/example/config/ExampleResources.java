package no.nav.dialogarena.modiabrukerdialog.example.config;

import java.util.Arrays;
import java.util.List;

import no.nav.modig.pagelet.spi.ResourceReferences;

import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class ExampleResources implements ResourceReferences {

    @Override
    public List<CssResourceReference> getCssResourceReferences() {
        return null;
    }

    @Override
    public List<JavaScriptResourceReference> getJavaScriptResourceReferences() {
        return null;
    }

    @Override
    public List<Class<?>> getSpringConfiguration() {
        return Arrays.asList(new Class<?>[] {ExampleProdContext.class});
    }

}
