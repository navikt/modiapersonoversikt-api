package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.brukerdialog.security.context.StaticSubjectHandler;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInnstillingerPanelMockContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel.SAKSBEHANDLERINNSTILLINGER_TOGGLET;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SaksbehandlerInnstillingerPanelMockContext.class})
public class SaksbehandlerInnstillingerPanelTest extends WicketPageTest {

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private AnsattService ansattService;

    private EventGenerator saksbehandlerInnstillingerToggletEvent;

    @Before
    public void setUp() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        saksbehandlerInnstillingerToggletEvent = new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(SAKSBEHANDLERINNSTILLINGER_TOGGLET);
            }
        };
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
                .goToPageWith(SaksbehandlerInnstillingerPanel.class)
                .should().containComponent(thatIsVisible().and(ofType(SaksbehandlerInnstillingerPanel.class)));
    }

    @Test
    public void saksbehandlerPanelVisesIkkeVedKunEnEnhet() {
        when(ansattService.hentEnhetsliste()).thenReturn(asList(new AnsattEnhet("111", "Grunerløkka")));
        when(saksbehandlerInnstillingerService.saksbehandlerInnstillingerErUtdatert()).thenReturn(true);

        wicket
                .goToPageWith(SaksbehandlerInnstillingerPanel.class)
                .should().containComponent(thatIsInvisible().and(ofType(SaksbehandlerInnstillingerPanel.class)));
    }

    @Test
    public void saksbehandlerPanelTogglesVedKlikkPaToggler() {
        wicket
                .goToPageWith(SaksbehandlerInnstillingerPanel.class)
                .should().containComponent(thatIsInvisible().and(ofType(SaksbehandlerInnstillingerPanel.class)))
                .sendEvent(saksbehandlerInnstillingerToggletEvent)
                .should().containComponent(thatIsVisible().and(ofType(SaksbehandlerInnstillingerPanel.class)))
                .sendEvent(saksbehandlerInnstillingerToggletEvent)
                .should().containComponent(thatIsInvisible().and(ofType(SaksbehandlerInnstillingerPanel.class)));
    }

    @Test
    public void saksbehandlerPanelSkjulesVedKlikkPaVelgKnapp() {
        when(ansattService.hentEnhetsliste()).thenReturn(flereAnsattEnheter());
        when(saksbehandlerInnstillingerService.saksbehandlerInnstillingerErUtdatert()).thenReturn(true);

        wicket
                .goToPageWith(SaksbehandlerInnstillingerPanel.class)
                .inForm(withId("enhetsform"))
                .select("enhet", 1)
                .submitWithAjaxButton(withId("velg"))
                .should().containComponent(thatIsInvisible().and(ofType(SaksbehandlerInnstillingerPanel.class)));
    }

    private List<AnsattEnhet> flereAnsattEnheter() {
        return asList(new AnsattEnhet("111", "Grunerløkka"), new AnsattEnhet("222", "Torshov"));
    }
}