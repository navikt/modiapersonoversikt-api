package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.TreeSet;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
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

        assertThat(beskrivelser.size(), is(2));
        assertThat(beskrivelser.contains("Dagpenger"), is(equalTo(true)));
        assertThat(beskrivelser.contains("Sykepenger"), is(equalTo(true)));
        assertThat(utbetaling.getBeskrivelse(),is("Dagpenger, Sykepenger"));
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

        assertThat(beskrivelser.size(), is(2));
        assertThat(beskrivelser.contains("Dagpenger"), is(equalTo(true)));
        assertThat(beskrivelser.contains("Sykepenger"), is(equalTo(true)));
        assertThat(utbetaling.getBeskrivelse(),is("Dagpenger, Sykepenger"));
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

    @Test
    public void skalTransformereUtbetaling() throws Exception {
        WSUtbetaling wsUtbetaling = new WSUtbetalingTestData().createUtbetaling1();
        String alderspensjon = "Alderspensjon";
        String kontoNr = "12345678900";

        Utbetaling u = new Utbetaling(wsUtbetaling);

        assertThat(u.getUtbetalingsDato(), is(wsUtbetaling.getUtbetalingDato()));
        assertThat(u.getStartDate(), is(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato()));
        assertThat(u.getEndDate(), is(wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato()));
        assertThat(u.getNettoBelop(), is(wsUtbetaling.getNettobelop()));
        assertThat(u.getBruttoBelop(), is(wsUtbetaling.getBruttobelop()));
        assertThat(u.getBeskrivelse(), is(alderspensjon));
        assertThat(u.getStatuskode(), is(wsUtbetaling.getStatusKode()));
        assertThat(u.getKontoNr(), is(kontoNr));

        assertThat(u.getBilag().size(), is(wsUtbetaling.getBilagListe().size()));

        Bilag bilag1 = u.getBilag().get(0);
        WSBilag wsBilag1 = wsUtbetaling.getBilagListe().get(0);
        assertThat(bilag1.getPosteringsDetaljer().size(), is(wsBilag1.getPosteringsdetaljerListe().size()));
        assertThat(bilag1.getMelding(), is(equalTo(wsBilag1.getMeldingListe().get(0).getMeldingtekst())));

        PosteringsDetalj posteringsDetalj = bilag1.getPosteringsDetaljer().get(0);
        WSPosteringsdetaljer wsPosteringsdetalj = wsBilag1.getPosteringsdetaljerListe().get(0);
        assertThat(posteringsDetalj.getHovedBeskrivelse(), is(wsPosteringsdetalj.getKontoBeskrHoved()));
        assertThat(posteringsDetalj.getKontoNr(), is(wsPosteringsdetalj.getKontonr()));

        Bilag bilag2 = u.getBilag().get(1);
        WSBilag wsBilag2 = wsUtbetaling.getBilagListe().get(1);
        assertThat(bilag2.getPosteringsDetaljer().size(), is(wsBilag2.getPosteringsdetaljerListe().size()));

        posteringsDetalj = bilag2.getPosteringsDetaljer().get(0);
        wsPosteringsdetalj = wsBilag2.getPosteringsdetaljerListe().get(0);
        assertThat(posteringsDetalj.getKontoNr(), is(wsPosteringsdetalj.getKontonr()));
        assertThat(posteringsDetalj.getHovedBeskrivelse(), is(wsPosteringsdetalj.getKontoBeskrHoved()));
    }

}
