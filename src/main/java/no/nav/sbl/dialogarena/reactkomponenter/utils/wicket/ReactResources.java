package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import no.nav.modig.frontend.FrontendModule;
import no.nav.sbl.dialogarena.reactkomponenter.ResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class ReactResources {
    public static final FrontendModule HENVENDELSE_SOK = new FrontendModule.With()
            .scripts(
                    new JavaScriptResourceReference(ResourceReference.class, "build/React.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/Utils.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/Modal.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/HenvendelseSok.js")
            )
            .less(
                    new PackageResourceReference(ResourceReference.class, "build/modal.less"),
                    new PackageResourceReference(ResourceReference.class, "build/sokLayout.less"),
                    new PackageResourceReference(ResourceReference.class, "build/henvendelseSok.less")
            )
            .done();

    public static final FrontendModule SKRIVESTOTTE = new FrontendModule.With()
            .scripts(
                    new JavaScriptResourceReference(ResourceReference.class, "build/React.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/Utils.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/Modal.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/Knagginput.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/Skrivestotte.js")
            )
            .less(
                    new PackageResourceReference(ResourceReference.class, "build/modal.less"),
                    new PackageResourceReference(ResourceReference.class, "build/sokLayout.less"),
                    new PackageResourceReference(ResourceReference.class, "build/knagginput.less"),
                    new PackageResourceReference(ResourceReference.class, "build/skrivestotte.less")
            )
            .done();

    public static final FrontendModule TAGINPUT = new FrontendModule.With()
            .scripts(
                    new JavaScriptResourceReference(ResourceReference.class, "build/React.js"),
                    new JavaScriptResourceReference(ResourceReference.class, "build/Knagginput.js")
            )
            .less(new PackageResourceReference(ResourceReference.class, "build/knagginput.less"))
            .done();
}
