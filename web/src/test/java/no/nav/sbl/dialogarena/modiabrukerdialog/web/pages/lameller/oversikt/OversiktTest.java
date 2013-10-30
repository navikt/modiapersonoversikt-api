package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.sbl.dialogarena.aktorid.service.AktorService;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktorPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.sbl.dialogarena.utbetaling.config.UtbetalingConfig;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HenvendelsePortTypeMock.class,
        KjerneinfoPepMockContext.class,
        SykepengerWidgetMockContext.class,
        SakOgBehandlingPortTypeMock.class,
        AktorPortTypeMock.class,
        SoknaderService.class,
        AktorService.class,
        UtbetalingConfig.class,
        UtbetalingPortTypeMock.class
})
public class OversiktTest extends WicketPageTest {

    @Test
    public void skalOppretteOversikt() {
        wicket.goToPageWith(new Oversikt("id", "fnr"))
                .should().containComponent(withId("lenker").and(ofType(LenkeWidget.class)))
                .should().containComponent(withId("sykepenger").and(ofType(SykepengerWidget.class)));
    }

}
