package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.BilagBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetaljBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelserFraUtbetalinger;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.summerBelopForUnderytelser;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;


public class UtbetalingListeUtilsTest {

    public static final String DAGPENGER = "Dagpenger";
    public static final String SYKEPENGER = "Sykepenger";
    public static final String BARNETRYGD = "Barnetrygd";
    public static final String SKATT = "Skatt";

    @Test
    public void testHentYtelserFraUtbetalinger_ViserSortertListeAvYtelser_OgSkattBlirFjernet() throws Exception {

        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).createPosteringsDetalj();
        PosteringsDetalj sykepenger = new PosteringsDetaljBuilder().setHovedBeskrivelse(SYKEPENGER).createPosteringsDetalj();
        PosteringsDetalj barnetrygd = new PosteringsDetaljBuilder().setHovedBeskrivelse(BARNETRYGD).createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse(SKATT).createPosteringsDetalj();
        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, barnetrygd, skatt)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, sykepenger, skatt)).createBilag();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setBilag(Arrays.asList(bilag1)).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setBilag(Arrays.asList(bilag1, bilag2)).createUtbetaling();

        List<String> beskrivelser = hentYtelserFraUtbetalinger(asList(utbetaling1, utbetaling2));

        assertThat(beskrivelser.size(), is(3));
        assertThat(beskrivelser, containsInAnyOrder(DAGPENGER, SYKEPENGER, BARNETRYGD));
        assertThat(beskrivelser, not(contains(SKATT)));
    }

    @Test
    public void hentYtelserOgSummerBelop_SummerPerHovedtype() throws Exception {

        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).setBelop(1000.0).createPosteringsDetalj();
        PosteringsDetalj sykepenger = new PosteringsDetaljBuilder().setHovedBeskrivelse(SYKEPENGER).setBelop(200.0).createPosteringsDetalj();
        PosteringsDetalj barnetrygd = new PosteringsDetaljBuilder().setHovedBeskrivelse(BARNETRYGD).setBelop(300.0).createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse(SKATT).setBelop(100.0).createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, barnetrygd, skatt)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, sykepenger, skatt)).createBilag();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setBilag(Arrays.asList(bilag1)).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setBilag(Arrays.asList(bilag1, bilag2)).createUtbetaling();

        Map<String, Map<String, Double>> belopPerYtelse = UtbetalingListeUtils.summerBelopForUnderytelser(asList(utbetaling1, utbetaling2));

        assertThat(belopPerYtelse.get(DAGPENGER).get(DAGPENGER), is(3000.0));
        assertThat(belopPerYtelse.get(DAGPENGER).get(SKATT), is(300.0));
        assertThat(belopPerYtelse.get(SYKEPENGER).get(SYKEPENGER), is(200.0));
        assertThat(belopPerYtelse.get(BARNETRYGD).get(BARNETRYGD), is(600.0));
    }

    @Test
    public void testGetBelopPerUnderYtelse_EnUtbetaling_EttBilag() throws Exception {
        PosteringsDetalj dagpengerGrunn = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).setUnderBeskrivelse("Grunnbeløp").setBelop(1000.0).createPosteringsDetalj();
        PosteringsDetalj dagpengerTillegg = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).setUnderBeskrivelse("Tillegg").setBelop(1000.0).createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).setUnderBeskrivelse(SKATT).setBelop(-100.0).createPosteringsDetalj();

        Bilag bilag = new BilagBuilder().setPosteringsDetaljer(asList(dagpengerGrunn, dagpengerGrunn, dagpengerTillegg, dagpengerTillegg, skatt)).createBilag();
        Utbetaling utbetaling = new UtbetalingBuilder().setBilag(asList(bilag)).createUtbetaling();

        Map<String,Map<String,Double>> belopPerUnderYtelse = UtbetalingListeUtils.summerBelopForUnderytelser(asList(utbetaling));

        assertThat(belopPerUnderYtelse.get(DAGPENGER).get("Grunnbeløp"), is(2000.0));
        assertThat(belopPerUnderYtelse.get(DAGPENGER).get("Tillegg"), is(2000.0));
        assertThat(belopPerUnderYtelse.get(DAGPENGER).get(SKATT), is(-100.0));
    }

    @Test
    public void testGetBelopPerUnderYtelse_FlereUtbetalinger_FlereBilag() throws Exception {
        PosteringsDetalj dagpengerGrunn = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).setUnderBeskrivelse("Grunnbeløp").setBelop(1000.0).createPosteringsDetalj();
        PosteringsDetalj dagpengerTillegg = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).setUnderBeskrivelse("Tillegg").setBelop(1000.0).createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse(DAGPENGER).setUnderBeskrivelse(SKATT).setBelop(-100.0).createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(dagpengerGrunn, dagpengerGrunn, dagpengerTillegg, dagpengerTillegg, skatt)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(asList(dagpengerGrunn, dagpengerGrunn, skatt)).createBilag();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setBilag(asList(bilag1)).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setBilag(asList(bilag2)).createUtbetaling();

        Map<String,Map<String,Double>> belopPerUnderYtelse = UtbetalingListeUtils.summerBelopForUnderytelser(asList(utbetaling1, utbetaling2));

        assertThat(belopPerUnderYtelse.get(DAGPENGER).get("Grunnbeløp"), is(4000.0));
        assertThat(belopPerUnderYtelse.get(DAGPENGER).get("Tillegg"), is(2000.0));
        assertThat(belopPerUnderYtelse.get(DAGPENGER).get(SKATT), is(-200.0));
    }

    @Test
    public void testRegnUtOppsummering_Ytelser() throws Exception {
        final String alderspensjon = "Alderspensjon";
        PosteringsDetalj detalj = new PosteringsDetaljBuilder().setHovedBeskrivelse(alderspensjon).setBelop(1000.0).createPosteringsDetalj();
        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(detalj)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(asList(detalj, detalj)).createBilag();
        Bilag bilag3 = new BilagBuilder().setPosteringsDetaljer(asList(detalj, detalj)).createBilag();

        Utbetaling utbetaling = new UtbetalingBuilder().setBilag(asList(bilag1, bilag2, bilag3)).createUtbetaling();
        Map<String,Map<String,Double>> ytelserUtbetalt = summerBelopForUnderytelser(asList(utbetaling));
        assertThat(ytelserUtbetalt.get(alderspensjon).get(alderspensjon), Is.is(5000.0));
    }

}
