package no.nav.sbl.dialogarena.besvare;

import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
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
        new ApplicationSettingsConfig().configure(this);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return BesvareSporsmalPage.class;
    }

}
