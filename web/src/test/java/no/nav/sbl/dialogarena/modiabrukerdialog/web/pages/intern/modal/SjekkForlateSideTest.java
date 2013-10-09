package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationTestContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.CacheConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.common.MDCOperations.MDC_CALL_ID;
import static no.nav.modig.common.MDCOperations.generateCallId;
import static no.nav.modig.common.MDCOperations.putToMDC;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class, CacheConfig.class, WicketTesterConfig.class})
public class SjekkForlateSideTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    @Before
    public void setupMDC() {
        putToMDC(MDC_CALL_ID, generateCallId());
    }

    @Test
    public void skalOppretteSjekkForlateSide() {
        SjekkForlateSideAnswer answer = new SjekkForlateSideAnswer();
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .should().containComponent(withId("closeDiscard").and(ofType(AjaxLink.class)))
                .should().containComponent(withId("closeCancel").and(ofType(AjaxLink.class)));
    }

    @Test
    public void skalReturnereCancelAswer() {
        SjekkForlateSideAnswer answer = new SjekkForlateSideAnswer();
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .click().link(withId("closeCancel"));

        assertTrue(answer.is(AnswerType.CANCEL));
        assertFalse(answer.is(AnswerType.DISCARD));
    }

    @Test
    public void skalReturnereDiscardAswer() {
        SjekkForlateSideAnswer answer = new SjekkForlateSideAnswer();
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .click().link(withId("closeDiscard"));

        assertTrue(answer.is(AnswerType.DISCARD));
        assertFalse(answer.is(AnswerType.CANCEL));
    }
}
