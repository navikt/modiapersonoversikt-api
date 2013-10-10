package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.CANCEL;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class, WicketTesterConfig.class})
public class SjekkForlateSideTest {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    private SjekkForlateSideAnswer answer;

    @Before
    public void setup() {
        answer = new SjekkForlateSideAnswer();
    }

    @Test
    public void skalOppretteSjekkForlateSide() {
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .should().containComponent(withId("closeDiscard").and(ofType(AjaxLink.class)))
                .should().containComponent(withId("closeCancel").and(ofType(AjaxLink.class)));
    }

    @Test
    public void skalReturnereCancelAswer() {
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .click().link(withId("closeCancel"));

        assertTrue(answer.is(CANCEL));
        assertFalse(answer.is(DISCARD));
    }

    @Test
    public void skalReturnereDiscardAswer() {
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .click().link(withId("closeDiscard"));

        assertTrue(answer.is(DISCARD));
        assertFalse(answer.is(CANCEL));
    }

}
