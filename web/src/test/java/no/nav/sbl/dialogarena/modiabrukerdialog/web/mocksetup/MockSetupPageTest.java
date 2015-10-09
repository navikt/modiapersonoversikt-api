package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.MockSetupPageMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        MockSetupPageMockContext.class})
public class MockSetupPageTest extends WicketPageTest {

    @Test
    public void shouldRenderMockSetupPage() {
        wicket.goTo(MockSetupPage.class).should().containComponent(withId("velgMockForm"));
    }

    @Test
    public void shouldGoToHentPersonPageWhenSubmitMockSetup() {
        wicket.goTo(MockSetupPage.class).inForm("velgMockForm").submit().should().beOn(HentPersonPage.class);
    }
}
