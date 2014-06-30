package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SakServiceMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class, SakServiceMockContext.class})
public class ReferatPanelTest extends WicketPageTest {

    @Inject
    protected SakService sakService;

    @Before
    public void init() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void inneholderReferatspesifikkeKomponenter() {
        wicket.goToPageWith(new ReferatPanel("id", "fnr"))
                .should().containComponent(withId("temagruppe").and(ofType(DropDownChoice.class)))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        ReferatPanel referatPanel = new ReferatPanel("id", "fnr");
        wicket.goToPageWith(referatPanel)
                .inForm(withId("dialogform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(referatPanel.getString("dialogform.temagruppe.Required")));
    }

    @Test
    public void skalSendeReferattypeTilHenvendelse() {
        wicket.goToPageWith(new ReferatPanel("id", "fnr"))
                .inForm(withId("dialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("temagruppe", 0)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(sakService).sendReferat(any(Referat.class));
    }
}