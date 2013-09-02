package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.FrontendModules;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.SporsmalOgSvarPage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;


public class BesvareSporsmalApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    protected void init() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
        getMarkupSettings().setStripWicketTags(true);
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public void onBeginRequest(RequestCycle cycle) {
                SubjectHandlerUtils.setInternBruker("userId");
            }

            @Override
            public void onEndRequest(RequestCycle cycle) {
                SubjectHandlerUtils.reset();
            }
        });
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
                .addCss(new CssResourceReference(BesvareSporsmalApplication.class, "stylesheets/innboks.css"))
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return SporsmalOgSvarPage.class;
    }

}
