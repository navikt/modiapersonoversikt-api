package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.sak.config.SaksoversiktServiceConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        EndpointMockContext.class,
        SykepengerWidgetMockContext.class,
        UtbetalingLamellContext.class,
        SporsmalOgSvarContext.class,
        SaksoversiktServiceConfig.class,
        KodeverkV2PortTypeMock.class,
        PersonPageMockContext.class
})
public class OversiktLerretTest extends WicketPageTest {

    @Test
    public void skalOppretteOversikt() {
        System.setProperty("kodeverkendpoint.v2.url", "something");
        wicket.goToPageWith(new OversiktLerret("oversiktId", "fnr"))
                .should().containComponent(withId("lenker").and(ofType(LenkeWidget.class)))
                .should().containComponent(withId("sykepenger").and(ofType(SykepengerWidget.class)));
    }

}
