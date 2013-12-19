package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData;
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

        String grunnbelop = "Grunnbeløp";
        String forskuddstrekkSkatt = "Forskuddstrekk skatt";
        for (UtbetalingTransformObjekt transformObjekt : transformObjekter) {
            assertThat(transformObjekt.getHovedYtelse().equalsIgnoreCase("Dagpenger"), is(true));
        }
        assertThat(transformObjekter.get(0).getUnderYtelse().equalsIgnoreCase(grunnbelop), is(true));
        assertThat(transformObjekter.get(1).getUnderYtelse().equalsIgnoreCase(forskuddstrekkSkatt), is(true));
        assertThat(transformObjekter.get(2).getUnderYtelse().equalsIgnoreCase(grunnbelop), is(true));
        assertThat(transformObjekter.get(3).getUnderYtelse().equalsIgnoreCase(forskuddstrekkSkatt), is(true));
    }



    @Test
    public void lagUtbetalinger_FireYtelser_Gir_EnUtbetaling() throws Exception {
        List<Utbetaling> utbetalinger = transformer.createUtbetalinger(asList(createUtbetaling1()));

        assertThat(utbetalinger.size(), is(1));
        assertThat(utbetalinger.get(0).getHovedytelse(), is("Dagpenger"));
        assertThat(utbetalinger.get(0).getUnderytelser().size(), is(4));
    }


    @Test
    public void lagUtbetalinger_Ytelser_Gir_ToUtbetalinger() throws Exception {
        List<Utbetaling> utbetalinger = transformer.createUtbetalinger(asList(createUtbetaling1(), createUtbetaling3(), createUtbetaling2()));

        assertThat(utbetalinger.size(), is(3));
        assertThat(utbetalinger.get(0).getHovedytelse(), is("Dagpenger"));
        assertThat(utbetalinger.get(0).getUnderytelser().size(), is(4));
        assertThat(utbetalinger.get(1).getHovedytelse(), is("Foreldrepenger"));
        assertThat(utbetalinger.get(1).getUnderytelser().size(), is(2));
        assertThat(utbetalinger.get(2).getHovedytelse(), is("Uføre"));
        assertThat(utbetalinger.get(2).getUnderytelser().size(), is(4));
    }



}
