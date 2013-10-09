package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.MockTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@ContextConfiguration(classes = {MockTest.class, WicketTesterConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HentPersonPageTest {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    @Test
    public void shouldRenderHentPersonPage() {
        fluentWicketTester.goTo(HentPersonPage.class)
                .should().containComponent(withId("searchPanel").and(ofType(HentPersonPanel.class)))
                .should().containComponent(withId("personsokPanel").and(ofType(PersonsokPanel.class)));
    }

    @Test
    public void shouldRenderHentPersonPageWithErrorMessage() {
        Parameters param = new Parameters();
        param.pageParameters.set("error", "errorMessage");
        fluentWicketTester.goTo(HentPersonPage.class, param).should().containPatterns("errorMessage");
    }

}
