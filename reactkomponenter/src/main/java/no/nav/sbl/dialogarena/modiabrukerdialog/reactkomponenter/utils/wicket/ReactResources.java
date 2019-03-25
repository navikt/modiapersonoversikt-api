package no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket;

import no.nav.modig.frontend.FrontendModule;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.ResourceReference;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

@SuppressWarnings("WeakerAccess")
public class ReactResources {
    public static final FrontendModule REACT_KOMPONENTER = new FrontendModule.With()
            .scripts(new JavaScriptResourceReference(ResourceReference.class, "build/reactkomponenter.js"))
            .stylesheets(new CssResourceReference(ResourceReference.class, "build/import.css"))
            .less(
                    new PackageResourceReference(ResourceReference.class, "build/nav-core-variabler.less"),
                    new PackageResourceReference(ResourceReference.class, "build/modal.less"),
                    new PackageResourceReference(ResourceReference.class, "build/redirect-modal.less"),
                    new PackageResourceReference(ResourceReference.class, "build/sok-layout.less"),
                    new PackageResourceReference(ResourceReference.class, "build/meldinger-sok.less"),
                    new PackageResourceReference(ResourceReference.class, "build/slaa-sammen-traader.less"),
                    new PackageResourceReference(ResourceReference.class, "build/knagginput.less"),
                    new PackageResourceReference(ResourceReference.class, "build/skrivestotte.less"),
                    new PackageResourceReference(ResourceReference.class, "build/journalforing-panel.less"),
                    new PackageResourceReference(ResourceReference.class, "build/pleiepenger-panel.less"),
                    new PackageResourceReference(ResourceReference.class, "build/varsel-module.less"),
                    new PackageResourceReference(ResourceReference.class, "build/saksoversikt-module.less"),
                    new PackageResourceReference(ResourceReference.class, "build/saksoversikt-module.less"),
                    new PackageResourceReference(ResourceReference.class, "build/delvis-svar.less"),
                    new PackageResourceReference(ResourceReference.class, "build/nav-kontor.less"),
                    new PackageResourceReference(ResourceReference.class, "build/traadvisning.less"),
                    new PackageResourceReference(ResourceReference.class, "build/tildelt-flere-oppgaver-alert.less"),
                    new PackageResourceReference(ResourceReference.class, "build/alertstripe.less"),
                    new PackageResourceReference(ResourceReference.class, "build/ny-frontend.less"),
                    new PackageResourceReference(ResourceReference.class, "build/ny-saksoversikt.less"),
                    new PackageResourceReference(ResourceReference.class, "build/ny-pleiepenger.less"),
                    new PackageResourceReference(ResourceReference.class, "build/ny-oppfolging.less"),
                    new PackageResourceReference(ResourceReference.class, "build/ny-foreldrepenger.less")
            ).done();
}
