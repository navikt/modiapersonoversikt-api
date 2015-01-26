package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.referatpanel.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.TestUtils;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConsumerServicesMockContext.class,
        EndpointMockContext.class})
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
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)));
    }

    @Test
    public void viserValgtSakDersomSakErValgt() {
        henvendelseVM.getObject().valgtSak = TestUtils.createSak("id", "temakode", "fagsystemkode", "sakstype", DateTime.now());
        wicket.goToPageWith(new JournalforingsPanel("id", "fnr", henvendelseVM))
                .should().containComponent(thatIsInvisible().and(withId("ingenSakValgt")))
                .should().containComponent(thatIsVisible().and(withId("sakValgt")))
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)));
    }

    @Test
    public void togglerSynlighetForVelgSakPanelHvisManKlikkerPaaValgtSakLenke() {
        wicket.click().link(withId("valgtSakLenke"))
                .should().containComponent(thatIsVisible().and(ofType(VelgSakPanel.class)))
                .click().link(withId("valgtSakLenke"))
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)));
    }

    @Test
    public void viserValgtSakOgSkjulerVelgSakPaneletDersomManVelgerSak() {
        wicket.click().link(withId("valgtSakLenke"))
                .inForm(withId("plukkSakForm"))
                    .select("valgtSak", 0)
                    .submitWithAjaxButton(withId("velgSak"))
                .should().containComponent(thatIsInvisible().and(withId("ingenSakValgt")))
                .should().containComponent(thatIsVisible().and(withId("sakValgt")))
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)));
    }

}