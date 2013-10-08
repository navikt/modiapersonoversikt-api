package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.modig.pagelet.spi.ResourceReferences;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.ArrayList;
import java.util.List;

public class SoknaderListeResources implements ResourceReferences {
    @Override
    public List<CssResourceReference> getCssResourceReferences() {
        ArrayList<CssResourceReference> cssResourceReferences = new ArrayList<>();
        cssResourceReferences.add(SoknadListe.CSS_RESOURCE);
        return cssResourceReferences;
    }

    @Override
    public List<JavaScriptResourceReference> getJavaScriptResourceReferences() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Class<?>> getSpringConfiguration() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
