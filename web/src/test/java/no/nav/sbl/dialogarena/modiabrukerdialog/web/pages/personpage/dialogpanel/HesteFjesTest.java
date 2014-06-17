package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HenvendelseMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class, HenvendelseMockContext.class})
public class HesteFjesTest extends WicketPageTest {

    @Inject
    protected SendHenvendelsePortType ws;

    @Before
    public void setUp() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void inneholderGenerelleKomponenter() {
        wicket.goToPageWith(new TestHesteFjes("id", "fnr"))
                .should().containComponent(withId("dialogform").and(ofType(Form.class)))
                .should().containComponent(withId("tema").and(ofType(DropDownChoice.class)))
                .should().containComponent(withId("tekstfelt").and(ofType(EnhancedTextArea.class)))
                .should().containComponent(withId("send").and(ofType(AjaxButton.class)))
                .should().containComponent(withId("feedback").and(ofType(FeedbackPanel.class)))
                .should().containComponent(withId("kvittering").and(ofType(Kvitteringspanel.class).thatIsInvisible()));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        TestHesteFjes dialogPanel = new TestHesteFjes("id", "fnr");
        wicket.goToPageWith(dialogPanel)
                .inForm(withId("dialogform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, containsInAnyOrder(
                dialogPanel.get("dialogform:tekstfelt").getString("text.Required"),
                dialogPanel.getString("dialogform.tema.Required")
        ));
    }

    @Test
    public void viserKvitteringNaarManSenderInn() {
        TestHesteFjes dialogPanel = lagDialogPanelMedKanalSatt();
        wicket.goToPageWith(dialogPanel)
                .inForm(withId("dialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("tema", 0)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().withId("dialogform"))
                .should().containComponent(thatIsVisible().ofType(Kvitteringspanel.class));
    }

    protected TestHesteFjes lagDialogPanelMedKanalSatt() {
        TestHesteFjes dialogPanel = new TestHesteFjes("id", "fnr");
        Object dialogformModel = dialogPanel.get("dialogform").getDefaultModelObject();
        DialogVM dialogVM = (DialogVM) dialogformModel;
        dialogVM.kanal = TestKanal.TEST;
        return dialogPanel;
    }

    private static class TestHesteFjes extends HesteFjes {
        public TestHesteFjes(String id, String fnr) {
            super(id, fnr);
        }

        @Override
        protected void sendHenvendelse(DialogVM dialogVM, String fnr) {

        }
    }

    static enum TestKanal implements Kanal {
        TEST;

        @Override
        public String getKvitteringKey() {
            return "svarpanel.kvittering.bekreftelse";
        }
    }
}