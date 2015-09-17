package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.opprettMeldingEksempel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MerkePanelTest extends WicketPageTest {

    private static final String PANEL_MERK_FORM_ID = "panel:merkForm";
    private static final String MERK_TYPE_RADIOGROUP_ID = "merkType";
    private static final String FNR = "fnr";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    private InnboksVM innboksVM;

    @Test
    public void paneletViserAlleRadioknappene() {
        wicket.goToPageWith(getStandardMerkePanel())
                .should().containComponent(thatIsVisible().withId("feilsendtRadio"))
                .should().containComponent(thatIsVisible().withId("bidragRadioValg"))
                .should().containComponent(thatIsVisible().withId("bidragRadio"))
                .should().containComponent(thatIsVisible().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsVisible().withId("kontorsperretRadio"));
    }

    @Test
    public void paneletViserIkkeMerkValgFoerManHarValgtEnRadioknapp() {
        wicket.goToPageWith(getStandardMerkePanel()).should().containComponent(thatIsInvisible().withId("merk"));
    }

    @Test
    public void skjulerOpprettOppgavePanelHvisIngenAvRadiovalgeneErSatt() {
        wicket.goToPageWith(getStandardMerkePanel())
                .should().containComponent(thatIsInvisible().withId("kontorsperrePanel"));
    }

    @Test
    public void viserOpprettOppgavePanelHvisKontorsperreErValgt() {
        wicket.goToPageWith(getStandardMerkePanel())
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 2)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .should().containComponent(thatIsVisible().withId("kontorsperrePanel"));
    }

    @Test
    public void merkerTraadSomKontorsperret() {
        MerkePanel merkePanel = getStandardMerkePanel();
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 2)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class));

        wicket.tester.executeAjaxEvent(wicket.get().component(withId("opprettOppgaveCheckbox")), "click");

        wicket.inForm(PANEL_MERK_FORM_ID).submitWithAjaxButton(withId("merk"));

        verify(henvendelseBehandlingService).merkSomKontorsperret(FNR, innboksVM.getValgtTraad());
    }

    @Test
    public void merkerTraadSomFeilsendt() {
        MerkePanel merkePanel = getStandardMerkePanel();
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 0)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(PANEL_MERK_FORM_ID)
                .submitWithAjaxButton(withId("merk"));

        verify(henvendelseBehandlingService).merkSomFeilsendt(innboksVM.getValgtTraad());
    }

    @Test
    public void resetterMerkVMIdetManMarkererSomFeilsendt() {
        MerkePanel merkePanel = getStandardMerkePanel();
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 0)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(PANEL_MERK_FORM_ID)
                .submitWithAjaxButton(withId("merk"));

        assertNull(((MerkVM) merkePanel.get("merkForm").getDefaultModelObject()).getMerkType());
        assertThat(merkePanel.isVisibilityAllowed(), is(false));
    }

    @Test
    public void skjulerMerkPaneletIdetManTrykkerAvbryt() {
        MerkePanel merkePanel = getStandardMerkePanel();
        wicket.goToPageWith(merkePanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 0)
                .andReturn()
                .click().link(withId("avbryt"));

        assertThat(merkePanel.isVisibilityAllowed(), is(false));
    }

    @Test
    public void skjulerBidragOgKontorsperretvalgDersomValgtTraadErKontorsperret() {
        MerkePanel merkePanelForKontorsperretTraad = getMerkPanelMedKontorsperretValgtMelding();
        merkePanelForKontorsperretTraad.setVisibilityAllowed(true);

        wicket.goToPageWith(merkePanelForKontorsperretTraad)
                .should().containComponent(thatIsVisible().withId("feilsendtRadio"))
                .should().containComponent(thatIsInvisible().withId("bidragRadioValg"))
                .should().containComponent(thatIsInvisible().withId("bidragRadio"))
                .should().containComponent(thatIsInvisible().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsInvisible().withId("kontorsperretRadio"));
    }

    @Test
    public void skjulerBidragValgHvisValgtTraadHarTemagruppeSosialeTjenester() {
        wicket.goToPageWith(getMerkePanel(asList(opprettMeldingEksempel().withGjeldendeTemagruppe(Temagruppe.OKSOS))).setVisibilityAllowed(true))
                .should().containComponent(thatIsVisible().withId("feilsendtRadio"))
                .should().containComponent(thatIsInvisible().withId("bidragRadioValg"))
                .should().containComponent(thatIsInvisible().withId("bidragRadio"))
                .should().containComponent(thatIsVisible().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsVisible().withId("kontorsperretRadio"));

        wicket.goToPageWith(getMerkePanel(asList(opprettMeldingEksempel().withGjeldendeTemagruppe(Temagruppe.ANSOS))).setVisibilityAllowed(true))
                .should().containComponent(thatIsVisible().withId("feilsendtRadio"))
                .should().containComponent(thatIsInvisible().withId("bidragRadioValg"))
                .should().containComponent(thatIsInvisible().withId("bidragRadio"))
                .should().containComponent(thatIsVisible().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsVisible().withId("kontorsperretRadio"));
    }

    @Test
    public void erMuligAaMerkeSomFeilsendtDersomValgtTraadErKontorsperret() {
        MerkePanel merkePanelForKontorsperretTraad = getMerkPanelMedKontorsperretValgtMelding();
        merkePanelForKontorsperretTraad.setVisibilityAllowed(true);

        assertThat(innboksVM.getValgtTraad().erFeilsendt(), is(false));

        wicket.goToPageWith(merkePanelForKontorsperretTraad)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 0)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(PANEL_MERK_FORM_ID)
                .submitWithAjaxButton(withId("merk"));

        verify(henvendelseBehandlingService).merkSomFeilsendt(innboksVM.getValgtTraad());
    }

    private MerkePanel getStandardMerkePanel() {
        return getMerkePanel(asList(opprettMeldingEksempel()));
    }

    private MerkePanel getMerkPanelMedKontorsperretValgtMelding() {
        Melding melding = TestUtils.opprettMeldingEksempel().withKontorsperretEnhet("kontorsperretEnhet");
        return getMerkePanel(asList(melding));
    }

    private MerkePanel getMerkePanel(List<Melding> meldinger) {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(meldinger);
        innboksVM = new InnboksVM(FNR, henvendelseBehandlingService);
        innboksVM.oppdaterMeldinger();
        innboksVM.settForsteSomValgtHvisIkkeSatt();
        MerkePanel merkePanel = new MerkePanel("panel", innboksVM);
        merkePanel.setVisibilityAllowed(true);
        return merkePanel;
    }

}
