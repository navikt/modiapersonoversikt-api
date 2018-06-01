package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.util.Collections.singletonList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.joda.time.DateTime.now;

@ContextConfiguration(classes = MockServiceTestContext.class)
@ExtendWith(SpringExtension.class)
public class MeldingerWidgetPanelTest extends WicketPageTest {

    @Test
    public void skalInneholdeRiktigeKomponenter() {
        wicket.goToPageWith(new MeldingerWidgetPanel("melding", new Model<>(new WidgetMeldingVM(
                singletonList(createMelding("id", SPORSMAL_SKRIFTLIG, now(), Temagruppe.ARBD, "1")),
                false))))
                .should().containComponent(withId("meldingsstatus").and(ofType(Label.class)))
                .should().containComponent(withId("visningsDato").and(ofType(Label.class)));
    }
}