package no.nav.sbl.dialogarena.utbetaling;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.FrontendModules;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

public class UtbetalingApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public Class<? extends Page> getHomePage() {
        return UtbetalingTestPage.class;
    }

    protected void setupSpringInjector() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }

    @Override
    protected void init() {
        setupSpringInjector();
        getMarkupSettings().setStripWicketTags(true);

        //        Datoformat.brukLocaleFra(LocaleFromWicketSession.INSTANCE);

        new ApplicationSettingsConfig().configure(this);
        new FrontendConfigurator()
                .withModules(FrontendModules.MODIA)
                .addMetas(
                        MetaTag.CHARSET_UTF8,
                        MetaTag.VIEWPORT_SCALE_1,
                        new MetaTag.With()
                                .attribute("http-equiv", "X-UA-Compatible")
                                .attribute("content", "IE=edge,chrome=1")
                                .done())
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
    }

}
