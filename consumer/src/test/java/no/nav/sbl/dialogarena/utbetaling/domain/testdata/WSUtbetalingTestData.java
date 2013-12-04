package no.nav.sbl.dialogarena.utbetaling.domain.testdata;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPeriode;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.joda.time.DateTime.now;


public class WSUtbetalingTestData {

    private static final String KONTO_NR = "***REMOVED***";
    private static final String NAVN = "Kjell Olsen";
    public static final String UTBETALT = "MOTTATT_KONTOFØRER";
    public static final String MOTTATT_KONTOFØRER = "MOTTATT KONTOFØRER";
    private static final String STATUS_KODE = "0018";
    private static String fnr;

    public static List<WSUtbetaling> getWsUtbetalinger(String fNr) {
        fnr = fNr;
        List<WSUtbetaling> utbetalinger = new ArrayList<>();
        utbetalinger.add(createUtbetaling1());
        utbetalinger.add(createUtbetaling2());
        utbetalinger.add(createUtbetaling3());
        utbetalinger.add(createUtbetaling4());
        utbetalinger.add(createUtbetaling5());
        utbetalinger.add(createUtbetaling6());
        utbetalinger.add(createUtbetaling7());
        utbetalinger.add(createUtbetaling8());
        return utbetalinger;
    }

    public static WSUtbetaling createUtbetaling1() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Alderspensjon", KONTO_NR, "Hovedytelse", 1, 1.0);
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj("Skatt", KONTO_NR, "", 1, 1.0);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj2);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(1000.0)
                .withBruttobelop(1200.0)
                .withTrekk(200.0)
                .withStatusBeskrivelse(MOTTATT_KONTOFØRER)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(now().minusDays(4))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 1, 23, 0, 0), new DateTime(2011, 1, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling2() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Uføre", KONTO_NR, "Tilleggsytelse", 1, 1.0);
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj("Foreldrepenger", KONTO_NR, "", 1, 1.0);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", KONTO_NR, "Forskuddstrekk", 1, 1.0);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj2, posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(1700.0)
                .withBruttobelop(2000.0)
                .withTrekk(300.0)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(now().minusDays(7))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 2, 23, 0, 0), new DateTime(2011, 2, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling3() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Barnepenger", KONTO_NR, "", 1, 1.0);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", KONTO_NR, "Forskuddstrekk", 1, 1.0);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(2500.0)
                .withBruttobelop(3000.0)
                .withTrekk(500.0)
                .withValuta("kr")
                .withStatusBeskrivelse(UTBETALT)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withStatusKode(STATUS_KODE)
                .withUtbetalingDato(now().minusDays(10))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 3, 23, 0, 0), new DateTime(2011, 3, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling4() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Dagpenger", KONTO_NR, "Tilleggsytelse", 13, 1.45);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj1);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(3000.00)
                .withBruttobelop(4000.0)
                .withTrekk(1000.0)
                .withValuta("kr")
                .withStatusBeskrivelse(UTBETALT)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withStatusKode(STATUS_KODE)
                .withUtbetalingDato(now().minusDays(40))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 4, 23, 0, 0), new DateTime(2011, 4, 24, 0, 0)));
        utbetaling.withBilagListe(bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling5() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("APGrunnbeløp", KONTO_NR, "Hoved", 1, 1.0);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", KONTO_NR, "Forskuddstrekk", 1, 1.0);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(2500.50)
                .withBruttobelop(5100.50)
                .withTrekk(2600.0)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusDays(84))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 5, 23, 0, 0), new DateTime(2011, 5, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling6() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Pensjon", KONTO_NR, "Løpende", 1, 1.0);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", KONTO_NR, "", 1, 1.0);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(4000.00)
                .withBruttobelop(6000.0)
                .withTrekk(2000.0)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusMonths(5))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 6, 23, 0, 0), new DateTime(2011, 6, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }
    public static WSUtbetaling createUtbetaling7() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Pensjon", KONTO_NR, "Løpende", 1, 1.0);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", KONTO_NR, "Forskuddstrekk", 1, 1.0);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(4000.00)
                .withBruttobelop(6000.0)
                .withTrekk(2000.0)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusMonths(5))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 6, 23, 0, 0), new DateTime(2011, 6, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }
    public static WSUtbetaling createUtbetaling8() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Pensjon", KONTO_NR, "Løpende", 1, 1.0);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj("Skatt", KONTO_NR, "", 1, 1.0);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj3);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(4000.00)
                .withBruttobelop(6000.0)
                .withTrekk(2000.0)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusMonths(5))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2010, 6, 23, 0, 0), new DateTime(2011, 6, 24, 0, 0)));
        utbetaling.withBilagListe(bilag1, bilag2);
        return utbetaling;
    }

    public static WSPeriode createPeriode(DateTime fomDate, DateTime tomDate) {
        return new WSPeriode().withPeriodeFomDato(fomDate).withPeriodeTomDato(tomDate);
    }

    public static WSPosteringsdetaljer createPosteringsDetalj(String hovedBeskrivelse, String kontoNr, String underbeskrivelse, Integer antall, Double sats) {
        return new WSPosteringsdetaljer()
                        .withKontoBeskrHoved(hovedBeskrivelse)
                        .withKontoBeskrUnder(underbeskrivelse)
                        .withAntall(antall)
                        .withSats(sats)
                        .withKontonr(kontoNr);
    }

    public static WSBilag createBilag(String melding, WSPosteringsdetaljer... posteringsdetaljer) {
        return new WSBilag().withMeldingListe(new WSMelding().withMeldingtekst(melding)).withPosteringsdetaljerListe(posteringsdetaljer);
    }

    private static WSMottaker createTrygdetMottaker() {
        WSMottaker wsMottaker = new WSMottaker();
        wsMottaker.withMottakerId(fnr)
                .withMottakertypeKode("")
                .withNavn(NAVN);
        return wsMottaker;
    }

    private static WSMottaker createArbeidsgiverMottaker() {
        WSMottaker wsMottaker = new WSMottaker();
        wsMottaker.withMottakerId("51321")
                .withMottakertypeKode("")
                .withNavn("Arbeidsgiver");
        return wsMottaker;
    }


}
