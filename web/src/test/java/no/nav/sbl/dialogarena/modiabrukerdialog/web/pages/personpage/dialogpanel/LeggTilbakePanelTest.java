package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerServicesMockContext.class})
public class LeggTilbakePanelTest extends WicketPageTest {

    private static final String MELDING_FNR = "11111111111";
    private static final String MELDING_ID = "123";
    private static final String BEHANDLINGS_ID = "behandlingsid";

    private static final GrunnInfo grunnInfo = new GrunnInfo(new GrunnInfo.Bruker("").withEnhet("0219",  "NAV Bærum"), null);

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;

    @Before
    public void setUpTest() {
        setup("Enhet");
    }

    public void setup(String enhet) {
        Melding sporsmal = new Melding().withFnr(MELDING_FNR).withId(MELDING_ID).withFerdigstiltDato(now());
        sporsmal.oppgaveId = "1";
        sporsmal.temagruppe = "temagruppe";
        sporsmal.gjeldendeTemagruppe = Temagruppe.ARBD;
        sporsmal.tilknyttetEnhet = enhet;
        sporsmal.brukersEnhet = enhet;
        wicket.goToPageWith(new LeggTilbakePanel("id", sporsmal.temagruppe, sporsmal.gjeldendeTemagruppe, null, sporsmal, BEHANDLINGS_ID));
    }

    @Test
    public void inneholderNodvendigeKomponenter() {
        wicket.should().containComponent(ofType(Label.class).and(withId("temagruppe")))
                .should().containComponent(ofType(Form.class))
                .should().inComponent(ofType(Form.class)).containComponents(3, ofType(Radio.class))
                .should().inComponent(ofType(Form.class)).containComponent(ofType(TextArea.class))
                .should().inComponent(ofType(Form.class)).containComponent(ofType(DropDownChoice.class))
                .should().containComponent(ofType(FeedbackPanel.class))
                .should().containComponent(ofType(AjaxButton.class))
                .should().containComponent(ofType(AjaxLink.class));
    }

    @Test
    public void leggTilbakeFeilTema() {
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 0)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(ofType(Form.class))
                .select("valgtAarsak:temagruppeWrapper:nyTemagruppeSkjuler:nyTemagruppe", 1)
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(true));

        Mockito.verify(henvendelseUtsendingService, Mockito.times(1)).oppdaterTemagruppe(eq(MELDING_ID), any(String.class));
    }

    @Test
    public void leggTilbakeFeilTemaANSOS() {
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 0)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(ofType(Form.class))
                .select("valgtAarsak:temagruppeWrapper:nyTemagruppeSkjuler:nyTemagruppe", 10)
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(true));
        Mockito.verify(henvendelseUtsendingService, Mockito.times(1)).merkSomKontorsperret(eq(MELDING_FNR), any(List.class));
    }

    @Test(expected = IndexOutOfBoundsException.class)//Prøver å velge OKSOS
    public void fjernetANSOSogOKSOSHvisBrukersEnhetIkkeErSatt() {
        setup(null);
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 0)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(ofType(Form.class))
                .select("valgtAarsak:temagruppeWrapper:nyTemagruppeSkjuler:nyTemagruppe", 9);
    }

    @Test
    public void leggTilbakeSenderAvbrytTilHenvendelse() {
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 1)
                .submitWithAjaxButton(withId("leggtilbake"));

        Mockito.verify(henvendelseUtsendingService, Mockito.times(1)).avbrytHenvendelse(BEHANDLINGS_ID);
    }

    @Test
    public void leggTilbakeInabil() {
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 1)
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(true));
    }

    @Test
    public void leggTilbakeAnnenAarsak() {
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 2)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(ofType(Form.class))
                .write("valgtAarsak:annenAarsakTekst", "Dette er en tekst")
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(true));
    }

    @Test
    public void girFeedbackOmIngenRadioknapperErKlikket() {
        wicket.inForm(ofType(Form.class))
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
        assertThat(errorMessages, contains(wicket.get().component(ofType(RadioGroup.class)).getString("valgtAarsak.Required")));
    }

    @Test
    public void girFeedbackOmFeilTemaMenNyttTemaIkkeValgt() {
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 0)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(ofType(Form.class))
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
        assertThat(errorMessages, contains(wicket.get().component(ofType(DropDownChoice.class)).getString("nyTemagruppe.Required")));
    }

    @Test
    public void girFeedbackOmAnnenAarsakMenTomTekst() {
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 2)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(ofType(Form.class))
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
        assertThat(errorMessages, contains(wicket.get().component(ofType(TextArea.class)).getString("annenAarsakTekst.Required")));
    }

    @Test
    public void kanIkkeLeggeTilbakePaaTemagruppeForAndreSosialeTjenester() {
        Melding sporsmal = new Melding().withFnr(MELDING_FNR).withId(MELDING_ID).withFerdigstiltDato(now());
        sporsmal.oppgaveId = "1";
        sporsmal.temagruppe = "temagruppe";
        sporsmal.gjeldendeTemagruppe = Temagruppe.ANSOS;
        wicket.goToPageWith(new LeggTilbakePanel("id", sporsmal.temagruppe, sporsmal.gjeldendeTemagruppe, null, sporsmal, BEHANDLINGS_ID));

        wicket.should().containComponent(both(withId("temagruppeWrapper")).and(thatIsInvisible()));
    }

}
