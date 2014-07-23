package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInstillingerPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {SaksbehandlerInstillingerPanelMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SaksbehandlerInstillingerPanelTest extends WicketPageTest {

    @Inject
    private AnsattService ansattService;

    @Before
    public void setUp(){
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
    }

    @Test
    public void skalStarteSaksbehandlerPanelUtenFeil() {
        wicket.goToPageWith(new SaksbehandlerInstillingerPanel("saksbehandlerPanel"));
    }

    @Test
    public void sjekkAtSaksbehandlerPanelVisesVedKlikkPaNavIdentKnapp() {
        wicket
                .goToPageWith(new SaksbehandlerInstillingerPanel("saksbehandlerPanel"))
                .should().containComponent(thatIsInvisible().and(ofType(WebMarkupContainer.class)).and(withId("valgContainer")))
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsVisible().and(ofType(WebMarkupContainer.class)).and(withId("valgContainer")));
    }

    @Test
    public void sjekkAtSaksbehandlerPanelSkjulesVedKlikkPaVelgKnapp() {
        List<AnsattEnhet> ansattEnheter = Arrays.asList(new AnsattEnhet("111", "Grunerløkka"), new AnsattEnhet("222", "Torshov"));
        when(ansattService.hentEnhetsliste()).thenReturn(ansattEnheter);
        wicket
                .goToPageWith(new SaksbehandlerInstillingerPanel("saksbehandlerPanel"))
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .inForm(withId("enhetsform"))
                .select("enhet", 1)
                .submitWithAjaxButton(withId("velg"))
                .should().containComponent(thatIsInvisible().and(ofType(WebMarkupContainer.class)).and(withId("valgContainer")));
    }
}