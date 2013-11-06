package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@ContextConfiguration(classes = {KjerneinfoPepMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MockSetupPageTest extends WicketPageTest {

    @Test
    public void shouldRenderMockSetupPage() {
        wicket.goTo(MockSetupPage.class).should().containComponent(withId("velgMockForm"));
    }
}
