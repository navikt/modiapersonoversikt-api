package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.CANCEL;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KjerneinfoPepMockContext.class, WicketTesterConfig.class})
public class SjekkForlateSideTest extends WicketPageTest {

    private SjekkForlateSideAnswer answer;
    private SjekkForlateSide sjekkForlateSide;

    @Override
    protected void additionalSetup() {
        answer = new SjekkForlateSideAnswer();
        sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);

    }

    @Test
    public void skalOppretteSjekkForlateSide() {
        wicket.goToPageWith(sjekkForlateSide)
                .should().containComponent(withId("closeDiscard").and(ofType(AjaxLink.class)))
                .should().containComponent(withId("closeCancel").and(ofType(AjaxLink.class)));
    }

    @Test
    public void skalReturnereCancelAswer() {
        wicket.goToPageWith(sjekkForlateSide)
                .click().link(withId("closeCancel"));
        assertTrue(answer.is(CANCEL));
        assertFalse(answer.is(DISCARD));
    }

    @Test
    public void skalReturnereDiscardAswer() {
        wicket.goToPageWith(sjekkForlateSide)
                .click().link(withId("closeDiscard"));

        assertTrue(answer.is(DISCARD));
        assertFalse(answer.is(CANCEL));
    }

}
