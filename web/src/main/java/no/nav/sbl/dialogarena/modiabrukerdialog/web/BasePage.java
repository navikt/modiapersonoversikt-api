package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.brukerprofil.BrukerprofilPanel;
import no.nav.components.NOKAmountLabel;
import no.nav.kjerneinfo.Kjerneinfo;
import no.nav.kjerneinfo.kontrakter.KontrakterPanel;
import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.modia.metrics.TimingMetricsBehaviour;
import no.nav.modig.wicket.errorhandling.ExceptionHandlingBehavior;
import no.nav.personsok.PersonsokPanel;
import no.nav.personsok.result.PersonsokResultPanel;
import no.nav.personsok.search.PersonsokSearchPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tekniskfeil.ReactTekniskFeilModal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tempnaisgosys.GosysNaisLenke;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.apache.wicket.markup.head.OnDomReadyHeaderItem.forScript;

public class BasePage extends WebPage {
    private static String currentDomain = System.getProperty("current.domain", "");

    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);

    public static final ConditionalCssResource MODIA_LAYOUT_IE_CSS = new ConditionalCssResource(
            new CssResourceReference(BasePage.class, "css/felles/layout-ie9.css"), "screen", "lt IE 10");

    public static final ConditionalCssResource KJERNEINFO_IE9_CSS = new ConditionalCssResource(
            new CssResourceReference(Kjerneinfo.class, "kjerneinfo_ie9.css"), "screen", "lt IE 10");

    public static final UrlResourceReference INTERN_DECORATOR = new UrlResourceReference(Url.parse(currentDomain + "/internarbeidsflatedecorator/js/head.min.js"));
    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(BasePage.class, "lokal.js");
    public static final JavaScriptResourceReference JS_SESSION_TIMEOUT = new JavaScriptResourceReference(BasePage.class, "sessionTimeout.js");
    public static final JavaScriptResourceReference JS_TAB_POPUP_RESOURCE = new JavaScriptResourceReference(BasePage.class, "tabPopup.js");
    public static final JavaScriptResourceReference JS_AUTO_SCROLL_RESOURCE = new JavaScriptResourceReference(BasePage.class, "auto-scroll.js");
    public static final JavaScriptResourceReference JS_FRONTENDLOGGER_INIT = new JavaScriptResourceReference(BasePage.class, "frontendlogger-init.js");
    public static final UrlResourceReference JS_FRONTENDLOGGER = new UrlResourceReference(Url.parse(currentDomain + "/frontendlogger/logger.js"));
    public static final JavaScriptResourceReference JS_APPDYNAMICS_USER_MONITORING = new JavaScriptResourceReference(BasePage.class, "appdynamicsUM.js");
    public static final CssResourceReference PERSONINFO_LESS = new CssResourceReference(PersonPage.class, "personpage.less");
    public static final CssResourceReference NYTT_VISITTKORT_LESS = new CssResourceReference(PersonPage.class, "nyttvisittkort.less");
    public static final CssResourceReference PERSONSOKRESULT = new CssResourceReference(PersonsokResultPanel.class, "PersonsokResultPanel.css");
    public static final CssResourceReference PERSONSOKSEARCH = new CssResourceReference(PersonsokSearchPanel.class, "PersonsokSearchPanel.css");
    public static final PackageResourceReference MODIA_COMMON_LESS = new PackageResourceReference(BasePage.class, "less/felles/common.less");
    public static final PackageResourceReference MODIA_WIDGET_LESS = new PackageResourceReference(BasePage.class, "less/felles/widget.less");
    public static final PackageResourceReference MODIA_LAMELL_LESS = new PackageResourceReference(BasePage.class, "less/felles/lamell.less");
    public static final PackageResourceReference MODIA_LAYOUT_LESS = new PackageResourceReference(BasePage.class, "less/felles/layout.less");
    public static final PackageResourceReference MODIA_UTILITIES_LESS = new PackageResourceReference(BasePage.class, "less/felles/utilities.less");
    public static final PackageResourceReference RESPONSIVE = new PackageResourceReference(BasePage.class, "less/felles/responsive.less");

    public static final PackageResourceReference HEADER = new PackageResourceReference(BasePage.class, "less/komponenter/header.less");
    public static final PackageResourceReference MELDINGERWIDGET = new PackageResourceReference(MeldingerWidget.class, "meldingerwidget.less");
    public static final PackageResourceReference MELDINGERLAMELL = new PackageResourceReference(Innboks.class, "innboks.less");
    public static final PackageResourceReference KJERNEINFO_FELLES_LISTE = new PackageResourceReference(NOKAmountLabel.class, "felles-liste.less");
    public static final PackageResourceReference OPPFOLGING = new PackageResourceReference(KontrakterPanel.class, "oppfolging.less");
    public static final PackageResourceReference OPPGAVEFORM = new PackageResourceReference(BasePage.class, "less/komponenter/oppgaveform.less");
    public static final PackageResourceReference PERSONSOK_GENERELL = new PackageResourceReference(PersonsokPanel.class, "personsok_generell.less");
    public static final PackageResourceReference PERSONSOK_RESULT = new PackageResourceReference(PersonsokResultPanel.class, "personsok_result.less");
    public static final PackageResourceReference PERSONSOK_SEARCH = new PackageResourceReference(PersonsokSearchPanel.class, "personsok_search.less");
    public static final PackageResourceReference SAKBEHANDLERINNSTILLINGER = new PackageResourceReference(BasePage.class, "less/komponenter/sakbehandlerinnstillinger.less");
    public static final PackageResourceReference JOURNALFORING = new PackageResourceReference(BasePage.class, "less/komponenter/journalforing.less");
    public static final PackageResourceReference BRUKERPROFIL = new PackageResourceReference(BrukerprofilPanel.class, "brukerprofil.less");
    public static final PackageResourceReference HENTPERSON = new PackageResourceReference(BasePage.class, "less/komponenter/hentperson.less");
    public static final PackageResourceReference KJERNEINFO = new PackageResourceReference(Kjerneinfo.class, "kjerneinfo.less");
    public static final PackageResourceReference OVERSIKT = new PackageResourceReference(BasePage.class, "less/komponenter/oversikt.less");
    public static final PackageResourceReference VARSLING = new PackageResourceReference(BasePage.class, "less/komponenter/varsling.less");
    public static final PackageResourceReference SYKEPENGER_FORELDREPENGER = new PackageResourceReference(SykmeldingsperiodePanel.class, "sykepenger_foreldrepenger.less");

    private final WebMarkupContainer body;

    public BasePage(PageParameters pageParameters) {
        body = (WebMarkupContainer) new TransparentWebMarkupContainer("body").setOutputMarkupId(true);

        add(body);
        add(new TimingMetricsBehaviour().withPrefix("page."));

        ReactTekniskFeilModal tekniskFeilModal = new ReactTekniskFeilModal("tekniskFeil", pageParameters);
        add(tekniskFeilModal);

        add(new ExceptionHandlingBehavior() {
                @Override
                public IRequestHandler handleException(Component source, Exception ex) {
                    logger.error("Teknisk feil:", ex);
                    tekniskFeilModal.getModal().call("vis");
                    return RequestCycle.get().find(AjaxRequestTarget.class);
                }
            }
        );
    }

    public WebMarkupContainer getBody() {
        return body;
    }

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        FocusHandler.handleEvent(getPage(), event);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String script = format("createTabHandler('modiabrukerdialog', {hovedtekst: '%s', avbryttekst: '%s', fortsetttekst: '%s'});",
                new StringResourceModel("feilmelding.flerevinduer.hovedtekst", this, null).getString(),
                new StringResourceModel("feilmelding.flerevinduer.lenke.avbryt", this, null).getString(),
                new StringResourceModel("feilmelding.flerevinduer.lenke.fortsett", this, null).getString()
        );

        response.render(JavaScriptUrlReferenceHeaderItem.forReference(INTERN_DECORATOR));
        response.render(JavaScriptUrlReferenceHeaderItem.forReference(JS_FRONTENDLOGGER));
        response.render(forScript(script));
    }
}
