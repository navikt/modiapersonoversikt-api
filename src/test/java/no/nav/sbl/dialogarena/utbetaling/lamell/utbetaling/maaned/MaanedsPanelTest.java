package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned;

import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.SammenlagtUtbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.MaanedOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingPanel;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.markup.html.list.ListView;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static org.joda.time.DateTime.now;

public class MaanedsPanelTest extends AbstractWicketTest {

    private static final String ID = "id";

    @Override
    protected void setup() {
    }

    @Test
    public void testMaanedsPanelMedUtbetalinger() {
        Hovedytelse hovedytelse = new Hovedytelse()
                .withId(ID);

        SammenlagtUtbetaling sammenlagtUtbetaling = new SammenlagtUtbetaling().withId(ID);

        List<Hovedytelse> utbetalinger = asList(
                hovedytelse
                        .withHovedytelsedato(now())
                        .withYtelse("Ytelse 1")
                        .withYtelsesperiode(new Interval(now().minusDays(5), now()))
                        .withBruttoUtbetalt(0d)
                        .withSammenlagtTrekkBeloep()
                        .withNettoUtbetalt(0d)
                        .withSumTrekk(0d)
                        .withUtbetaltTil(new Aktoer().withNavn("Ola Nordmann"))
                        .withUtbetaltTilKonto("1112233")
                        .withUnderytelseListe(singletonList(new Underytelse()
                                .withYtelsesType("Tittel")
                                .withSatsAntall(3d)
                                .withYtelseBeloep(200.0)
                                .withSatsAntall(1.0))),
                hovedytelse
                        .withHovedytelsedato(now().minusDays(4))
                        .withYtelse("Ytelse 2")
                        .withBruttoUtbetalt(0d)
                        .withSammenlagtTrekkBeloep()
                        .withNettoUtbetalt(0d)
                        .withSumTrekk(0d)
                        .withUtbetaltTilKonto("1112233")
                        .withUtbetaltTil(new Aktoer().withNavn("Ola Nordmann"))
                        .withYtelsesperiode(new Interval(now().minusDays(10), now()))
                        .withUnderytelseListe(singletonList(new Underytelse()
                                .withYtelsesType("Tittel2")
                                .withSatsAntall(5d)
                                .withYtelseBeloep(700.0)
                                .withSatsAntall(2.0)))
        );

        List<SammenlagtUtbetaling> sammenlagteUtbetalinger = singletonList(
                sammenlagtUtbetaling
                        .withHovedytelser(utbetalinger)
        );

        MaanedsPanel maanedsPanel = new MaanedsPanel("maanedsPanel", sammenlagteUtbetalinger);
        wicketTester.goToPageWith(maanedsPanel);

        wicketTester.should().containComponent(ofType(MaanedOppsummeringPanel.class))
                .should().containComponent(ofType(ListView.class))
                .should().inComponent(ofType(ListView.class)).containComponents(2, ofType(UtbetalingPanel.class));
    }

    @Test
    public void testMaanedsPanelUtenUtbetalinger() {
        MaanedsPanel maanedsPanel = new MaanedsPanel("maanedsPanel", new ArrayList<>());
        wicketTester.goToPageWith(maanedsPanel);

        wicketTester.should().containComponent(ofType(MaanedOppsummeringPanel.class))
                .should().containComponent(ofType(ListView.class))
                .should().inComponent(ofType(ListView.class)).notContainComponent(ofType(UtbetalingPanel.class));
    }
}
