package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {PersonPageMockContext.class})
public class OversiktLerretTest extends WicketPageTest {

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Before
    public void setUp() {
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("enhet");
    }

    @Test
    public void skalOppretteOversikt() {
        System.setProperty("kodeverkendpoint.v2.url", "something");
        wicket.goToPageWith(new OversiktLerret("oversiktId", "fnr"))
                .should().containComponent(withId("lenker").and(ofType(LenkeWidget.class)))
                .should().containComponent(withId("sykepenger").and(ofType(SykepengerWidget.class)));
    }

}
