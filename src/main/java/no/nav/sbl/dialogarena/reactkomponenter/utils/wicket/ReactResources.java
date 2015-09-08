package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import no.nav.modig.frontend.FrontendModule;
import no.nav.sbl.dialogarena.reactkomponenter.ResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class ReactResources {
    public static final FrontendModule REACT_KOMPONENTER = new FrontendModule.With()
            .scripts(new JavaScriptResourceReference(ResourceReference.class, "build/reactkomponenter.js"))
            .less(
                    new PackageResourceReference(ResourceReference.class, "build/modal.less"),
                    new PackageResourceReference(ResourceReference.class, "build/sokLayout.less"),
                    new PackageResourceReference(ResourceReference.class, "build/meldingerSok.less"),
                    new PackageResourceReference(ResourceReference.class, "build/knagginput.less"),
                    new PackageResourceReference(ResourceReference.class, "build/skrivestotte.less"),
                    new PackageResourceReference(ResourceReference.class, "build/journalforingspanel.less")
            ).done();
}
