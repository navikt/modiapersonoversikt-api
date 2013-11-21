package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPeriode;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.joda.time.DateTime.now;


public class WSUtbetalingTestData {

    private static String kontoNr = "***REMOVED***";

    public static List<WSUtbetaling> getWsUtbetalinger() {
        List<WSUtbetaling> utbetalinger = new ArrayList<>();
        utbetalinger.add(createUtbetaling1());
        utbetalinger.add(createUtbetaling2());
        utbetalinger.add(createUtbetaling3());
        utbetalinger.add(createUtbetaling4());
        utbetalinger.add(createUtbetaling5());
        utbetalinger.add(createUtbetaling6());
        return utbetalinger;
    }

    public static WSUtbetaling createUtbetaling1() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Alderspensjon",kontoNr);
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj("Skatt", kontoNr);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj2);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(1000.0)
                .withBruttobelop(1000.0)
                .withStatusKode("12")
                .withStatusBeskrivelse("Uføre")
                .withUtbetalingDato(now().minusDays(4))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 1, 23, 0, 0), new DateTime(2011, 1, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling2() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Uføre", kontoNr);
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj("Foreldrepenger", kontoNr);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", kontoNr);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj2, posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(2000.0)
                .withBruttobelop(2000.0)
                .withStatusKode("12")
                .withStatusBeskrivelse("Trygd")
                .withUtbetalingDato(now().minusDays(7))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 2, 23, 0, 0), new DateTime(2011, 2, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling3() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Barnepenger", kontoNr);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", kontoNr);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(3000.0)
                .withBruttobelop(3000.0)
                .withValuta("kr")
                .withStatusKode("12")
                .withStatusBeskrivelse("Barnepenger")
                .withUtbetalingDato(now().minusDays(10))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 3, 23, 0, 0), new DateTime(2011, 3, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling4() {
        //String kontoNr = kontoNr;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Trygd", kontoNr);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj1);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(4000.00)
                .withBruttobelop(4000.0)
                .withValuta("kr")
                .withStatusKode("12")
                .withStatusBeskrivelse("Trygd")
                .withUtbetalingDato(now().minusDays(40))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 4, 23, 0, 0), new DateTime(2011, 4, 24, 0, 0)));
        utbetaling.withBilagListe(bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling5() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("APGrunnbeløp", kontoNr);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", kontoNr);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(5100.50)
                .withBruttobelop(5100.50)
                .withStatusKode("12")
                .withStatusBeskrivelse("APGrunnbeløp")
                .withUtbetalingDato(now().minusDays(84))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 5, 23, 0, 0), new DateTime(2011, 5, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling6() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Pensjon", kontoNr);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", kontoNr);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(6000.00)
                .withBruttobelop(6000.0)
                .withStatusKode("12")
                .withStatusBeskrivelse("Pensjon")
                .withUtbetalingDato(now().minusDays(200))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 6, 23, 0, 0), new DateTime(2011, 6, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSPeriode createPeriode(DateTime fomDate, DateTime tomDate) {
        return new WSPeriode().withPeriodeFomDato(fomDate).withPeriodeTomDato(tomDate);
    }

    public static WSPosteringsdetaljer createPosteringsDetalj(String hovedBeskrivelse, String kontoNr) {
        return new WSPosteringsdetaljer().withKontoBeskrHoved(hovedBeskrivelse).withKontonr(kontoNr);
    }

    public static WSBilag createBilag(String melding, WSPosteringsdetaljer... posteringsdetaljer) {
        return new WSBilag().withMeldingListe(new WSMelding().withMeldingtekst(melding)).withPosteringsdetaljerListe(posteringsdetaljer);
    }


}
