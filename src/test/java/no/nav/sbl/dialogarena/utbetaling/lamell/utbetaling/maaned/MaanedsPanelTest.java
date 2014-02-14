package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.MaanedOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingPanel;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.markup.html.list.ListView;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static org.joda.time.DateTime.now;

public class MaanedsPanelTest extends AbstractWicketTest {

    @Override
    protected void setup() {
    }

    @Test
    public void testMaanedsPanelMedUtbetalinger() {
        UtbetalingBuilder utbetalingBuilder = new UtbetalingBuilder();
        List<Utbetaling> utbetalinger = asList(
                utbetalingBuilder.withUtbetalingsDato(now())
                        .withPeriode(new Interval(now().minusDays(5), now()))
                        .withUnderytelser(asList(new Underytelse("Tittel", "Spesifikasjon", optional(3d), 200.0, optional(1.0))))
                        .build(),
                utbetalingBuilder.withUtbetalingsDato(now().minusDays(4))
                        .withPeriode(new Interval(now().minusDays(10), now()))
                        .withUnderytelser(asList(new Underytelse("Tittel2", "Spesifikasjon2", optional(5d), 700.0, optional(2.0))))
                        .build());

        MaanedsPanel maanedsPanel = new MaanedsPanel("maanedsPanel", utbetalinger);
        wicketTester.goToPageWith(maanedsPanel);

        wicketTester.should().containComponent(ofType(MaanedOppsummeringPanel.class))
                .should().containComponent(ofType(ListView.class))
                .should().inComponent(ofType(ListView.class)).containComponents(2, ofType(UtbetalingPanel.class));
    }

    @Test
    public void testMaanedsPanelUtenUtbetalinger() {
        MaanedsPanel maanedsPanel = new MaanedsPanel("maanedsPanel", new ArrayList<Utbetaling>());
        wicketTester.goToPageWith(maanedsPanel);

        wicketTester.should().containComponent(ofType(MaanedOppsummeringPanel.class))
                .should().containComponent(ofType(ListView.class))
                .should().inComponent(ofType(ListView.class)).notContainComponent(ofType(UtbetalingPanel.class));
    }
}
