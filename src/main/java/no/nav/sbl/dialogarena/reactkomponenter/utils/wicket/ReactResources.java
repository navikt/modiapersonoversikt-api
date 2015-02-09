package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import no.nav.sbl.dialogarena.reactkomponenter.ResourceReference;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class ReactResources {
    public static final JavaScriptResourceReference SKRIVESTOTTE_JS = new JavaScriptResourceReference(ResourceReference.class, "js/build/main.js");
    public static final CssResourceReference SKRIVESTOTTE_CSS = new CssResourceReference(ResourceReference.class, "js/styles/main.css");

}
