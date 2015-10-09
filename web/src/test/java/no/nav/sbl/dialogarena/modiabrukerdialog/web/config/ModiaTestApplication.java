package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;
import java.util.Locale;

public class ModiaTestApplication extends WebApplication {
    @Inject
    private ApplicationContext applicationContext;

    @Override
    public Class<? extends Page> getHomePage() {
        return PersonPage.class;
    }

    @Override
    protected void init() {
        super.init();
        getResourceSettings().getStringResourceLoaders().add(new IStringResourceLoader() {
            @Override
            public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
                return "";
            }

            @Override
            public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
                return "";
            }
        });

        new ApplicationSettingsConfig().configure(this);

        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }
}
