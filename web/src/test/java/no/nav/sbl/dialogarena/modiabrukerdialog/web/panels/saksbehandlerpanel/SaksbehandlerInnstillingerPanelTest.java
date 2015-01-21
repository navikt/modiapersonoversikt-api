package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.DefaultSaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInnstillingerPanelMockContext;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {
        KjerneinfoPepMockContext.class,
        SaksbehandlerInnstillingerPanelMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SaksbehandlerInnstillingerPanelTest extends WicketPageTest {

    @Inject
    private DefaultSaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Inject
    private AnsattService ansattService;

    @Before
    public void setUp() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
    }

    @Test
    public void skalStarteSaksbehandlerPanelUtenFeil() {
        wicket.goToPageWith(new SaksbehandlerInnstillingerPanel("saksbehandlerInnstillingerPanel"));
    }

    @Test
    public void saksbehandlerPanelVisesVedFlereEnheter() {
        when(ansattService.hentEnhetsliste()).thenReturn(flereAnsattEnheter());
        when(saksbehandlerInnstillingerService.saksbehandlerInnstillingerErUtdatert()).thenReturn(true);

        wicket
                .goTo(SaksbehandlerInnstillingerTestPage.class)
                .should().containComponent(thatIsVisible().and(ofType(SaksbehandlerInnstillingerPanel.class)));
    }

    @Test
    public void saksbehandlerPanelVisesIkkeVedKunEnEnhet() {
        when(ansattService.hentEnhetsliste()).thenReturn(asList(new AnsattEnhet("111", "Grunerløkka")));
        when(saksbehandlerInnstillingerService.saksbehandlerInnstillingerErUtdatert()).thenReturn(true);

        wicket
                .goTo(SaksbehandlerInnstillingerTestPage.class)
                .should().containComponent(thatIsInvisible().and(ofType(SaksbehandlerInnstillingerPanel.class)));
    }

    @Test
    public void saksbehandlerPanelTogglesVedKlikkPaToggler() {
        wicket
                .goTo(SaksbehandlerInnstillingerTestPage.class)
                .should().containComponent(thatIsInvisible().and(ofType(SaksbehandlerInnstillingerPanel.class)))
                .onComponent(ofType(SaksbehandlerInnstillingerTogglerPanel.class))
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsVisible().and(ofType(SaksbehandlerInnstillingerPanel.class)))
                .onComponent(ofType(SaksbehandlerInnstillingerTogglerPanel.class))
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsInvisible().and(ofType(SaksbehandlerInnstillingerPanel.class)));
    }

    @Test
    public void saksbehandlerPanelSkjulesVedKlikkPaVelgKnapp() {
        when(ansattService.hentEnhetsliste()).thenReturn(flereAnsattEnheter());
        when(saksbehandlerInnstillingerService.saksbehandlerInnstillingerErUtdatert()).thenReturn(true);

        wicket
                .goTo(SaksbehandlerInnstillingerTestPage.class)
                .inForm(withId("enhetsform"))
                .select("enhet", 1)
                .submitWithAjaxButton(withId("velg"))
                .should().containComponent(thatIsInvisible().and(ofType(SaksbehandlerInnstillingerPanel.class)));
    }

    private List<AnsattEnhet> flereAnsattEnheter() {
        return asList(new AnsattEnhet("111", "Grunerløkka"), new AnsattEnhet("222", "Torshov"));
    }
}