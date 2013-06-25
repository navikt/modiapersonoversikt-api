package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.errorhandling.ModiaApplicationConfigurator;
import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.modia.liste.EkspanderingsListe;
import no.nav.modig.modia.liste.Liste;
import no.nav.modig.modia.navigation.KeyNavigationResourceReference;
import no.nav.modig.modia.shortcuts.ShortcutListenerResourceReference;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.pagelet.spi.utils.SPIResources;
import no.nav.modig.wicket.component.datepicker.DatePicker;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.modig.wicket.events.NamedEventDispatcher;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil.PageNotFound;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.selftest.SelfTestPage;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.settings.IMarkupSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

import java.util.Locale;

import static no.nav.modig.frontend.FrontendModules.MODIA;

public class WicketApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    public static WicketApplication get() {
        return (WicketApplication) Application.get();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HentPersonPage.class;
    }

    @Override
    protected void init() {
        super.init();
        if (usesDevelopmentConfig()) {
            getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
        }

        new FrontendConfigurator()
                .withModules(MODIA)
                .addMetas(
                        MetaTag.CHARSET_UTF8,
                        MetaTag.VIEWPORT_SCALE_1,
                        MetaTag.XUA_IE_EDGE)
                .withResourcePacking(this.usesDeploymentConfig())
                .addConditionalJavascript(Intern.RESPOND_JS)
                .addCss(SPIResources.getCss())
                .addCss(BasePage.CSS_MODUS)
                .addScripts(SPIResources.getScripts())
                .addScripts(BasePage.JS_RESOURCE)
                .addScripts(ShortcutListenerResourceReference.get()) //TODO: Flytt til MODIA modul ?
                .addScripts(KeyNavigationResourceReference.get())    //TODO: Flytt til MODIA modul ?
                .addScripts(Widget.JS_RESOURCE)                      //TODO: Flytt til MODIA modul ?
                .addScripts(EkspanderingsListe.JS_RESOURCE)          //TODO: Flytt til MODIA modul ?
                .addScripts(Liste.JS_RESOURCE)                       //TODO: Flytt til MODIA modul ?
                .addScripts(Intern.JQUERY_UI_JS, DatePicker.JQUERY_PLACEHOLDER)

                .configure(this);

        // Innstillinger vi bør ha
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        IMarkupSettings markupSettings = getMarkupSettings();
        markupSettings.setStripWicketTags(true);
        markupSettings.setStripComments(true);
        markupSettings.setCompressWhitespace(true);
        markupSettings.setDefaultMarkupEncoding("UTF-8");

        new ApplicationSettingsConfig().configure(this);

        getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
        Application.get().getRequestLoggerSettings().setRequestLoggerEnabled(true);

        getFrameworkSettings().add(new NamedEventDispatcher());

        new ModiaApplicationConfigurator()
                .withExceptionHandler(true)
                .configure(this);

        mountPage("/person/${fnr}", Intern.class);
        mountPage("internal/selftest", SelfTestPage.class);
        mountPage("/404", PageNotFound.class);

        setSpringComponentInjector();
    }

    @Override
    public Session newSession(Request request, Response response) {
        Session session = super.newSession(request, response);
        session.setLocale(new Locale("nb")); // Vis kun bokmaal i leveranse 1
        return session;
    }

    protected void setSpringComponentInjector() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }
}
