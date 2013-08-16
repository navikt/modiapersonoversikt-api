package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser;

import static no.nav.modig.frontend.FrontendModules.EKSTERNFLATE;
import static no.nav.modig.frontend.FrontendModules.UNDERSCORE;
import static no.nav.modig.frontend.MetaTag.CHARSET_UTF8;
import static no.nav.modig.frontend.MetaTag.VIEWPORT_SCALE_1;
import static no.nav.modig.frontend.MetaTag.XUA_IE_EDGE;
import static no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel.INNSTILLINGER_JS;
import static no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel.INNSTILLINGER_LESS;
import static no.nav.sbl.dialogarena.webkomponent.tilbakemelding.web.TilbakemeldingContainer.TILBAKEMELDING_JS;
import static no.nav.sbl.dialogarena.webkomponent.tilbakemelding.web.TilbakemeldingContainer.TILBAKEMELDING_LESS;

import javax.inject.Inject;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

/**
 * Kontekst for wicket
 */
public class WicketApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    public static WicketApplication get() {
        return (WicketApplication) Application.get();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return SporsmalOgSvarSide.class;
    }
    
    @Override
    protected void init() {
        super.init();
        new FrontendConfigurator()
                .withModules(EKSTERNFLATE, UNDERSCORE)
                .addMetas(CHARSET_UTF8, VIEWPORT_SCALE_1, XUA_IE_EDGE)
                .addLess(TILBAKEMELDING_LESS, INNSTILLINGER_LESS, new PackageResourceReference(SporsmalOgSvarSide.class, "sporsmal.less"))
                .addScripts(TILBAKEMELDING_JS, INNSTILLINGER_JS)
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
        new ApplicationSettingsConfig().configure(this);
//        mountPage("internal/selftest", SelfTestPage.class);
        Application.get().getRequestLoggerSettings().setRequestLoggerEnabled(true);
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
