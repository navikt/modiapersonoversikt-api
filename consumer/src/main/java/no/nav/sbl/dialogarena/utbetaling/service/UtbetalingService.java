package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.BilagBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetaljBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static org.joda.time.DateTime.now;

public class UtbetalingService {

    @Inject
    UtbetalingPortType utbetalingPortType;

    public List<Utbetaling> hentUtbetalinger(String fnr) {
        return (asList(
                createUtbetaling1(),
                createUtbetaling2(),
                createUtbetaling3(),
                createUtbetaling4(),
                createUtbetaling5(),
                createUtbetaling6(),
                createUtbetaling7()
        ));
    }

    private Utbetaling createUtbetaling1() {
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Alderspensjon").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(asList(detalj2)).setMelding("bilag2").createBilag();

        return new UtbetalingBuilder()
                .setPeriode("2010.01.23-2011.01.24")
                .setNettoBelop(1000.0)
                .setStatuskode("12")
                .setBeskrivelse("Uføre")
                .setUtbetalingsDato(now().minusDays(4))
                .setBilag(asList(bilag1, bilag2)).createUtbetaling();
    }

    private Utbetaling createUtbetaling2() {
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Uføre").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Foreldrepenger").createPosteringsDetalj();
        PosteringsDetalj detalj3 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(asList(detalj2, detalj3)).setMelding("bilag2").createBilag();


        Utbetaling utbetaling = new UtbetalingBuilder()
                .setPeriode("2010.02.23-2011.02.24")
                .setNettoBelop(2000.0)
                .setStatuskode("12")
                .setBeskrivelse("Trygd")
                .setUtbetalingsDato(now().minusDays(7))
                .setBilag(asList(bilag1, bilag2)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling3() {
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Barnepenger").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(asList(detalj2)).setMelding("bilag2").createBilag();

        Utbetaling utbetaling = new UtbetalingBuilder()
                .setPeriode("2010.03.23-2011.03.24")
                .setNettoBelop(3000.10)
                .setStatuskode("12")
                .setBeskrivelse("Barnepenger")
                .setUtbetalingsDato(now().minusDays(10))
                .setBilag(asList(bilag1, bilag2)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling4() {
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Trygd").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(detalj1)).setMelding("bilag1").createBilag();

        Utbetaling utbetaling = new UtbetalingBuilder()
                .setPeriode("2010.04.23-2011.04.24")
                .setNettoBelop(4000.00)
                .setStatuskode("12")
                .setBeskrivelse("Trygd")
                .setUtbetalingsDato(new DateTime().now().minusDays(40))
                .setBilag(asList(bilag1)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling5() {
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("APGrunnbeløp").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(asList(detalj2)).setMelding("bilag2").createBilag();


        Utbetaling utbetaling = new UtbetalingBuilder()
                .setPeriode("2010.05.23-2011.05.24")
                .setNettoBelop(5100.50)
                .setStatuskode("12")
                .setBeskrivelse("APGrunnbeløp")
                .setUtbetalingsDato(new DateTime().now().minusDays(84))
                .setBilag(asList(bilag1, bilag2)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling6() {
        PosteringsDetalj detalj1 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Pensjon").createPosteringsDetalj();
        PosteringsDetalj detalj2 = new PosteringsDetaljBuilder().setHovedBeskrivelse("Skatt").createPosteringsDetalj();

        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(detalj1)).setMelding("bilag1").createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(asList(detalj2)).setMelding("bilag2").createBilag();


        Utbetaling utbetaling = new UtbetalingBuilder()
                .setPeriode("2010.06.23-2011.06.24")
                .setNettoBelop(6000.00)
                .setStatuskode("12")
                .setBeskrivelse("Pensjon")
                .setUtbetalingsDato(new DateTime().now().minusDays(200))
                .setBilag(asList(bilag1, bilag2)).createUtbetaling();
        return utbetaling;
    }

    private Utbetaling createUtbetaling7() {
        Utbetaling utbetaling = new UtbetalingBuilder()
                .setUtbetalingsDato(new DateTime().now().minusDays(300))
                .createUtbetaling();
        return utbetaling;
    }

}
