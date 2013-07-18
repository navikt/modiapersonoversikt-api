package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.InnboksPage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

import static no.nav.modig.frontend.FrontendModules.EKSTERNFLATE;


public class BesvareSporsmalApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    protected void init() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
        getMarkupSettings().setStripWicketTags(true);
        new ApplicationSettingsConfig().configure(this);
        new FrontendConfigurator()
                .withModules(EKSTERNFLATE)
                .addMetas(
                        MetaTag.CHARSET_UTF8,
                        MetaTag.VIEWPORT_SCALE_1,
                        new MetaTag.With()
                                .attribute("http-equiv", "X-UA-Compatible")
                                .attribute("content", "IE=edge,chrome=1")
                                .done())
                .addLess(new PackageResourceReference(BesvareSporsmalApplication.class, "stylesheets/innboks.less"))
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return InnboksPage.class;
    }

}
