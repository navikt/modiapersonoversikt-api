package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
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
                1,
                "kvitteringsmelding", new AjaxRequestHandler(wicket.tester.getLastRenderedPage()),
                new Form("id"));

        wicket.should().containComponent(ofType(KvitteringsPanel.class).thatIsVisible());

        wicket.executeAjaxBehaviors(BehaviorMatchers.ofType(AbstractAjaxTimerBehavior.class));

        wicket.should().containComponent(ofType(KvitteringsPanel.class).thatIsInvisible())
                .should().inAjaxResponse().haveComponents(ofType(KvitteringsPanel.class));
    }
}