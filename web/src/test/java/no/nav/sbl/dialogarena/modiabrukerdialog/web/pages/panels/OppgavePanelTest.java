package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels;

import no.nav.sbl.dialogarena.aktorid.service.AktorService;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktorPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BesvareHenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OppgavebehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.sbl.dialogarena.utbetaling.config.UtbetalingConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * DirtiesContext er nødvendig her for at InternTest skal kjøre
 * på Jenkins.
 */
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        HenvendelsePortTypeMock.class,
        SykepengerWidgetMockContext.class,
        SakOgBehandlingPortTypeMock.class,
        OppgavebehandlingPortTypeMock.class,
        BesvareHenvendelsePortTypeMock.class,
        AktorPortTypeMock.class,
        SoknaderService.class,
        AktorService.class,
        UtbetalingConfig.class,
        UtbetalingPortTypeMock.class
})
public class OppgavePanelTest extends WicketPageTest {

    @Test
    public void skalOppretteOppgavepanelUtenOppgaveId() {
        wicket.goTo(Intern.class, with().param("fnr", "03054549872"))
                .should().containComponent(both(ofType(OppgavePanel.class)).and(thatIsVisible()));
    }

}
