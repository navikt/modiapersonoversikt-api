package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.BilagBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetaljBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelserFraUtbetalinger;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelserOgSummerBelop;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class UtbetalingListeUtilsTest {

    @Test
    public void testHentYtelserFraUtbetalinger_ViserSortertListeAvYtelser_OgSkattBlirFjernet() throws Exception {

        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Dagpenger").createPosteringsDetalj();
        PosteringsDetalj sykepenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Sykepenger").createPosteringsDetalj();
        PosteringsDetalj barnetrygd = new PosteringsDetaljBuilder().setHovedBeskrivelse("Barnetrygd").createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();
        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, barnetrygd, skatt)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, sykepenger, skatt)).createBilag();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setBilag(Arrays.asList(bilag1)).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setBilag(Arrays.asList(bilag1, bilag2)).createUtbetaling();

        List<String> beskrivelser = hentYtelserFraUtbetalinger(asList(utbetaling1, utbetaling2));

        assertThat(beskrivelser.size(), is(3) );
        assertThat(beskrivelser.contains("Dagpenger"), is(equalTo(true)));
        assertThat(beskrivelser.contains("Sykepenger"), is(equalTo(true)));
        assertThat(beskrivelser.contains("Barnetrygd"), is(equalTo(true)));
        assertThat(utbetaling1.getBeskrivelse(), is("Barnetrygd, Dagpenger"));
    }

    @Test
    public void skalHenteYtelserOgSummereBelop() throws Exception {

        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Dagpenger").setBelop(1000.0).createPosteringsDetalj();
        PosteringsDetalj sykepenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Sykepenger").setBelop(200.0).createPosteringsDetalj();
        PosteringsDetalj barnetrygd = new PosteringsDetaljBuilder().setHovedBeskrivelse("Barnetrygd").setBelop(300.0).createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").setBelop(100.0).createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, barnetrygd, skatt)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, sykepenger, skatt)).createBilag();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setBilag(Arrays.asList(bilag1)).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setBilag(Arrays.asList(bilag1, bilag2)).createUtbetaling();

        Map<String,Double> belopPerYtelse = hentYtelserOgSummerBelop(asList(utbetaling1, utbetaling2));

        assertThat(belopPerYtelse.get("Dagpenger"), is(3000.0));
        assertThat(belopPerYtelse.get("Sykepenger"), is(200.0));
        assertThat(belopPerYtelse.get("Barnetrygd"), is(600.0));
        assertThat(belopPerYtelse.get("Skatt"), is(300.0));
    }

}
