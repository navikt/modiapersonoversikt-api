package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.*;
import no.nav.sbl.dialogarena.sak.widget.SaksoversiktWidget;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import no.nav.sbl.dialogarena.varsel.config.VarslerMock;
import no.nav.sbl.dialogarena.varsel.config.VarslingContext;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        SykepengerWidgetMockContext.class,
        UtbetalingerMockContext.class,
        SaksoversiktMockContext.class,
        SporsmalOgSvarMockContext.class,
        VarslingMockContext.class
})
public class OversiktLerretTest extends WicketPageTest {

    @Test
    public void skalOppretteOversikt() {
        System.setProperty("kodeverkendpoint.v2.url", "something");
        wicket.goToPageWith(new OversiktLerret("oversiktId", "fnr"))
                .should().containComponent(withId("lenker").and(ofType(LenkeWidget.class)))
                .should().containComponent(withId("sykepenger").and(ofType(SykepengerWidget.class)))
                .should().containComponent(withId("meldinger").and(ofType(MeldingerWidget.class)))
                .should().containComponent(withId("saksoversikt").and(ofType(SaksoversiktWidget.class)))
                .should().containComponent(withId("varsling-lenke").and(ofType(OversiktLerret.VarslerAjaxLink.class)));
    }

}
