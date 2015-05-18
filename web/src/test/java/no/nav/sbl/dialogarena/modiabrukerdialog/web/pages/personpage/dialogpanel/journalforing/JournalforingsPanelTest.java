package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.TestUtils;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerServicesMockContext.class})
public class JournalforingsPanelTest extends WicketPageTest {

    @Inject
    private SakerService sakerService;

    private CompoundPropertyModel<HenvendelseVM> henvendelseVM;

    @Before
    public void setUp() {
        henvendelseVM = new CompoundPropertyModel<>(new HenvendelseVM());
        wicket.goToPageWith(new JournalforingsPanel("id", "fnr", henvendelseVM));
        when(sakerService.hentSaker(anyString())).thenReturn(TestUtils.createMockSaker());
    }

    @Test
    public void viserIngenSakValgtHvisIngenSakErValgt() {
        wicket
                .should().containComponent(thatIsVisible().and(withId("ingenSakValgt")))
                .should().containComponent(thatIsInvisible().and(withId("sakValgt")))
                .should().containComponent(thatIsInvisible().and(ofType(AjaxLazyLoadVelgSakPanel.class)));
    }

    @Test
    public void viserValgtSakDersomSakErValgt() {
        henvendelseVM.getObject().valgtSak = TestUtils.createSak("id", "temakode", "fagsystemkode", "sakstype", DateTime.now());
        wicket.goToPageWith(new JournalforingsPanel("id", "fnr", henvendelseVM))
                .should().containComponent(thatIsInvisible().and(withId("ingenSakValgt")))
                .should().containComponent(thatIsVisible().and(withId("sakValgt")))
                .should().containComponent(thatIsInvisible().and(ofType(AjaxLazyLoadVelgSakPanel.class)));
    }

    @Test
    public void togglerSynlighetForVelgSakPanelHvisManKlikkerPaaValgtSakLenke() {
        wicket.click().link(withId("valgtSakLenke"));
        wicket.onComponent(ofType(AjaxLazyLoadVelgSakPanel.class)).executeAjaxBehaviors(BehaviorMatchers.ofType(AbstractDefaultAjaxBehavior.class));
        wicket.should().containComponent(thatIsVisible().and(ofType(VelgSakPanel.class)))
                .click().link(withId("valgtSakLenke"))
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)));
    }

    @Test
    public void viserValgtSakOgSkjulerVelgSakPaneletDersomManVelgerSak() {
        wicket.click().link(withId("valgtSakLenke"));
        wicket.onComponent(ofType(AjaxLazyLoadVelgSakPanel.class)).executeAjaxBehaviors(BehaviorMatchers.ofType(AbstractDefaultAjaxBehavior.class));
        wicket.inForm(withId("plukkSakForm"))
                .select("valgtSak", 0)
                .submitWithAjaxButton(withId("velgSak"))
                .should().containComponent(thatIsInvisible().and(withId("ingenSakValgt")))
                .should().containComponent(thatIsVisible().and(withId("sakValgt")))
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)));
    }

}