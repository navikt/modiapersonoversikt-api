package no.nav.sbl.dialogarena.varsel;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;
import java.util.Locale;

public class VarselApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public Class<? extends Page> getHomePage() {
        return VarselTestPage.class;
    }

    protected void setupSpringInjector() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }

    @Override
    protected void init() {
        setupSpringInjector();
        Locale.setDefault(new Locale("nb", "no"));
        getMarkupSettings().setStripWicketTags(true);


    }

}
