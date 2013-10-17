package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {HentPersonPanelMockContext.class})
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

}
