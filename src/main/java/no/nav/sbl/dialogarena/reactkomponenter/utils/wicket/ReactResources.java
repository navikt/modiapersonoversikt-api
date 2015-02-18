package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import no.nav.modig.frontend.FrontendModule;
import no.nav.sbl.dialogarena.reactkomponenter.ResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class ReactResources {
    public static final FrontendModule SKRIVESTOTTE = new FrontendModule.With()
            .scripts(
                    new JavaScriptResourceReference(ResourceReference.class, "build/nav-react.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/modal.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/knagginput.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/skrivestotte.js")
            )
            .less(
                    new PackageResourceReference(ResourceReference.class, "build/modal.less"),
                    new PackageResourceReference(ResourceReference.class, "build/knagginput.less"),
                    new PackageResourceReference(ResourceReference.class, "build/skrivestotte.less")
            )
            .done();

    public static final FrontendModule TAGINPUT = new FrontendModule.With()
            .scripts(
                    new JavaScriptResourceReference(ResourceReference.class, "build/nav-react.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/knagginput.js")
            )
            .less(new PackageResourceReference(ResourceReference.class, "build/knagginput.less"))
            .done();
}
