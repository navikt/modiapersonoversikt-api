package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.modia.liste.EkspanderingsListe;
import no.nav.modig.modia.liste.Liste;
import no.nav.modig.modia.navigation.KeyNavigationResourceReference;
import no.nav.modig.modia.shortcuts.ShortcutListenerResourceReference;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.pagelet.spi.utils.SPIResources;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.selftest.SelfTestPage;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IApplicationSettings;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.settings.IMarkupSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static no.nav.modig.frontend.FrontendModules.MODIA;

public class WicketApplication extends WebApplication {

    @Autowired
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
                .addCss(SPIResources.getCss())
                .addScripts(SPIResources.getScripts())
                .addScripts(ShortcutListenerResourceReference.get())
                .addScripts(KeyNavigationResourceReference.get())
                .addScripts(BasePage.JS_RESOURCE)
                .addScripts(Widget.JS_RESOURCE)
                .addScripts(EkspanderingsListe.JS_RESOURCE)
                .addScripts(Liste.JS_RESOURCE)
                .configure(this);

        // Innstillinger vi bør ha
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        IMarkupSettings markupSettings = getMarkupSettings();
        markupSettings.setStripWicketTags(true);
        markupSettings.setStripComments(true);
        markupSettings.setCompressWhitespace(true);
        markupSettings.setDefaultMarkupEncoding("UTF-8");

        // Innstillinger vi kan ha
        IApplicationSettings applicationSettings = getApplicationSettings();
        applicationSettings.setPageExpiredErrorPage(getHomePage());

        getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
        Application.get().getRequestLoggerSettings().setRequestLoggerEnabled(true);

        mountPage("/person/${fnr}", Intern.class);
        mountPage("internal/selftest", SelfTestPage.class);

        setSpringComponentInjector();
    }

    protected void setSpringComponentInjector() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }
}
