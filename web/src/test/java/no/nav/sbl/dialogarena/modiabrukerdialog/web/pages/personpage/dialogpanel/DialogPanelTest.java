package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SakServiceMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class, SakServiceMockContext.class})
public class DialogPanelTest extends WicketPageTest {

    @Inject
    protected SakService sakService;

    @Before
    public void setUp() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void inneholderGenerelleKomponenter() {
        wicket.goToPageWith(new ReferatPanel("id", "fnr"))
                .should().containComponent(withId("dialogform").and(ofType(Form.class)))
                .should().containComponent(withId("tekstfelt").and(ofType(EnhancedTextArea.class)))
                .should().containComponent(withId("send").and(ofType(AjaxButton.class)))
                .should().containComponent(withId("feedback").and(ofType(FeedbackPanel.class)))
                .should().containComponent(withId("kvittering").and(ofType(KvitteringsPanel.class).thatIsInvisible()));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        DialogPanel dialogPanel = new ReferatPanel("id", "fnr");
        wicket.goToPageWith(dialogPanel)
                .inForm(withId("dialogform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
    }

    @Test
    public void viserKvitteringNaarManSenderInn() {
        wicket.goToPageWith(lagDialogPanelMedKanalSatt())
                .inForm(withId("dialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().withId("dialogform"))
                .should().containComponent(thatIsVisible().ofType(KvitteringsPanel.class));
    }

    protected DialogPanel lagDialogPanelMedKanalSatt() {
        DialogPanel dialogPanel = new ReferatPanel("id", "fnr");
        Object dialogformModel = dialogPanel.get("dialogform").getDefaultModelObject();
        DialogVM dialogVM = (DialogVM) dialogformModel;
        dialogVM.kanal = ReferatKanal.TELEFON;
        dialogVM.temagruppe = Temagruppe.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT;
        return dialogPanel;
    }

}