package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.personsok.result.PersonsokResultPanel;
import no.nav.personsok.search.PersonsokSearchPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class BasePage extends WebPage {

	public static final ConditionalCssResource MODIA_FLEXBOX_IE_CSS = new ConditionalCssResource(
			new CssResourceReference(BasePage.class, "css/flexbox-ie9.css"), "screen", "lt IE 10");

    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(PersonPage.class, "lokal.js");
    public static final CssResourceReference PERSONINFO_LESS = new CssResourceReference(PersonPage.class, "personpage.less");
    public static final CssResourceReference PERSONSOKRESULT = new CssResourceReference(PersonsokResultPanel.class, "PersonsokResultPanel.css");
    public static final CssResourceReference PERSONSOKSEARCH = new CssResourceReference(PersonsokSearchPanel.class, "PersonsokSearchPanel.css");
    public static final PackageResourceReference MODIA_COMMON_LESS = new PackageResourceReference(BasePage.class, "less/common.less");
    public static final PackageResourceReference MODIA_KOMPONENTER_LESS = new PackageResourceReference(BasePage.class, "less/komponenter.less");
    public static final PackageResourceReference MODIA_RAMME_LESS = new PackageResourceReference(BasePage.class, "less/ramme.less");
    public static final PackageResourceReference MODIA_FLEXBOX_LESS = new PackageResourceReference(BasePage.class, "less/flexbox.less");
    public static final PackageResourceReference MELDINGERWIDGET = new PackageResourceReference(MeldingerWidget.class, "meldingerwidget.less");
    public static final PackageResourceReference MELDINGERLAMELL = new PackageResourceReference(Innboks.class, "innboks.less");

    private final WebMarkupContainer body;

    public BasePage() {
        body = (WebMarkupContainer) new TransparentWebMarkupContainer("body").setOutputMarkupId(true);
        add(body);
    }

    public WebMarkupContainer getBody() {
        return body;
    }
}
