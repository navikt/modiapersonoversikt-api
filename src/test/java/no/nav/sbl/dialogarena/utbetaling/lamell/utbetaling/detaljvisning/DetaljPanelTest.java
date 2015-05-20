package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.*;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withTextSaying;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class DetaljPanelTest extends AbstractWicketTest {

    private DetaljPanel detaljPanel;
    private UtbetalingVM utbetalingVM;


    @Override
    protected void setup() {
        utbetalingVM = new UtbetalingVM(createMockHovedytelse());
        detaljPanel = new DetaljPanel("detaljpanel", utbetalingVM);
    }

    @Test
    public void opprettYtelsesrader() {
        List<YtelseVM> ytelseVMer = asList(
                new YtelseVM("Grunnbeløp", 100D),
                new YtelseVM("Grunnbeløp utvidet", 100D, 1D, 10D, "dag"),
                new YtelseVM("Særtillegg", 123D),
                new YtelseVM("Særtillegg utvidet", 123D, 2D, 200D, "dag"));

        ListView listView = detaljPanel.createYtelserader(ytelseVMer);
        assertThat(listView.getId(), is("underytelser"));
        assertThat(listView.getModelObject().size(), is(4));
        assertThat(listView.getModelObject().get(0), is(instanceOf(YtelseVM.class)));
    }

    @Test
    public void appendUnderytelserTilYtelseVMListe() {
        List<YtelseVM> ytelseVMListe = new ArrayList<>();
        ytelseVMListe.add(new YtelseVM("Grunnbeløp", 10D));
        Record<Hovedytelse> hovedytelse = createMockHovedytelse();
        UtbetalingVM utbetalingVM = new UtbetalingVM(hovedytelse);

        assertThat(ytelseVMListe.size(), is(1));
        detaljPanel.appendUnderytelser(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(3));

        hovedytelse.remove(Hovedytelse.underytelseListe);
        detaljPanel.appendUnderytelser(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(3));
    }

    @Test
    public void appendTrekkTilYtelseVMListe() {
        List<YtelseVM> ytelseVMListe = new ArrayList<>();
        ytelseVMListe.add(new YtelseVM("Grunnbeløp", 10D));
        Record<Hovedytelse> hovedytelse = createMockHovedytelse();
        UtbetalingVM utbetalingVM = new UtbetalingVM(hovedytelse);

        assertThat(ytelseVMListe.size(), is(1));
        detaljPanel.appendTrekk(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(2));

        hovedytelse.remove(Hovedytelse.trekkListe);
        detaljPanel.appendTrekk(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(2));
    }

    @Test
    public void appendSkattTilYtelseVMListe() {
        List<YtelseVM> ytelseVMListe = new ArrayList<>();
        ytelseVMListe.add(new YtelseVM("Grunnbeløp", 10D));
        Record<Hovedytelse> hovedytelse = createMockHovedytelse();
        UtbetalingVM utbetalingVM = new UtbetalingVM(hovedytelse);

        assertThat(ytelseVMListe.size(), is(1));
        detaljPanel.appendSkatteTrekk(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(4));

        hovedytelse.remove(Hovedytelse.skattListe);
        detaljPanel.appendSkatteTrekk(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(4));
    }

    @Test
    public void sortererUnderytelsenIRiktigRekkefolge() {
        System.setProperty("ytelse.skatt.beskrivelse.tekst", "Skattetrekk");
        UtbetalingVM utbetalingVM = new UtbetalingVM(createMockHovedytelse());
        List<YtelseVM> ytelseVMer = detaljPanel.createYtelseVMerList(utbetalingVM);

        assertThat(ytelseVMer.size(), is(6));
        assertThat(ytelseVMer.get(0).getYtelse(), is("Særtillegg"));
        assertThat(ytelseVMer.get(1).getYtelse(), is("Grunnbeløp"));
        assertThat(ytelseVMer.get(2).getYtelse(), is("Skattetrekk"));
        assertThat(ytelseVMer.get(2).getBelop(), is("-1,00"));
        assertThat(ytelseVMer.get(3).getYtelse(), is("Skattetrekk"));
        assertThat(ytelseVMer.get(3).getBelop(), is("-2,00"));
        assertThat(ytelseVMer.get(4).getYtelse(), is("Skattetrekk"));
        assertThat(ytelseVMer.get(4).getBelop(), is("-10,00"));
        assertThat(ytelseVMer.get(5).getYtelse(), is("Kreditortrekk"));
        assertThat(ytelseVMer.get(5).getBelop(), is("-140,00"));
    }

    @Test
    public void viserRiktigMetadataForUtbetalingen() {
        wicketTester.goToPageWith(detaljPanel)
                .should().containComponent(withId("konto").and(withTextSaying("11223312345")))
                .should().containComponent(withId("utbetalingsmelding").and(withTextSaying("Dette er en testmelding")));
    }

    @Test
    public void viserUnderytelseListenMedRiktigAntallUnderytelser() {
        wicketTester.goToPageWith(detaljPanel)
                .should().containComponent(withId("underytelser"))
                .should().containComponents(6, withId("underytelse"));
    }

    private Record<Hovedytelse> createMockHovedytelse() {
        return new Record<Hovedytelse>()
                .with(Hovedytelse.id, "id1")
                .with(Hovedytelse.utbetaltTil, createUtbetaltTil())
                .with(Hovedytelse.utbetaltTilKonto, "11223312345")
                .with(Hovedytelse.ytelse, "Dagpenger")
                .with(Hovedytelse.utbetalingsmelding, "Dette er en testmelding")
                .with(Hovedytelse.underytelseListe, createUnderytelser())
                .with(Hovedytelse.utbetalingsDato, now())
                .with(Hovedytelse.nettoUtbetalt, 1300D)
                .with(Hovedytelse.skattListe, createSkattListe())
                .with(Hovedytelse.sumSkatt, 12D)
                .with(Hovedytelse.trekkListe, createTrekkListe())
                .with(Hovedytelse.bruttoUtbetalt, 2000D)
                .with(Hovedytelse.sammenlagtTrekkBeloep, -200D)
                .with(Hovedytelse.sumTrekk, 14D);
    }

    private List<Record<Underytelse>> createUnderytelser() {
        return asList(new Record<Underytelse>()
                    .with(Underytelse.ytelsesType, "Grunnbeløp")
                    .with(Underytelse.satsBeloep, 10D)
                    .with(Underytelse.satsType, "SatsType")
                    .with(Underytelse.satsAntall, 1D)
                    .with(Underytelse.ytelseBeloep, 100D),
                new Record<Underytelse>()
                        .with(Underytelse.ytelsesType, "Særtillegg")
                        .with(Underytelse.satsBeloep, 11D)
                        .with(Underytelse.satsType, "SatsTypeSærtillegg")
                        .with(Underytelse.satsAntall, 2D)
                        .with(Underytelse.ytelseBeloep, 1200D));
    }

    private List<Record<Trekk>> createTrekkListe() {
        return asList(new Record<Trekk>()
                    .with(Trekk.kreditor, "Kreditor AS")
                    .with(Trekk.trekksType, "Kreditortrekk")
                    .with(Trekk.trekkBeloep, -140D));
    }

    private List<Double> createSkattListe() {
        return asList(-10D, -1D, -2D);
    }

    private Record<Aktoer> createUtbetaltTil() {
        return new Record<Aktoer>()
            .with(Aktoer.aktoerId, "11223312345")
            .with(Aktoer.navn, "Ola Nordmann");
    }
}