package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.personsok.result.PersonsokResultPanel;
import no.nav.personsok.search.PersonsokSearchPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.TraadPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class BasePage extends WebPage {

    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(Intern.class, "lokal.js");
	public static final CssResourceReference INTERN_LESS = new CssResourceReference(Intern.class, "intern.less");
	public static final CssResourceReference PERSONSOKRESULT = new CssResourceReference(PersonsokResultPanel.class, "PersonsokResultPanel.css");
	public static final CssResourceReference PERSONSOKSEARCH = new CssResourceReference(PersonsokSearchPanel.class, "PersonsokSearchPanel.css");
	public static final CssResourceReference PERSONKJERNEINFO = new CssResourceReference(PersonKjerneinfoPanel.class, "PersonKjerneinfoPanel.css");
    public static final PackageResourceReference TRAADPANEL = new PackageResourceReference(TraadPanel.class, "besvare.less");
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
