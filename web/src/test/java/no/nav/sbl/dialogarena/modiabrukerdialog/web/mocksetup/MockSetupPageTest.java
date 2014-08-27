package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;

import no.nav.modig.cache.CacheConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SaksbehandlerInnstillingerPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import no.nav.sbl.modiabrukerdialog.pep.config.spring.PepConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {
        EndpointMockContext.class,
        HentPersonPanelMockContext.class,
        SykepengerWidgetMockContext.class,
        SaksbehandlerInnstillingerPanelMockContext.class,
        UtbetalingLamellContext.class,
        SporsmalOgSvarContext.class,
		CacheConfig.class,
		PepConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
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
