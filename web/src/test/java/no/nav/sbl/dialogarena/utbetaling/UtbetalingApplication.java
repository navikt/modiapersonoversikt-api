package no.nav.sbl.dialogarena.utbetaling;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.FrontendModules;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.modia.shortcuts.ShortcutListenerResourceReference;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLamell;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import org.apache.commons.collections15.Factory;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;
import java.util.Locale;

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

        Datoformat.brukLocaleFra(new Factory<Locale>() {
            @Override
            public Locale create() {
                return Session.get().getLocale();
            }
        });

        new ApplicationSettingsConfig().withUtf8Properties(true).configure(this);
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
                .addLess(UtbetalingLamell.UTBETALING_LAMELL_LESS, UtbetalingWidget.UTBETALING_WIDGET_LESS)
                .addScripts(Widget.JS_RESOURCE,
                        ShortcutListenerResourceReference.get())
                .configure(this);
    }

}
