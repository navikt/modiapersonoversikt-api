package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.errorhandling.ModiaApplicationConfigurator;
import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.modia.constants.ModiaConstants;
import no.nav.modig.modia.lamell.LamellPanel;
import no.nav.modig.modia.lamell.ModalErrorPanel;
import no.nav.modig.modia.liste.EkspanderingsListe;
import no.nav.modig.modia.liste.Liste;
import no.nav.modig.modia.navigation.KeyNavigationResourceReference;
import no.nav.modig.modia.shortcuts.ShortcutListenerResourceReference;
import no.nav.modig.modia.token.JqueryTokenValueChangeBehavior;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.pagelet.spi.utils.SPIResources;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.wicket.BehaviorPolicyAuthorizationStrategy;
import no.nav.modig.wicket.component.datepicker.DatePicker;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.modig.wicket.selftest.HealthCheck;
import no.nav.modig.wicket.selftest.JsonResourceReference;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils.LocaleFromWicketSession;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup.MockSetupPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.selftest.SelfTestPage;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactResources;
import no.nav.sbl.dialogarena.sak.lamell.SaksoversiktLerret;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks;
import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLerret;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.settings.IMarkupSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Locale;

import static no.nav.modig.frontend.FrontendModules.MODIA;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockSetupErTillatt;
import static no.nav.sbl.dialogarena.time.Datoformat.brukLocaleFra;
import static org.apache.wicket.util.time.Duration.ONE_SECOND;
import static org.slf4j.LoggerFactory.getLogger;

public class WicketApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private CmsContentRetriever cms;

    private static final Logger log = getLogger(WicketApplication.class);

    @Resource(name = "pep")
    private EnforcementPoint pep;

    public static WicketApplication get() {
        return (WicketApplication) Application.get();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HentPersonPage.class;
    }

    @Override
    protected void init() {
        super.init();
        Locale.setDefault(new Locale("nb", "no"));
        if (usesDevelopmentConfig()) {
            getResourceSettings().setResourcePollFrequency(ONE_SECOND);
        }

        configureFrontend();

        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");

        setMarkupSettings();

        getSecuritySettings().setAuthorizationStrategy(new BehaviorPolicyAuthorizationStrategy(pep));

        new ApplicationSettingsConfig().withUtf8Properties(true).configure(this);

        Application.get().getRequestLoggerSettings().setRequestLoggerEnabled(true);

        configureCmsResourceLoader();

        new ModiaApplicationConfigurator()
                .withExceptionHandler(true)
                .configure(this);

        mountPages();

        setSpringComponentInjector();

        brukLocaleFra(LocaleFromWicketSession.INSTANCE);
    }

    private void configureCmsResourceLoader() {
        getResourceSettings().getStringResourceLoaders().add(0, new IStringResourceLoader() {
            @Override
            public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
                return hentTekstFraCms(key);
            }

            @Override
            public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
                return hentTekstFraCms(key);
            }

            private String hentTekstFraCms(String key) {
                try {
                    return cms.hentTekst(key);
                } catch (Exception e) {
                    log.warn("Fant ikke " + key + " i cms. Defaulter til properties-fil. " + e.getMessage());
                    return null;
                }
            }
        });
    }

    private void setMarkupSettings() {
        IMarkupSettings markupSettings = getMarkupSettings();
        markupSettings.setStripWicketTags(true);
        markupSettings.setStripComments(true);
        markupSettings.setCompressWhitespace(true);
        markupSettings.setDefaultMarkupEncoding("UTF-8");
    }

    private void configureFrontend() {
        new FrontendConfigurator()
                .withModules(MODIA)
                .addMetas(
                        MetaTag.XUA_IE_EDGE,
                        MetaTag.CHARSET_UTF8,
                        MetaTag.VIEWPORT_SCALE_1
                )
                .addConditionalCss(
                        PersonPage.INTERN_IE,
                        SaksoversiktLerret.SAKSOVERSIKT_IE_CSS,
                        BasePage.MODIA_FLEXBOX_IE_CSS,
                        Innboks.MELDINGER_IE_CSS
                )
                .addLess(
                        BasePage.MODIA_COMMON_LESS,
                        BasePage.MODIA_KOMPONENTER_LESS,
                        BasePage.MODIA_RAMME_LESS,
                        BasePage.MODIA_FLEXBOX_LESS,
                        BasePage.PERSONINFO_LESS,
                        UtbetalingLerret.UTBETALING_LESS,
                        SaksoversiktLerret.SAKSOVERSIKT_LESS,
                        BasePage.MELDINGERWIDGET,
                        BasePage.MELDINGERLAMELL,
                        PersonPage.SVAR_OG_REFERATPANEL_LESS
                )
                .addCss(
                        BasePage.PERSONSOKRESULT,
                        BasePage.PERSONSOKSEARCH
                )
                .addScripts(SPIResources.getScripts())
                .addScripts(
                        BasePage.JS_RESOURCE,
                        ShortcutListenerResourceReference.get(),
                        KeyNavigationResourceReference.get(),
                        Widget.JS_RESOURCE,
                        LamellPanel.JS_RESOURCE,
                        DatePicker.DATEPICKER_JS,
                        DateRangePicker.JS_REFERENCE,
                        JqueryTokenValueChangeBehavior.JS_REFERENCE,
                        ModigModalWindow.JS,
                        EkspanderingsListe.JS_RESOURCE,
                        Liste.JS_RESOURCE,
                        DatePicker.JQUERY_PLACEHOLDER,
                        ModalErrorPanel.JS_RESOURCE,
                        UtbetalingLerret.UTBETALING_LAMELL_JS,
                        PersonPage.SELECTMENU_JS,
                        SaksoversiktLerret.SAKSOVERSIKT_JS,
                        SaksbehandlerInnstillingerTogglerPanel.SAKSBEHANDLER_INNSTILLINGER_JS,
                        Innboks.MELDINGER_JS
                )
                .withModules(ReactResources.SKRIVESTOTTE)
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
    }

    private void mountPages() {
        mountPage("/person/${fnr}", PersonPage.class);
        mountPage("internal/isAlive", HealthCheck.class);
        mountPage("internal/selftest", SelfTestPage.class);
        mountResource("internal/selftest.json", new JsonResourceReference(SelfTestPage.class));
        if (mockSetupErTillatt()) {
            mountPage("internal/mocksetup", MockSetupPage.class);
        }
    }


    @Override
    public Session newSession(Request request, Response response) {
        Session session = super.newSession(request, response);
        session.setAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET, false);
        return session;
    }

    protected void setSpringComponentInjector() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }

}
