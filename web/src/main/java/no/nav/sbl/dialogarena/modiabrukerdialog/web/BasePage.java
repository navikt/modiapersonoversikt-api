package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.personsok.result.PersonsokResultPanel;
import no.nav.personsok.search.PersonsokSearchPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class BasePage extends WebPage {

    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(PersonPage.class, "lokal.js");
	public static final CssResourceReference PERSONINFO_LESS = new CssResourceReference(PersonPage.class, "personpage.less");
	public static final CssResourceReference PERSONSOKRESULT = new CssResourceReference(PersonsokResultPanel.class, "PersonsokResultPanel.css");
	public static final CssResourceReference PERSONSOKSEARCH = new CssResourceReference(PersonsokSearchPanel.class, "PersonsokSearchPanel.css");
    public static final PackageResourceReference MODIA_COMMON_LESS = new PackageResourceReference(BasePage.class, "less/common.less");
    public static final PackageResourceReference MODIA_KOMPONENTER_LESS = new PackageResourceReference(BasePage.class, "less/komponenter.less");
    public static final PackageResourceReference MODIA_RAMME_LESS = new PackageResourceReference(BasePage.class, "less/ramme.less");

    private final WebMarkupContainer body;

    public BasePage() {
        body = (WebMarkupContainer) new TransparentWebMarkupContainer("body").setOutputMarkupId(true);
        add(body);
    }

    public WebMarkupContainer getBody() {
        return body;
    }
}
