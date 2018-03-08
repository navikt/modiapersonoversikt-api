package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.modig.frontend.ConditionalCssResource;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public interface FancySelect {
    PackageResourceReference LESS = new PackageResourceReference(FancySelect.class, "FancySelect.less");
    ConditionalCssResource IECSS = new ConditionalCssResource(new CssResourceReference(FancySelect.class, "FancySelect-ie9.css"), "screen", "lt IE 10");
    JavaScriptResourceReference JS = new JavaScriptResourceReference(FancySelect.class, "jquery.combobox.js");
}
