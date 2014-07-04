package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class})
public class KvitteringsPanelTest extends WicketPageTest {

    @Test
    public void inneholderMeldingOgErUsynlig() {
        wicket.goToPageWith(new KvitteringsPanel("id"))
                .should().containComponent(withId("kvitteringsmelding"))
                .should().containComponent(ofType(KvitteringsPanel.class).thatIsInvisible());
    }

    @Test
    public void viserOgSkjulerSegSelv() {
        KvitteringsPanel kvitteringsPanel = new KvitteringsPanel("id");
        wicket.goToPageWith(kvitteringsPanel);
        kvitteringsPanel.visISekunder(
                Duration.seconds(1),
                new AjaxRequestHandler(wicket.tester.getLastRenderedPage()),
                "kvitteringsmelding", new Form("id"));

        wicket.should().containComponent(ofType(KvitteringsPanel.class).thatIsVisible());

        wicket.executeAjaxBehaviors(BehaviorMatchers.ofType(AbstractAjaxTimerBehavior.class));

        wicket.should().containComponent(ofType(KvitteringsPanel.class).thatIsInvisible())
                .should().inAjaxResponse().haveComponents(ofType(KvitteringsPanel.class));
    }
}