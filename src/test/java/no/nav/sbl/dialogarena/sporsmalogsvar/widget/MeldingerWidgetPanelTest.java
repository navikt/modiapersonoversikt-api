package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.joda.time.DateTime.now;

@ContextConfiguration(classes = ServiceTestContext.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MeldingerWidgetPanelTest extends WicketPageTest {

    @Test
    public void skalInneholdeRiktigeKomponenter() {
        wicket.goToPageWith(new MeldingerWidgetPanel("melding", new Model<>(new WidgetMeldingVM(asList(createMelding("id", SPORSMAL_SKRIFTLIG, now(), Temagruppe.ARBD, "1"))))))
                .should().containComponent(withId("meldingsstatus").and(ofType(Label.class)))
                .should().containComponent(withId("opprettetDato").and(ofType(Label.class)));
    }
}