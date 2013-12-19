package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling1;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling2;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling3;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UtbetalingTransformerTest {

    private static final String FORSKUDDSTREKK_SKATT = "Forskuddstrekk skatt";
    private static final String GRUNNBELØP = "Grunnbeløp";
    private UtbetalingTransformer transformer;

    @Before
    public void setUp() throws Exception {
        transformer = new UtbetalingTransformer();
    }

    @Test
    public void testUtbetalingerBlirTransformertTilTransformObjekter() throws Exception {
        WSUtbetaling wsUtbetaling = createUtbetaling1();

        List<UtbetalingTransformObjekt> transformObjekter = transformer.createTransformObjekter(asList(wsUtbetaling));

        assertThat(transformObjekter.size(), is(4));
        assertThat(transformObjekter.get(0).getUtbetalingsDato(), is(wsUtbetaling.getUtbetalingDato()));
        assertThat(transformObjekter.get(0).getKontonummer(), is(wsUtbetaling.getGironr()));
        assertThat(transformObjekter.get(0).getValuta(), is(wsUtbetaling.getValuta()));
        assertThat(transformObjekter.get(0).getMottaker(), is(wsUtbetaling.getUtbetalingMottaker().getNavn()));
        assertThat(transformObjekter.get(0).getMottakerId(), is(wsUtbetaling.getUtbetalingMottaker().getMottakerId()));
        assertThat(transformObjekter.get(0).getUtbetalingsDato(), is(wsUtbetaling.getUtbetalingDato()));

        Double detaljBelop1 = wsUtbetaling.getBilagListe().get(0).getPosteringsdetaljerListe().get(0).getBelop();
        Double detaljBelop2 = wsUtbetaling.getBilagListe().get(0).getPosteringsdetaljerListe().get(1).getBelop();
        Double detaljBelop3 = wsUtbetaling.getBilagListe().get(1).getPosteringsdetaljerListe().get(0).getBelop();
        Double detaljBelop4 = wsUtbetaling.getBilagListe().get(1).getPosteringsdetaljerListe().get(1).getBelop();
        assertThat(transformObjekter.get(0).getBelop(), is(detaljBelop1));
        assertThat(transformObjekter.get(1).getBelop(), is(detaljBelop2));
        assertThat(transformObjekter.get(2).getBelop(), is(detaljBelop3));
        assertThat(transformObjekter.get(3).getBelop(), is(detaljBelop4));
    }

    @Test
    public void testSkattBlirLagtSomUnderYtelse() throws Exception {
        transformer = new UtbetalingTransformer();
        WSUtbetaling wsUtbetaling = createUtbetaling1();

        transformer.transformerSkatt(asList(wsUtbetaling));
        List<UtbetalingTransformObjekt> transformObjekter = transformer.createTransformObjekter(asList(wsUtbetaling));

        for (UtbetalingTransformObjekt transformObjekt : transformObjekter) {
            assertThat(transformObjekt.getHovedYtelse().equalsIgnoreCase("Dagpenger"), is(true));
        }
        assertThat(transformObjekter.get(0).getUnderYtelse().equalsIgnoreCase(GRUNNBELØP), is(true));
        assertThat(transformObjekter.get(1).getUnderYtelse().equalsIgnoreCase(FORSKUDDSTREKK_SKATT), is(true));
        assertThat(transformObjekter.get(2).getUnderYtelse().equalsIgnoreCase(GRUNNBELØP), is(true));
        assertThat(transformObjekter.get(3).getUnderYtelse().equalsIgnoreCase(FORSKUDDSTREKK_SKATT), is(true));
    }

    @Test
    public void lagUtbetalinger_FireYtelser_Gir_EnUtbetaling() throws Exception {
        List<Utbetaling> utbetalinger = transformer.createUtbetalinger(asList(createUtbetaling1()));

        assertThat(utbetalinger.size(), is(1));
        assertThat(utbetalinger.get(0).getHovedytelse(), is("Dagpenger"));
        assertThat(utbetalinger.get(0).getUnderytelser().size(), is(2));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getTittel(), is(FORSKUDDSTREKK_SKATT));
        assertThat(utbetalinger.get(0).getUnderytelser().get(1).getTittel(), is(GRUNNBELØP));
    }

    @Test
    public void lagUtbetalinger_Ytelser_Gir_ToUtbetalinger() throws Exception {
        List<Utbetaling> utbetalinger = transformer.createUtbetalinger(asList(createUtbetaling1(), createUtbetaling3(), createUtbetaling2()));

        assertThat(utbetalinger.size(), is(3));
        assertThat(utbetalinger.get(0).getHovedytelse(), is("Dagpenger"));
        assertThat(utbetalinger.get(0).getUnderytelser().size(), is(2));
        assertThat(utbetalinger.get(0).getBrutto(), is(2000.0));
        assertThat(utbetalinger.get(0).getTrekk(), is(-700.0));
        assertThat(utbetalinger.get(0).getUtbetalt(), is(1300.0));

        assertThat(utbetalinger.get(1).getHovedytelse(), is("Foreldrepenger"));
        assertThat(utbetalinger.get(1).getUnderytelser().size(), is(2));
        assertThat(utbetalinger.get(1).getBrutto(), is(1000.0));
        assertThat(utbetalinger.get(1).getTrekk(), is(-350.0));
        assertThat(utbetalinger.get(1).getUtbetalt(), is(650.0));

        assertThat(utbetalinger.get(2).getHovedytelse(), is("Uføre"));
        assertThat(utbetalinger.get(2).getUnderytelser().size(), is(3));
        assertThat(utbetalinger.get(2).getBrutto(), is(2500.0));
        assertThat(utbetalinger.get(2).getTrekk(), is(-1700.0));
        assertThat(utbetalinger.get(2).getUtbetalt(), is(800.0));
    }

    @Test
    public void lagUtbetalinger_slaaSammenUnderYtelserMedSammeTittel() throws Exception {
        List<Utbetaling> utbetalinger = transformer.createUtbetalinger(asList(createUtbetaling1()));

        assertThat(utbetalinger.size(), is(2));
        assertThat(utbetalinger.get(0).getHovedytelse(), is("Dagpenger"));
        assertThat(utbetalinger.get(0).getUnderytelser().size(), is(1));

        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getTittel(), is(GRUNNBELØP));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getSpesifikasjon(), is("Ekstra detaljinfo"));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getAntall(), is(12));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getSats(), is(123.0));
        assertThat(utbetalinger.get(0).getUnderytelser().get(0).getBelop(), is(2000.0));

        assertThat(utbetalinger.get(1).getUnderytelser().get(0).getTittel(), is(FORSKUDDSTREKK_SKATT));
        assertThat(utbetalinger.get(1).getUnderytelser().get(0).getSpesifikasjon(), is(""));
        assertThat(utbetalinger.get(1).getUnderytelser().get(0).getAntall(), is(1));
        assertThat(utbetalinger.get(1).getUnderytelser().get(0).getSats(), is(1.0));
        assertThat(utbetalinger.get(1).getUnderytelser().get(0).getBelop(), is(-700.0));
    }


}
