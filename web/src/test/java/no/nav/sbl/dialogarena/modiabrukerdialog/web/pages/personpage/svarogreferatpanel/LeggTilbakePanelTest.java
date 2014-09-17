package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConsumerServicesMockContext.class,
        EndpointMockContext.class})
public class LeggTilbakePanelTest extends WicketPageTest {

    @Before
    public void setUpTest() {
        Sporsmal sporsmal = new Sporsmal("sporsmal", DateTime.now());
        sporsmal.oppgaveId = "1";
        sporsmal.temagruppe = "temagruppe";
        wicket.goToPageWith(new TestLeggTilbakePanel("id", sporsmal));
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
                .select("valgtAarsak", 1)
                .andReturn()
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxFormChoiceComponentUpdatingBehavior.class))
                .inForm(ofType(Form.class))
                .select("valgtAarsak:temagruppewrapper:nyTemagruppe", 1)
                .submitWithAjaxButton(withId("leggtilbake"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(true));
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
