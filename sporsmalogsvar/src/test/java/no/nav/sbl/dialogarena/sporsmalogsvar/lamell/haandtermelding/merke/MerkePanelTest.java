package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.opprettMeldingEksempel;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.opprettSamtalereferatEksempel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MockServiceTestContext.class})
@ExtendWith(SpringExtension.class)
public class MerkePanelTest extends WicketPageTest {

    private static final String PANEL_MERK_FORM_ID = "panel:merkForm";
    private static final String MERK_TYPE_RADIOGROUP_ID = "merkType";
    private static final String FNR = "fnr";

    @Inject
    @Named("henvendelseBehandlingServiceMock")
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Inject
    private EnforcementPoint pep;

    private InnboksVM innboksVM;

    @Test
    public void paneletViserAlleRadioknappene() {
        wicket.goToPageWith(getMerkePanel(asList(opprettMeldingEksempel())))
                .should().containComponent(thatIsVisible().withId("feilsendtRadio"))
                .should().containComponent(thatIsVisible().withId("bidragRadioValg"))
                .should().containComponent(thatIsVisible().withId("bidragRadio"))
                .should().containComponent(thatIsVisible().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsVisible().withId("avsluttRadio"))
                .should().containComponent(thatIsVisible().withId("kontorsperretRadio"))
                .should().containComponent(thatIsVisible().withId("avsluttRadio"));
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
    public void disablerAlleBortsettFraAvsluttRadio() {
        MerkePanel merkePanel = getMerkePanel(asList(opprettMeldingEksempel()));
        merkePanel.setVisibilityAllowed(true);

        wicket.goToPageWith(merkePanel)
                .should().containComponent(thatIsDisabled().withId("feilsendtRadio"))
                .should().containComponent(thatIsDisabled().withId("bidragRadioValg"))
                .should().containComponent(thatIsDisabled().withId("bidragRadio"))
                .should().containComponent(thatIsDisabled().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsDisabled().withId("kontorsperretRadio"))
                .should().containComponent(thatIsEnabled().withId("avsluttRadio"));
    }

    @Test
    public void enablerAlleBortsettFraAvsluttRadio() {
        MerkePanel merkePanel = getMerkePanel(asList(opprettSamtalereferatEksempel(), opprettSamtalereferatEksempel()));
        merkePanel.setVisibilityAllowed(true);

        wicket.goToPageWith(merkePanel)
                .should().containComponent(thatIsEnabled().withId("feilsendtRadio"))
                .should().containComponent(thatIsEnabled().withId("bidragRadioValg"))
                .should().containComponent(thatIsEnabled().withId("bidragRadio"))
                .should().containComponent(thatIsEnabled().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsEnabled().withId("kontorsperretRadio"))
                .should().containComponent(thatIsDisabled().withId("avsluttRadio"));
    }

    @Test
    public void disablerBidragValgHvisValgtTraadHarTemagruppeSosialeTjenester() {
        wicket.goToPageWith(getMerkePanel(asList(opprettMeldingEksempel().withGjeldendeTemagruppe(Temagruppe.OKSOS),
                opprettSamtalereferatEksempel().withGjeldendeTemagruppe(Temagruppe.OKSOS))).setVisibilityAllowed(true))
                .should().containComponent(thatIsEnabled().withId("feilsendtRadio"))
                .should().containComponent(thatIsDisabled().withId("bidragRadioValg"))
                .should().containComponent(thatIsDisabled().withId("bidragRadio"))
                .should().containComponent(thatIsEnabled().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsEnabled().withId("kontorsperretRadio"))
                .should().containComponent(thatIsDisabled().withId("avsluttRadio"));

        wicket.goToPageWith(getMerkePanel(asList(opprettMeldingEksempel().withGjeldendeTemagruppe(Temagruppe.ANSOS),
                opprettSamtalereferatEksempel().withGjeldendeTemagruppe(Temagruppe.ANSOS))).setVisibilityAllowed(true))
                .should().containComponent(thatIsEnabled().withId("feilsendtRadio"))
                .should().containComponent(thatIsDisabled().withId("bidragRadioValg"))
                .should().containComponent(thatIsDisabled().withId("bidragRadio"))
                .should().containComponent(thatIsEnabled().withId("kontorsperretRadioValg"))
                .should().containComponent(thatIsEnabled().withId("kontorsperretRadio"))
                .should().containComponent(thatIsDisabled().withId("avsluttRadio"));
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

    @Test
    public void erMuligAaMerkeSporsmalSomFerdigstiltUtenSvar() {
        MerkePanel merkepanel = getStandardMerkePanel();
        merkepanel.setVisibilityAllowed(true);

        wicket.goToPageWith(merkepanel)
                .inForm(PANEL_MERK_FORM_ID)
                .select(MERK_TYPE_RADIOGROUP_ID, 3)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(PANEL_MERK_FORM_ID)
                .submitWithAjaxButton(withId("merk"));

        verify(henvendelseBehandlingService).merkSomAvsluttet(eq(innboksVM.getValgtTraad()), anyString());
    }

    private MerkePanel getStandardMerkePanel() {
        return getMerkePanel(asList(opprettMeldingEksempel(), opprettMeldingEksempel()));
    }

    private MerkePanel getMerkPanelMedKontorsperretValgtMelding() {
        Melding melding = TestUtils.opprettMeldingEksempel().withKontorsperretEnhet("kontorsperretEnhet");
        return getMerkePanel(asList(opprettMeldingEksempel(), melding));
    }

    private MerkePanel getMerkePanel(List<Melding> meldinger) {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(meldinger));
        innboksVM = new InnboksVM(FNR, henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
        innboksVM.oppdaterMeldinger();
        innboksVM.settForsteSomValgtHvisIkkeSatt();
        MerkePanel merkePanel = new MerkePanel("panel", innboksVM);
        merkePanel.setVisibilityAllowed(true);
        return merkePanel;
    }
}
