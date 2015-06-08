package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
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

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerServicesMockContext.class})
public class LeggTilbakePanelTest extends WicketPageTest {

    private static final String MELDING_FNR = "11111111111";

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;

    @Before
    public void setUpTest() {
        Melding sporsmal = new Melding().withFnr(MELDING_FNR).withId("123").withOpprettetDato(now());
        sporsmal.oppgaveId = "1";
        sporsmal.temagruppe = "temagruppe";
        wicket.goToPageWith(new LeggTilbakePanel("id", sporsmal.temagruppe, Optional.<String>none(), asList(sporsmal)));
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
                .select("valgtAarsak:nyTemagruppeSkjuler:nyTemagruppe", 1)
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(true));
    }

    @Test
    public void leggTilbakeFeilTemaANSOS() {
        wicket.inForm(ofType(Form.class))
                .select("valgtAarsak", 0)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(ofType(Form.class))
                .select("valgtAarsak:nyTemagruppeSkjuler:nyTemagruppe", 6)
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(true));
        Mockito.verify(henvendelseUtsendingService, Mockito.times(1)).merkSomKontorsperret(eq(MELDING_FNR), any(List.class));
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

}
