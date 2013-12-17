package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils;
import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling1;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling4;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling5;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class BilagTest {


    public static final String DAGPENGER = "Dagpenger";
    public static final String FORSKUDDSTREKK_SKATT = "Forskuddstrekk skatt";
    public static final String GRUNNBELOP = "Grunnbeløp";

    @Test
    public void skattSettesTilUnderytelseAvVanligsteHovedytelseIBidrag_EnUtbetaling_EttBilag() throws Exception {
        Utbetaling utb = new UtbetalingBuilder().setUtbetalingsDato("").setPeriode(createUtbetaling1()).createUtbetaling();
        Map<String,Map<String,Double>> belopPerUnderYtelse = UtbetalingListeUtils.summerBelopForUnderytelser(asList(utb));

        assertThat(belopPerUnderYtelse.get(DAGPENGER).get(GRUNNBELOP), is(2000.0));
        assertThat(belopPerUnderYtelse.get(DAGPENGER).get(FORSKUDDSTREKK_SKATT), is(-700.0));
    }

    @Test
    public void skattSettesTilUnderytelseAvVanligsteHovedytelseIBidrag_FlereUtbetalinger_FlereBilag() throws Exception {

        Utbetaling utbetaling = new UtbetalingBuilder().setUtbetalingsDato("").setPeriode(createUtbetaling4()).createUtbetaling();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato("").setPeriode(createUtbetaling5()).createUtbetaling();

        Map<String, Map<String,Double>> belopPerUnderYtelse = UtbetalingListeUtils.summerBelopForUnderytelser(asList(utbetaling, utbetaling1));

        assertThat(belopPerUnderYtelse.get(DAGPENGER).get(GRUNNBELOP), is(1000.0));
        assertThat(belopPerUnderYtelse.get(DAGPENGER).get("Tilleggsytelse"), is(1000.0));
        assertThat(belopPerUnderYtelse.get(DAGPENGER).get("Feilretting"), is(-1000.0));
        assertThat(belopPerUnderYtelse.get(DAGPENGER).get(FORSKUDDSTREKK_SKATT), is(-350.0));
    }
}
