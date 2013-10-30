package no.nav.sbl.dialogarena.modiabrukerdialog.web;

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
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils.LocaleFromWicketSession;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup.MockSetupPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.InternBesvaremodus;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.selftest.SelfTestPage;
import no.nav.sbl.dialogarena.soknader.liste.SoknadListe;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLamell;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.CompoundAuthorizationStrategy;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.IMarkupSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import javax.inject.Inject;

import java.util.Locale;

import static no.nav.modig.frontend.FrontendModules.MODIA;
import static org.apache.wicket.util.time.Duration.ONE_SECOND;

public class WicketApplication extends WebApplication {

    @Inject
    private ApplicationContext applicationContext;

	@Resource(name = "kjerneinfoPep")
	private EnforcementPoint kjerneinfoPep;

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
        if (usesDevelopmentConfig()) {
            getResourceSettings().setResourcePollFrequency(ONE_SECOND);
        }

        new FrontendConfigurator()
                .withModules(MODIA)
                .addMetas(
                        MetaTag.XUA_IE_EDGE,
                        MetaTag.CHARSET_UTF8,
                        MetaTag.VIEWPORT_SCALE_1
                )
		        .addConditionalCss(Intern.INTERN_IE)
                .addConditionalJavascript(Intern.RESPOND_JS)
		        .addLess(
                        BasePage.INTERN_LESS,
                        BasePage.BESVARE_LESS,
                        BasePage.TRAADPANEL,
                        BasePage.JOURNALFORPANEL,
                        BasePage.MELDINGERWIDGET,
                        BasePage.MELDINGERLAMELL,
                        SoknadListe.SOKNADSLISTE_LESS,
                        BasePage.MELDINGERLAMELL,
                        UtbetalingLamell.UTBETALING_LAMELL_LESS,
                        UtbetalingWidget.UTBETALING_WIDGET_LESS)
                .addCss(
                        BasePage.PERSONSOKRESULT,
                        BasePage.PERSONSOKSEARCH,
                        BasePage.PERSONKJERNEINFO
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
                        Innboks.JS_REFERENCE
                )
		        .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);

        // Innstillinger vi bør ha
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        IMarkupSettings markupSettings = getMarkupSettings();
        markupSettings.setStripWicketTags(true);
        markupSettings.setStripComments(true);
        markupSettings.setCompressWhitespace(true);
        markupSettings.setDefaultMarkupEncoding("UTF-8");

		CompoundAuthorizationStrategy compoundAuthorizationStrategy = new CompoundAuthorizationStrategy();
		compoundAuthorizationStrategy.add(new BehaviorPolicyAuthorizationStrategy(kjerneinfoPep));
		getSecuritySettings().setAuthorizationStrategy(compoundAuthorizationStrategy);

        new ApplicationSettingsConfig().withUtf8Properties(true).configure(this);

        Application.get().getRequestLoggerSettings().setRequestLoggerEnabled(true);


        new ModiaApplicationConfigurator()
                .withExceptionHandler(true)
                .configure(this);

        mountPage("/person/${fnr}", Intern.class);
        mountPage("/person/${fnr}/besvaresporsmal/${oppgaveId}", InternBesvaremodus.class);
        mountPage("/mocksetup", MockSetupPage.class);
        mountPage("internal/selftest", SelfTestPage.class);

        setSpringComponentInjector();

        Datoformat.brukLocaleFra(LocaleFromWicketSession.INSTANCE);
    }

    @Override
    public Session newSession(Request request, Response response) {
        Session session = super.newSession(request, response);
        session.setLocale(new Locale("nb")); // Vis kun bokmaal i leveranse 1
	    session.setAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET, false);
        return session;
    }

    protected void setSpringComponentInjector() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }

}
