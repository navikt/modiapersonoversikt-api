package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.FrontendModules;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

public class InnboksTestApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public Class<? extends Page> getHomePage() {
        return InnboksTestPage.class;
    }

    @Override
    protected void init() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
        getMarkupSettings().setStripWicketTags(true);

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
                .addCss(new CssResourceReference(InnboksTestApplication.class, "stylesheets/innboks.css"))
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
    }
}
