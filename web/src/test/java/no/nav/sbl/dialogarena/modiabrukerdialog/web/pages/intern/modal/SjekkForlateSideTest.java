package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.common.MDCOperations.MDC_CALL_ID;
import static no.nav.modig.common.MDCOperations.generateCallId;
import static no.nav.modig.common.MDCOperations.putToMDC;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@ActiveProfiles({"test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
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
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new ModiaModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .should().containComponent(withId("closeDiscard").and(ofType(AjaxLink.class)))
                .should().containComponent(withId("closeCancel").and(ofType(AjaxLink.class)));
    }

    @Test
    public void skalReturnereCancelAswer() {
        SjekkForlateSideAnswer answer = new SjekkForlateSideAnswer();
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new ModiaModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .click().link(withId("closeCancel"));

        assertThat(answer.getAnswerType(), is(SjekkForlateSideAnswer.AnswerType.CANCEL));
    }

    @Test
    public void skalReturnereDiscardAswer() {
        SjekkForlateSideAnswer answer = new SjekkForlateSideAnswer();
        SjekkForlateSide sjekkForlateSide = new SjekkForlateSide("id", new ModiaModalWindow("modigModalWindow"), answer);

        fluentWicketTester.goToPageWith(sjekkForlateSide)
                .click().link(withId("closeDiscard"));

        assertThat(answer.getAnswerType(), is(SjekkForlateSideAnswer.AnswerType.DISCARD));
    }
}
