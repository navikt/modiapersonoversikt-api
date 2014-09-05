package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MerkePanelTest extends WicketPageTest {

    public static final String PANEL_MERK_FORM_ID = "panel:merkForm";
    public static final String MERK_TYPE_RADIOGROUP_ID = "merkType";
    private static final String FNR = "fnr";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    private MerkePanel merkePanel;

    @Before
    public void setUp() {
        InnboksVM innboksVM = new InnboksVM(FNR);
        merkePanel = new MerkePanel("panel", innboksVM);
        merkePanel.setVisibilityAllowed(true);
    }

    @Test
    public void skalGiFeilmeldingDersomManProverAaMarkereUtenAaVelgeKontorsperretEllerFeilsendt() {
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .submitWithAjaxButton(withId("merk"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
        assertThat(errorMessages, contains(wicket.get().component(ofType(RadioGroup.class)).getString(MERK_TYPE_RADIOGROUP_ID + ".Required")));
    }

    @Test
    public void skalSkjuleOpprettOppgavePanelHvisIngenAvRadiovalgeneErSatt() {
        wicket.goToPageWith(merkePanel)
                .should().containComponent(thatIsInvisible().withId("opprettOppgavePanel"));
    }

    @Test
    public void skalViseOpprettOppgavePanelHvisKontorsperreErValgt() {
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 1)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .should().containComponent(thatIsVisible().withId("opprettOppgavePanel"));
    }

    @Test
    public void skalMerkeTraadSomKontorsperret() {
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 1)
                .submitWithAjaxButton(withId("merk"));

        verify(henvendelseBehandlingService).merkSomKontorsperret(eq(FNR), any(TraadVM.class));
    }

    @Test
    public void skalMerkeTraadSomFeilsendt() {
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 0)
                .submitWithAjaxButton(withId("merk"));

        verify(henvendelseBehandlingService).merkSomFeilsendt(any(TraadVM.class));
    }

    @Test
    public void skalViseFeilMeldingDersomManHarHuketAvForOpprettOppgaveOgProeverAaMerkeUtenAaFullfoereOppgaveopprettelse() {
        wicket.goToPageWith(merkePanel)
                .printComponentsTree()
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 1)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(PANEL_MERK_FORM_ID)
                .check("merkType:opprettOppgavePanel:visNyOppgaveWrapper:opprettOppgaveCheckbox", true)
                .submitWithAjaxButton(withId("merk"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
        assertThat(errorMessages, contains(wicket.get().component(ofType(RadioGroup.class)).getString("kontorsperre.oppgave.opprettet.feil")));
    }

    @Test
    public void skalResetteMerkVMIdetManMarkererSomFeilsenddt() {
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 0)
                .submitWithAjaxButton(withId("merk"));

        assertNull(((MerkVM) merkePanel.get("merkForm").getDefaultModelObject()).getMerkType());
        assertThat(merkePanel.isVisibilityAllowed(), is(false));
    }

    @Test
    public void skalSkjuleMerkPaneletIdetManTrykkerAvbryt() {
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 0)
                .andReturn()
                .click().link(withId("avbryt"));

        assertThat(merkePanel.isVisibilityAllowed(), is(false));
    }

}
