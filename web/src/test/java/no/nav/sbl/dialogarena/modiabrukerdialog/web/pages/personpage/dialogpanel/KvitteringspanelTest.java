package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
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
public class KvitteringspanelTest extends WicketPageTest {

    @Test
    public void inneholderMeldingOgErUsynlig() {
        wicket.goToPageWith(new Kvitteringspanel("id"))
                .should().containComponent(withId("kvitteringsmelding"))
                .should().containComponent(ofType(Kvitteringspanel.class).thatIsInvisible());
    }

    @Test
    public void viserOgSkjulerSegSelv() {
        Kvitteringspanel kvitteringspanel = new Kvitteringspanel("id");
        wicket.goToPageWith(kvitteringspanel);
        DialogVM dialogVM = new DialogVM();
        dialogVM.kanal = HesteFjesTest.TestKanal.TEST;
        kvitteringspanel.visISekunder(
                Duration.seconds(1),
                new AjaxRequestHandler(wicket.tester.getLastRenderedPage()),
                new Form<>("formId", Model.of(dialogVM)),
                "kvitteringsmelding"
        );

        wicket.should().containComponent(ofType(Kvitteringspanel.class).thatIsVisible());

        wicket.executeAjaxBehaviors(BehaviorMatchers.ofType(AbstractAjaxTimerBehavior.class));

        wicket.should().containComponent(ofType(Kvitteringspanel.class).thatIsInvisible())
                .should().inAjaxResponse().haveComponents(ofType(Kvitteringspanel.class));
    }
}