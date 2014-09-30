package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.joda.time.DateTime.now;

@ContextConfiguration(classes = MeldingWidgetTestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MeldingWidgetPanelTest extends WicketPageTest {

    @Test
    public void skalInneholdeRiktigeKomponenter() {
        wicket.goToPageWith(new TestMeldingWidgetPanel("melding", new Model<>(new MeldingVM(asList(createMelding("id", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "1"))))))
                .should().containComponent(withId("opprettetDato").and(ofType(Label.class)))
                .should().containComponent(withId("avsender").and(ofType(Label.class)))
                .should().containComponent(withId("melding.temagruppe").and(ofType(Label.class)))
                .should().containComponent(withId("melding.status").and(ofType(Label.class)))
                .should().containComponent(withId("statusIndikator").and(ofType(WebMarkupContainer.class)));
    }
}