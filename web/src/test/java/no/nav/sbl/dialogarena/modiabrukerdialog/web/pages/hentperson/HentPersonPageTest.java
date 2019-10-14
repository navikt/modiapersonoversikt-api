package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback.SokOppBrukerCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static junit.framework.Assert.assertNull;
import static no.nav.modig.modia.events.InternalEvents.GOTO_HENT_PERSONPAGE;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HentPersonPanelMockContext.class})
public class HentPersonPageTest extends WicketPageTest {

    @Override
    protected void additionalSetup() {
//        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
//        System.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
//        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder("Z999999", IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }

    @Test
    public void shouldRenderHentPersonPage() {
        wicket.goTo(HentPersonPage.class)
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)));
    }

    @Test
    public void shouldRenderHentPersonPageWithErrorMessage() {
        wicket.goTo(HentPersonPage.class, with().param("error", "errorMessage"))
                .should().containPatterns("errorMessage");
    }

    @Test
    public void shouldRenderHentPersonPageWithErrorMessageAndFnr() {
        wicket.goTo(HentPersonPage.class, with().param("error", "errorMessage").param(SokOppBrukerCallback.JSON_SOKT_FNR, "wrongFnr"))
                .should().containPatterns("errorMessage")
                .should().containPatterns("wrongFnr");
    }

    @Test
    public void shouldRenderHentPersonPageWithSikkerhetstiltak() {
        wicket.goTo(HentPersonPage.class, with().param(HentPersonPage.SIKKERHETSTILTAK, "Farlig."))
                .should().containPatterns(HentPersonPage.SIKKERHETSTILTAK);
    }

    @Test
    public void shouldExtractSikkerhetstiltaksbeskrivelse() throws JSONException {
        HentPersonPage page = new HentPersonPage(new PageParameters());
        String sikkerhetstiltak = page.getTextFromPayload("{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}", SokOppBrukerCallback.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
        assertEquals("Farlig.", sikkerhetstiltak);
    }


    @Test
    public void shouldExtractErrortext() throws JSONException {
        HentPersonPage page = new HentPersonPage(new PageParameters());
        String errorTxt = page.getTextFromPayload("{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}", SokOppBrukerCallback.JSON_ERROR_TEXT);
        assertEquals("Feil tekst", errorTxt);
    }

    @Test
    public void shouldExtractErrortextAndSoktFnr() throws JSONException {
        HentPersonPage page = new HentPersonPage(new PageParameters());
        String soktFnr = page.getTextFromPayload("{\"errortext\":\"Feil tekst\",\"soektfnr\":\"wrongFnr\"}", SokOppBrukerCallback.JSON_SOKT_FNR);
        assertEquals("wrongFnr", soktFnr);
    }

    @Test
    public void shouldExtractNullWhenFnrtExist() throws JSONException {
        HentPersonPage page = new HentPersonPage(new PageParameters());
        String sikkerhetstiltak = page.getTextFromPayload("{\"errortext\":\"Feil tekst\"}", SokOppBrukerCallback.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
        assertNull(sikkerhetstiltak);
    }

    @Test
    public void vellykketGotoHentPersonPageBeggeError() {

        wicket.goTo(HentPersonPage.class, with().param("pageParameters", "{\"errortext\":\"Feil tekst\"}"));

        wicket.sendEvent(createEvent(GOTO_HENT_PERSONPAGE, "{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}"));
    }

    @Test
    public void vellykketGotoHentPersonPageKunErrortekst() {

        wicket.goTo(HentPersonPage.class, with().param("pageParameters", "{\"errortext\":\"Feil tekst\"}"));

        wicket.sendEvent(createEvent(GOTO_HENT_PERSONPAGE, "{\"errortext\":\"Feil tekst\"}"));
    }

    private EventGenerator createEvent(final String eventNavn, final Object payload) {
        return new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(eventNavn, payload);
            }
        };
    }
}
