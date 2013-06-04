package no.nav.sbl.dialogarena;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.MetaTag;
import no.nav.sbl.dialogarena.pages.HomePage;
import no.nav.sbl.dialogarena.selftest.SelfTestPage;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IApplicationSettings;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.settings.IMarkupSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static no.nav.modig.frontend.FrontendModules.ALL;


public class WicketApplication extends WebApplication {

    @Autowired
    private ApplicationContext applicationContext;

    public static WicketApplication get() {
        return (WicketApplication) Application.get();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected void init() {

        super.init();

        new FrontendConfigurator()
                .withModules(ALL)
                .addMetas(
                        MetaTag.CHARSET_UTF8,
                        MetaTag.VIEWPORT_SCALE_1,
                        MetaTag.XUA_IE_EDGE)
                .addCss(BasePage.CSS_RESOURCE)
                .addScripts(BasePage.JS_RESOURCE)
                .withResourcePacking(this.usesDeploymentConfig())
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

        mountPage("internal/selftest", SelfTestPage.class);

        setSpringComponentInjector();
    }

    protected void setSpringComponentInjector() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }
}
