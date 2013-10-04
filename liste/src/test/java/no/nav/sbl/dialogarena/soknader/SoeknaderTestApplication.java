package no.nav.sbl.dialogarena.soknader;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.FrontendModules;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.modia.liste.EkspanderingsListe;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

public class SoeknaderTestApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public Class<? extends Page> getHomePage() {
        return SoknaderTestPage.class;
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
                .withResourcePacking(this.usesDeploymentConfig())
                .addScripts(
                        //                        BasePage.JS_RESOURCE,
                        //                        ShortcutListenerResourceReference.get(), //TODO: Flytt til MODIA modul ?
                        //                        KeyNavigationResourceReference.get(),    //TODO: Flytt til MODIA modul ?
                        //                        Widget.JS_RESOURCE,
                        //                        LamellPanel.JS_RESOURCE,
                        //                        DatePicker.DATEPICKER_JS,
                        //                        DateRangePicker.JS_REFERENCE,
                        //                        JqueryTokenValueChangeBehavior.JS_REFERENCE,
                        //                        ModigModalWindow.JS,
                        EkspanderingsListe.JS_RESOURCE          //TODO: Flytt til MODIA modul ?
                        //                        Liste.JS_RESOURCE,                       //TODO: Flytt til MODIA modul ?
                        //                        DatePicker.JQUERY_PLACEHOLDER,
                        //                        ModalErrorPanel.JS_RESOURCE
                ).configure(this);
    }
}
