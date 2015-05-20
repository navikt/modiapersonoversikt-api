package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.modia.metrics.TimingMetricsBehaviour;
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

	public static final ConditionalCssResource MODIA_LAYOUT_IE_CSS = new ConditionalCssResource(
			new CssResourceReference(BasePage.class, "css/felles/layout-ie9.css"), "screen", "lt IE 10");

    public static final ConditionalCssResource KJERNEINFO_IE9_CSS = new ConditionalCssResource(
            new CssResourceReference(BasePage.class, "css/komponenter/kjerneinfo_ie9.css"), "screen", "lt IE 10");

    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(PersonPage.class, "lokal.js");
    public static final CssResourceReference PERSONINFO_LESS = new CssResourceReference(PersonPage.class, "personpage.less");
    public static final CssResourceReference PERSONSOKRESULT = new CssResourceReference(PersonsokResultPanel.class, "PersonsokResultPanel.css");
    public static final CssResourceReference PERSONSOKSEARCH = new CssResourceReference(PersonsokSearchPanel.class, "PersonsokSearchPanel.css");
    public static final PackageResourceReference MODIA_COMMON_LESS = new PackageResourceReference(BasePage.class, "less/felles/common.less");
    public static final PackageResourceReference MODIA_WIDGET_LESS = new PackageResourceReference(BasePage.class, "less/felles/widget.less");
    public static final PackageResourceReference MODIA_LAMELL_LESS = new PackageResourceReference(BasePage.class, "less/felles/lamell.less");
    public static final PackageResourceReference MODIA_LAYOUT_LESS = new PackageResourceReference(BasePage.class, "less/felles/layout.less");
    public static final PackageResourceReference RESPONSIVE = new PackageResourceReference(BasePage.class, "less/felles/responsive.less");

    public static final PackageResourceReference HEADER = new PackageResourceReference(BasePage.class, "less/komponenter/header.less");
    public static final PackageResourceReference MELDINGERWIDGET = new PackageResourceReference(MeldingerWidget.class, "meldingerwidget.less");
    public static final PackageResourceReference MELDINGERLAMELL = new PackageResourceReference(Innboks.class, "innboks.less");
    public static final PackageResourceReference OPPFOLGING = new PackageResourceReference(BasePage.class, "less/komponenter/oppfolging.less");
    public static final PackageResourceReference OPPGAVEFORM = new PackageResourceReference(BasePage.class, "less/komponenter/oppgaveform.less");
    public static final PackageResourceReference PERSONSOK = new PackageResourceReference(BasePage.class, "less/komponenter/personsok.less");
    public static final PackageResourceReference SAKBEHANDLERINNSTILLINGER = new PackageResourceReference(BasePage.class, "less/komponenter/sakbehandlerinnstillinger.less");
    public static final PackageResourceReference LISTE = new PackageResourceReference(BasePage.class, "less/komponenter/liste.less");
    public static final PackageResourceReference JOURNALFORING = new PackageResourceReference(BasePage.class, "less/komponenter/journalforing.less");
    public static final PackageResourceReference BRUKERPROFIL = new PackageResourceReference(BasePage.class, "less/komponenter/brukerprofil.less");
    public static final PackageResourceReference HENTPERSON = new PackageResourceReference(BasePage.class, "less/komponenter/hentperson.less");
    public static final PackageResourceReference KJERNEINFO = new PackageResourceReference(BasePage.class, "less/komponenter/kjerneinfo.less");
    public static final PackageResourceReference OVERSIKT = new PackageResourceReference(BasePage.class, "less/komponenter/oversikt.less");
    public static final PackageResourceReference SYKEPENGER_FORELDREPENGER = new PackageResourceReference(BasePage.class, "less/komponenter/sykepenger_foreldrepenger.less");

    private final WebMarkupContainer body;

    public BasePage() {
        body = (WebMarkupContainer) new TransparentWebMarkupContainer("body").setOutputMarkupId(true);
        add(body);
        add(new TimingMetricsBehaviour().withPrefix("page."));
    }

    public WebMarkupContainer getBody() {
        return body;
    }
}
