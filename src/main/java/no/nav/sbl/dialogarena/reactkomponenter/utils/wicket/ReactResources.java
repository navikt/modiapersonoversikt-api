package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import no.nav.modig.frontend.FrontendModule;
import no.nav.sbl.dialogarena.reactkomponenter.ResourceReference;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class ReactResources {
    public static final FrontendModule SKRIVESTOTTE = new FrontendModule.With()
            .scripts(new JavaScriptResourceReference(ResourceReference.class, "js/build/main.js"))
            .less(new PackageResourceReference(ResourceReference.class, "js/less/skrivestotte.less"))
            .done();
}
