package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.BilagBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetaljBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtbetalingService {

    public List<Utbetaling> hentUtbetalinger(String fnr){
        List<Utbetaling> utbetalinger = new ArrayList<>();



        utbetalinger.add(createUtbetaling1());
        utbetalinger.add(createUtbetaling2());
        utbetalinger.add(createUtbetaling3());
        utbetalinger.add(createUtbetaling4());
        utbetalinger.add(createUtbetaling5());
        utbetalinger.add(createUtbetaling6());
        utbetalinger.add(createUtbetaling7());
        return utbetalinger;
    }

    private Utbetaling createUtbetaling1(){
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Alderspensjon").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj2)).setMelding("bilag2").createBilag();

        Utbetaling utbetaling = new UtbetalingBuilder()
                                    .setPeriode("2010.01.23-2011.01.24")
                                    .setNettoBelop(1000.0)
                                    .setStatuskode("12")
                                    .setBeskrivelse("Uføre")
                                    .setUtbetalingsDato(new DateTime().now().minusDays(4))
                                    .setBilag(Arrays.asList(bilag1,bilag2)).createUtbetaling();

        return utbetaling;
    }

    private Utbetaling createUtbetaling2(){
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Uføre").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Foreldrepenger").createPosteringsDetalj();
        PosteringsDetalj detalj3 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj2,detalj3)).setMelding("bilag2").createBilag();


        Utbetaling utbetaling = new UtbetalingBuilder()
                                    .setPeriode("2010.02.23-2011.02.24")
                                    .setNettoBelop(2000.0)
                                    .setStatuskode("12")
                                    .setBeskrivelse("Trygd")
                                    .setUtbetalingsDato(new DateTime().now().minusDays(7))
                                    .setBilag(Arrays.asList(bilag1,bilag2)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling3(){
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Barnepenger").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj2)).setMelding("bilag2").createBilag();

        Utbetaling utbetaling = new UtbetalingBuilder()
                                    .setPeriode("2010.03.23-2011.03.24")
                                    .setNettoBelop(3000.10)
                                    .setStatuskode("12")
                                    .setBeskrivelse("Barnepenger")
                                    .setUtbetalingsDato(new DateTime().now().minusDays(10))
                                    .setBilag(Arrays.asList(bilag1,bilag2)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling4(){
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Trygd").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj1)).setMelding("bilag1").createBilag();

        Utbetaling utbetaling = new UtbetalingBuilder()
                .setPeriode("2010.04.23-2011.04.24")
                .setNettoBelop(4000.00)
                .setStatuskode("12")
                .setBeskrivelse("Trygd")
                .setUtbetalingsDato(new DateTime().now().minusDays(40))
                .setBilag(Arrays.asList(bilag1)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling5(){
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("APGrunnbeløp").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj2)).setMelding("bilag2").createBilag();


        Utbetaling utbetaling = new UtbetalingBuilder()
                .setPeriode("2010.05.23-2011.05.24")
                .setNettoBelop(5100.50)
                .setStatuskode("12")
                .setBeskrivelse("APGrunnbeløp")
                .setUtbetalingsDato(new DateTime().now().minusDays(84))
                .setBilag(Arrays.asList(bilag1,bilag2)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling6(){
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Pensjon").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(Arrays.asList(detalj2)).setMelding("bilag2").createBilag();


        Utbetaling utbetaling = new UtbetalingBuilder()
                .setPeriode("2010.06.23-2011.06.24")
                .setNettoBelop(6000.00)
                .setStatuskode("12")
                .setBeskrivelse("Pensjon")
                .setUtbetalingsDato(new DateTime().now().minusDays(200))
                .setBilag(Arrays.asList(bilag1,bilag2)).createUtbetaling();
        return utbetaling;
    }

     private Utbetaling createUtbetaling7(){
        Utbetaling utbetaling = new UtbetalingBuilder()
                .setUtbetalingsDato(new DateTime().now().minusDays(300))
                .createUtbetaling();
        return utbetaling;
    }

}
