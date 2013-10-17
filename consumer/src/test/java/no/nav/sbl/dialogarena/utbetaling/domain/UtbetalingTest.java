package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.TreeSet;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class UtbetalingTest {

    @Test
    public void extractUtbetalingsBeskrivelseFraDetalj() throws Exception {
        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Dagpenger").createPosteringsDetalj();
        PosteringsDetalj sykepenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Sykepenger").createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();
        Bilag bilag = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, sykepenger, skatt)).createBilag();
        Utbetaling utbetaling = new UtbetalingBuilder().setBilag(Arrays.asList(bilag)).createUtbetaling();

        TreeSet<String> beskrivelser = (TreeSet<String>) utbetaling.getBeskrivelser();

        assertThat(beskrivelser.size(), is(3));
        assertThat(beskrivelser.contains("Dagpenger"), is(equalTo(true)));
        assertThat(beskrivelser.contains("Sykepenger"), is(equalTo(true)));
        assertThat(beskrivelser.contains("Skatt"), is(equalTo(true)));
        assertThat(utbetaling.getBeskrivelse(),is("Dagpenger, Skatt, Sykepenger"));
    }

    @Test
    public void extractUtbetalingsBeskrivelseFraDetalj_FlereBilagMedForskjelligeDetaljer() throws Exception {
        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Dagpenger").createPosteringsDetalj();
        PosteringsDetalj sykepenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Sykepenger").createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();
        Bilag bilag = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, skatt)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(sykepenger, skatt)).createBilag();
        Utbetaling utbetaling = new UtbetalingBuilder().setBilag(Arrays.asList(bilag, bilag2)).createUtbetaling();

        TreeSet<String> beskrivelser = (TreeSet<String>) utbetaling.getBeskrivelser();

        assertThat(beskrivelser.size(), is(3));
        assertThat(beskrivelser.contains("Dagpenger"), is(equalTo(true)));
        assertThat(beskrivelser.contains("Sykepenger"), is(equalTo(true)));
        assertThat(beskrivelser.contains("Skatt"), is(equalTo(true)));
        assertThat(utbetaling.getBeskrivelse(),is("Dagpenger, Skatt, Sykepenger"));
    }

    @Test
    public void extractUtbetalingsBeskrivelseFraDetalj_FlereBilagMedTommeBeskrivelserDetaljer_TomBeskrivelse() throws Exception {
        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("").createPosteringsDetalj();
        PosteringsDetalj sykepenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("").createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse("").createPosteringsDetalj();
        Bilag bilag = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, skatt)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(sykepenger, skatt)).createBilag();
        Utbetaling utbetaling = new UtbetalingBuilder().setBilag(Arrays.asList(bilag, bilag2)).createUtbetaling();

        TreeSet<String> beskrivelser = (TreeSet<String>) utbetaling.getBeskrivelser();

        assertThat(beskrivelser.size(), is(1));
        assertThat(beskrivelser.contains(""), is(equalTo(true)));
        assertThat(utbetaling.getBeskrivelse(),is(""));
    }

    @Test
    public void equals_ForskjelligeBilag_ForskjelligeUtbetalinger() throws Exception {
        PosteringsDetalj dagpenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Dagpenger").createPosteringsDetalj();
        PosteringsDetalj sykepenger = new PosteringsDetaljBuilder().setHovedBeskrivelse("Sykepenger").createPosteringsDetalj();
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();
        Bilag bilag = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(dagpenger, skatt)).createBilag();
        DateTime now = DateTime.now();

        Utbetaling utbetaling = new UtbetalingBuilder().setUtbetalingsDato(now).setBilag(Arrays.asList(bilag)).createUtbetaling();

        Bilag bilag4 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(sykepenger, skatt)).createBilag();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(now).setBilag(Arrays.asList(bilag4)).createUtbetaling();

        assertThat(utbetaling, is(not(equalTo(utbetaling1))));
    }

    @Test
    public void equals_LikeBilag_LikeUtbetalinger() throws Exception {
        PosteringsDetalj skatt = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();
        Bilag bilag = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(skatt)).createBilag();
        DateTime now = DateTime.now();
        Utbetaling utbetaling = new UtbetalingBuilder().setUtbetalingsDato(now).setBilag(Arrays.asList(bilag)).createUtbetaling();

        Bilag bilag4 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(skatt)).createBilag();
        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(now).setBilag(Arrays.asList(bilag4)).createUtbetaling();

        assertThat(utbetaling, is(equalTo(utbetaling1)));
    }

    @Test
    public void extractDatoFromPeriodeString(){
        Utbetaling utbetaling = new UtbetalingBuilder().setPeriode("2011.05.21-2012.02.21").createUtbetaling();
        assertThat(utbetaling.getStartDate() , is(equalTo(new DateTime(2011,5,21,0,0))));
        assertThat(utbetaling.getEndDate() , is(equalTo(new DateTime(2012,2,21,0,0))));
    }


    @Test
    public void extractDatoFromEmptyPeriodeString(){
        Utbetaling utbetaling = new UtbetalingBuilder().setPeriode("").createUtbetaling();
        assertThat(utbetaling.getStartDate() , is(nullValue()));
        assertThat(utbetaling.getEndDate() , is(nullValue()));
    }

    @Test
    public void extractDatoFromNullPeriodeString(){
        Utbetaling utbetaling = new UtbetalingBuilder().setPeriode(null).createUtbetaling();
        assertThat(utbetaling.getStartDate() , is(nullValue()));
        assertThat(utbetaling.getEndDate() , is(nullValue()));
    }

}
