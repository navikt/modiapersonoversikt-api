package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInnstillingerPanelMockContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNull;
import static no.nav.modig.modia.events.InternalEvents.GOTO_HENT_PERSONPAGE;
import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        SaksbehandlerInnstillingerPanelMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HentPersonPageTest extends WicketPageTest {

    @Test
    public void shouldRenderHentPersonPage() {
        wicket.goTo(HentPersonPage.class)
                .should().containComponent(withId("searchPanel").and(ofType(HentPersonPanel.class)))
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)));
    }

    @Test
    public void shouldRenderHentPersonPageWithErrorMessage() {
        wicket.goTo(HentPersonPage.class, with().param("error", "errorMessage"))
                .should().containPatterns("errorMessage");
    }

	@Test
    public void shouldRenderHentPersonPageWithSikkerhetstiltak() {
        wicket.goTo(HentPersonPage.class, with().param(HentPersonPage.SIKKERHETSTILTAK, "Farlig."))
                .should().containPatterns(HentPersonPage.SIKKERHETSTILTAK);
    }

	@Test
	public void shouldExtractSikkerhetstiltaksbeskrivelse() throws JSONException {
		HentPersonPage page = new HentPersonPage(new PageParameters());
		String sikkerhetstiltak = page.getSikkerhetsTiltakBeskrivelse("{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}");
		assertEquals("Farlig.", sikkerhetstiltak);
	}


	@Test
	public void shouldExtractErrortext() throws JSONException {
		HentPersonPage page = new HentPersonPage(new PageParameters());
		String errorTxt = page.getErrorText("{\"errortext\":\"Feil tekst\",\"sikkerhettiltaksbeskrivelse\":\"Farlig.\"}");
		assertEquals("Feil tekst", errorTxt);
	}

	@Test
	public void shouldExtractNullWhenFnrtExist() throws JSONException {
		HentPersonPage page = new HentPersonPage(new PageParameters());
		String sikkerhetstiltak = page.getSikkerhetsTiltakBeskrivelse("{\"errortext\":\"Feil tekst\"}");
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
