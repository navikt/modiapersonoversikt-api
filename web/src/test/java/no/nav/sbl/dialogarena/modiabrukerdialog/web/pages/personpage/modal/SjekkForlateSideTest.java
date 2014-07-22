package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal;

import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktoerPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BehandleHenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BehandleJournalV2PortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavAnsattPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakHentSakslisteMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgaveV2PortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgavebehandlingV2PortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SendUtHenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInstillingerPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer.AnswerType.CANCEL;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        KjerneinfoPepMockContext.class,
        HentPersonPanelMockContext.class,
        SykepengerWidgetMockContext.class,
        AktoerPortTypeMock.class,
        UtbetalingLamellContext.class,
        SakOgBehandlingPortTypeMock.class,
        HenvendelseSoknaderPortTypeMock.class,
        SporsmalOgSvarContext.class,
        UtbetalingPortTypeMock.class,
        HenvendelsePortTypeMock.class,
        SendUtHenvendelsePortTypeMock.class,
        BehandleHenvendelsePortTypeMock.class,
        GsakHentSakslisteMock.class,
        BehandleJournalV2PortTypeMock.class,
        GsakOppgaveV2PortTypeMock.class,
        GsakOppgavebehandlingV2PortTypeMock.class,
        GosysNavAnsattPortTypeMock.class,
        SaksbehandlerInstillingerPanelMockContext.class
})
public class SjekkForlateSideTest extends WicketPageTest {

    private SjekkForlateSideAnswer answer;
    private SjekkForlateSide sjekkForlateSide;
    private Parameters parameters;

    @Override
    protected void additionalSetup() {
        parameters = new Parameters().param("fnr", "11111111111");
        answer = new SjekkForlateSideAnswer();
        sjekkForlateSide = new SjekkForlateSide("id", new RedirectModalWindow("modigModalWindow"), answer);
    }

    @Test
    public void skalOppretteSjekkForlateSide() {
        wicket.goTo(PersonPage.class, parameters).goToPageWith(sjekkForlateSide)
                .should().containComponent(withId("closeDiscard").and(ofType(AjaxLink.class)))
                .should().containComponent(withId("closeCancel").and(ofType(AjaxLink.class)));
    }

    @Test
    public void skalReturnereCancelAswer() {
        wicket.goTo(PersonPage.class, parameters).goToPageWith(sjekkForlateSide)
                .click().link(withId("closeCancel"));
        assertTrue(answer.is(CANCEL));
        assertFalse(answer.is(DISCARD));
    }

    @Test
    public void skalReturnereDiscardAswer() {
        wicket.goTo(PersonPage.class, parameters)
                .goToPageWith(sjekkForlateSide)
                .click().link(withId("closeDiscard"));
        assertTrue(answer.is(DISCARD));
        assertFalse(answer.is(CANCEL));
    }

}
