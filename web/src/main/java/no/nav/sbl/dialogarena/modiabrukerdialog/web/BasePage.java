package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class BasePage extends WebPage {

    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(Intern.class, "lokal.js");
    public static final PackageResourceReference LESS_RESOURCE = new PackageResourceReference(Intern.class, "intern.less");

    private final WebMarkupContainer body;

    public BasePage() {
        body = (WebMarkupContainer) new TransparentWebMarkupContainer("body").setOutputMarkupId(true);
        add(body);
    }

    public WebMarkupContainer getBody() {
        return body;
    }
}
