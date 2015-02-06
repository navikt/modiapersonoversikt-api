package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Konto;
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
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static org.joda.time.DateTime.now;

public class MaanedsPanelTest extends AbstractWicketTest {

    private static final String ID = "id";

    @Override
    protected void setup() {
    }

    @Test
    public void testMaanedsPanelMedUtbetalinger() {
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID);

        List<Record<Hovedytelse>> utbetalinger = asList(
                hovedytelse
                        .with(Hovedytelse.hovedytelsedato, now())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusDays(5), now()))
                        .with(Hovedytelse.bruttoUtbetalt, 0d)
                        .with(Hovedytelse.sammenlagtTrekkBeloep, 0d)
                        .with(Hovedytelse.nettoUtbetalt, 0d)
                        .with(Hovedytelse.sumTrekk, 0d)
                        .with(Hovedytelse.utbetaltTil, new Record<Aktoer>().with(Aktoer.navn, "Ola Nordmann"))
                        .with(Hovedytelse.utbetaltTilKonto, new Record<Konto>().with(Konto.kontonummer, "1112233"))
                        .with(Hovedytelse.underytelseListe, asList(new Record<Underytelse>()
                                .with(Underytelse.ytelsesType, "Tittel")
                                .with(Underytelse.satsAntall, 3d)
                                .with(Underytelse.ytelseBeloep, 200.0)
                                .with(Underytelse.satsAntall, 1.0))),
                hovedytelse
                        .with(Hovedytelse.hovedytelsedato, now().minusDays(4))
                        .with(Hovedytelse.bruttoUtbetalt, 0d)
                        .with(Hovedytelse.sammenlagtTrekkBeloep, 0d)
                        .with(Hovedytelse.nettoUtbetalt, 0d)
                        .with(Hovedytelse.sumTrekk, 0d)
                        .with(Hovedytelse.utbetaltTilKonto, new Record<Konto>().with(Konto.kontonummer, "1112233"))
                        .with(Hovedytelse.utbetaltTil, new Record<Aktoer>().with(Aktoer.navn, "Ola Nordmann"))
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusDays(10), now()))
                        .with(Hovedytelse.underytelseListe, asList(new Record<Underytelse>()
                                .with(Underytelse.ytelsesType, "Tittel2")
                                .with(Underytelse.satsAntall, 5d)
                                .with(Underytelse.ytelseBeloep, 700.0)
                                .with(Underytelse.satsAntall, 2.0)))
        );

        MaanedsPanel maanedsPanel = new MaanedsPanel("maanedsPanel", utbetalinger);
        wicketTester.goToPageWith(maanedsPanel);

        wicketTester.should().containComponent(ofType(MaanedOppsummeringPanel.class))
                .should().containComponent(ofType(ListView.class))
                .should().inComponent(ofType(ListView.class)).containComponents(2, ofType(UtbetalingPanel.class));
    }

    @Test
    public void testMaanedsPanelUtenUtbetalinger() {
        MaanedsPanel maanedsPanel = new MaanedsPanel("maanedsPanel", new ArrayList<Record<Hovedytelse>>());
        wicketTester.goToPageWith(maanedsPanel);

        wicketTester.should().containComponent(ofType(MaanedOppsummeringPanel.class))
                .should().containComponent(ofType(ListView.class))
                .should().inComponent(ofType(ListView.class)).notContainComponent(ofType(UtbetalingPanel.class));
    }
}
