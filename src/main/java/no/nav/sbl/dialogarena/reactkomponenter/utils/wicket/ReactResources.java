package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import no.nav.modig.frontend.FrontendModule;
import no.nav.sbl.dialogarena.reactkomponenter.ResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class ReactResources {
    public static final FrontendModule SKRIVESTOTTE = new FrontendModule.With()
            .scripts(new JavaScriptResourceReference(ResourceReference.class, "build/skrivestotte.js"))
            .less(new PackageResourceReference(ResourceReference.class, "build/skrivestotte.less"))
            .done();
}
