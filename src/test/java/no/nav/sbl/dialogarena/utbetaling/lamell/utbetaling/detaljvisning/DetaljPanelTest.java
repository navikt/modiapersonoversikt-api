package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Trekk;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.Ignore;
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

    @Ignore
    @Test
    public void appendUnderytelserTilYtelseVMListe() {
        List<YtelseVM> ytelseVMListe = new ArrayList<>();
        ytelseVMListe.add(new YtelseVM("Grunnbeløp", 10D));
        Hovedytelse hovedytelse = createMockHovedytelse();
        UtbetalingVM utbetalingVM = new UtbetalingVM(hovedytelse);

        assertThat(ytelseVMListe.size(), is(1));
        detaljPanel.appendUnderytelser(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(3));

        detaljPanel.appendUnderytelser(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(3));
    }

    @Ignore
    @Test
    public void appendTrekkTilYtelseVMListe() {
        List<YtelseVM> ytelseVMListe = new ArrayList<>();
        ytelseVMListe.add(new YtelseVM("Grunnbeløp", 10D));
        Hovedytelse hovedytelse = createMockHovedytelse();
        UtbetalingVM utbetalingVM = new UtbetalingVM(hovedytelse);

        assertThat(ytelseVMListe.size(), is(1));
        detaljPanel.appendTrekk(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(2));

        detaljPanel.appendTrekk(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(2));
    }

    @Ignore
    @Test
    public void appendSkattTilYtelseVMListe() {
        List<YtelseVM> ytelseVMListe = new ArrayList<>();
        ytelseVMListe.add(new YtelseVM("Grunnbeløp", 10D));
        Hovedytelse hovedytelse = createMockHovedytelse();
        UtbetalingVM utbetalingVM = new UtbetalingVM(hovedytelse);

        assertThat(ytelseVMListe.size(), is(1));
        detaljPanel.appendSkatteTrekk(utbetalingVM, ytelseVMListe);
        assertThat(ytelseVMListe.size(), is(4));

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
                .should().containComponent(withId("konto").and(withTextSaying("***REMOVED***")))
                .should().containComponent(withId("utbetalingsmelding").and(withTextSaying("Dette er en testmelding")));
    }

    @Test
    public void viserUnderytelseListenMedRiktigAntallUnderytelser() {
        wicketTester.goToPageWith(detaljPanel)
                .should().containComponent(withId("underytelser"))
                .should().containComponents(6, withId("underytelse"));
    }

    @Test
    public void viserPositiveTrekkMedTilbakebetaling() {
        System.setProperty("ytelse.skatt.beskrivelse.tekst", "Skattetrekk");
        UtbetalingVM utbetalingVM = new UtbetalingVM(createMockHovedytelseMedPositiveTrekk());
        List<YtelseVM> ytelseVMer = detaljPanel.createYtelseVMerList(utbetalingVM);

        assertThat(ytelseVMer.get(0).getYtelse(), is("Tilbakebetaling skattetrekk"));
        assertThat(ytelseVMer.get(1).getYtelse(), is("Skattetrekk"));
        assertThat(ytelseVMer.get(2).getYtelse(), is("Tilbakebetaling kreditortrekk"));
    }

    private Hovedytelse createMockHovedytelse() {
        return new Hovedytelse()
                .withId("id1")
                .withUtbetaltTil(createUtbetaltTil())
                .withUtbetaltTilKonto("***REMOVED***")
                .withYtelse("Dagpenger")
                .withUtbetalingsmelding("Dette er en testmelding")
                .withUnderytelseListe(createUnderytelser())
                .withUtbetalingsDato(now())
                .withNettoUtbetalt(1300D)
                .withSkattListe(createSkattListe())
                .withSumSkatt(12D)
                .withTrekkListe(createTrekkListe())
                .withBruttoUtbetalt(2000D)
                .withSammenlagtTrekkBeloep(-200D)
                .withSumTrekk(14D);
    }

    private List<Underytelse> createUnderytelser() {
        return asList(
                new Underytelse()
                        .withYtelsesType("Grunnbeløp")
                        .withSatsBeloep(10D)
                        .withSatsType("SatsType")
                        .withSatsAntall(1D)
                        .withYtelseBeloep(100D),
                new Underytelse()
                        .withYtelsesType("Særtillegg")
                        .withSatsBeloep(11D)
                        .withSatsType("SatsTypeSærtillegg")
                        .withSatsAntall(2D)
                        .withYtelseBeloep(1200D));
    }

    private List<Trekk> createTrekkListe() {
        return asList(new Trekk()
                    .withKreditor("Kreditor AS")
                    .withTrekksType("Kreditortrekk")
                    .withTrekkBeloep(-140D));
    }


    private List<Double> createSkattListe() {
        return asList(-10D, -1D, -2D);
    }

    private Aktoer createUtbetaltTil() {
        return new Aktoer()
            .withAktoerId("***REMOVED***")
            .withNavn("Ola Nordmann");
    }

    private Hovedytelse createMockHovedytelseMedPositiveTrekk() {
        return new Hovedytelse()
                .withId("id1")
                .withUtbetaltTil(createUtbetaltTil())
                .withUtbetaltTilKonto("***REMOVED***")
                .withYtelse("Dagpenger")
                .withUtbetalingsmelding("Dette er en testmelding")
                .withTrekkListe(createTrekkListeMedTilbakeBetaling())
                .withBruttoUtbetalt(2000D)
                .withSammenlagtTrekkBeloep(-200D)
                .withSumTrekk(14D)
                .withSkattListe(createTilbakebetalingSkattListe())
                .withSumSkatt(-9D);
    }

    private List<Trekk> createTrekkListeMedTilbakeBetaling() {
        return asList(new Trekk()
                .withKreditor("Test-Kreditor AS")
                .withTrekksType("Kreditortrekk")
                .withTrekkBeloep(100D));
    }

    private List<Double> createTilbakebetalingSkattListe() {
        return asList(10D, -1D);
    }
}