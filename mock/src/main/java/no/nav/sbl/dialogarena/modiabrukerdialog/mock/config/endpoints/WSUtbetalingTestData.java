package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPeriode;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static org.joda.time.DateTime.now;


public class WSUtbetalingTestData {

    private static final String KONTO_NR = "12345678900";
    private static final String NAVN = "Kjell Olsen";
    private static final String UTBETALT = "UTBETALT";
    private static final String MOTTATT_KONTOFORER = "MOTTATT KONTOFØRER";
    private static final String STATUS_KODE = "0018";
    private static final Double BELOP = 1000.0;
    private static final Double SKATTE_PROSENT = -0.35;
    private static final String GRUNNBELOP = "Grunnbeløp";
    private static final String FORSKUDDSTREKK_SKATT = "Forskuddstrekk skatt";
    private static final String FORSKUDDSTREKK = "Forskuddstrekk";
    private static final String SKATT = "Skatt";
    private static final String DAGPENGER = "Dagpenger";
    private static final String FORELDREPENGER = "Foreldrepenger";
    private static final String VALUTA = "NOK";
    private static final String PERSON_KONTONR = "22334455112";
    private static final String ARBEIDSGIVER_KONTONR = "99887766552";
    private static String fnr;

    public static List<WSUtbetaling> getWsUtbetalinger(String fNr, DateTime startDato, DateTime sluttDato) {
        fnr = fNr;
        List<WSUtbetaling> utbetalinger = new ArrayList<>();
        utbetalinger.addAll(asList(
                createUtbetaling4(),
                createUtbetaling2(),
                createUtbetaling5(),
                createUtbetaling3(),
                createUtbetaling7(),
                createUtbetaling6(),
                createUtbetaling9(),
                createUtbetaling8(),
                createUtbetaling1(),
                createUtbetaling10(),
                createUtbetaling11()
        ));

        final Interval periode = new Interval(startDato, sluttDato);
        Predicate<WSUtbetaling> innenPeriode = new Predicate<WSUtbetaling>() {
            public boolean evaluate(WSUtbetaling object) {
                return periode.contains(object.getUtbetalingDato());
            }
        };
        return on(utbetalinger).filter(innenPeriode).collect();
    }

    public static WSUtbetaling createUtbetaling1() {
        double trekk = BELOP * SKATTE_PROSENT;
        Double belop = BELOP;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(DAGPENGER, KONTO_NR, GRUNNBELOP, 1, 1.0, belop).withSpesifikasjon("Du har 3 uker igjen av Dagpenger");
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj(SKATT, KONTO_NR, FORSKUDDSTREKK_SKATT, 1, 1.0, trekk);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1, posteringsdetalj2).withMeldingListe(new WSMelding().withMeldingtekst("36% skatt"));
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj1, posteringsdetalj2);

        return new WSUtbetaling()
                .withNettobelop(2 * (belop + trekk))
                .withGironr(PERSON_KONTONR)
                .withBruttobelop(2 * belop)
                .withTrekk(2 * trekk)
                .withStatusBeskrivelse(MOTTATT_KONTOFORER)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(now().minusDays(4))
                .withUtbetalingsPeriode(createPeriode(now().minusDays(10).toDateTime(), now().minusDays(4).toDateTime()))
                .withBilagListe(bilag1, bilag2);
    }

    public static WSUtbetaling createUtbetaling2() {
        double belop0 = BELOP * 1.5;
        double trekk = SKATTE_PROSENT * BELOP * 2;
        Double belop1 = BELOP;
        Double belop4 = -BELOP;
        Double belop2 = BELOP;
        WSPosteringsdetaljer posteringsdetalj0 = createPosteringsDetalj("Uføre", KONTO_NR, "Tilleggsytelse", 0, 0.0, belop0);
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Uføre", KONTO_NR, "Tilleggsytelse", 0, 0.0, belop1);
        WSPosteringsdetaljer posteringsdetalj4 = createPosteringsDetalj("Uføre", KONTO_NR, "Tilleggsytelse tilbakebetalt", 0, 0.0, belop4);
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj("Foreldrepenger", KONTO_NR, "", 0, 0.0, belop2);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj0, posteringsdetalj1, posteringsdetalj2, posteringsdetalj4);

        double brutto = belop0 + belop1 + belop2 + belop4;
        return new WSUtbetaling()
                .withNettobelop(brutto + trekk)
                .withGironr(ARBEIDSGIVER_KONTONR)
                .withBruttobelop(brutto)
                .withTrekk(trekk)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusDays(7))
                .withUtbetalingsPeriode(createPeriode(now().minusDays(10).toDateTime(), now().minusDays(4).toDateTime()))
                .withBilagListe(bilag2);
    }

    public static WSUtbetaling createUtbetaling3() {
        double trekk = SKATTE_PROSENT * BELOP;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(FORELDREPENGER, KONTO_NR, "", 0, 0.0, BELOP);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);

        return new WSUtbetaling()
                .withNettobelop(BELOP + trekk)
                .withGironr("44442255555")
                .withBruttobelop(BELOP)
                .withTrekk(trekk)
                .withValuta(VALUTA)
                .withStatusBeskrivelse(UTBETALT)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withStatusKode(STATUS_KODE)
                .withUtbetalingDato(now().minusDays(10))
                .withUtbetalingsPeriode(createPeriode(now().minusDays(10).toDateTime(), now().minusDays(4).toDateTime()))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling4() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(DAGPENGER, KONTO_NR, "Tilleggsytelse", 13, 1.45, BELOP);
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj(DAGPENGER, KONTO_NR, "Feilretting", 13, 1.45, -BELOP);
        WSBilag bilag2 = createBilag("bilag2", posteringsdetalj1, posteringsdetalj2);

        return new WSUtbetaling()
                .withNettobelop(0.0)
                .withGironr("44442255555")
                .withBruttobelop(0.0)
                .withTrekk(0.0)
                .withValuta(VALUTA)
                .withStatusBeskrivelse(UTBETALT)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withStatusKode(STATUS_KODE)
                .withUtbetalingDato(now().minusDays(40))
                .withUtbetalingsPeriode(createPeriode(now().minusDays(60).toDateTime(), now().minusDays(45).toDateTime()))
                .withBilagListe(bilag2);
    }

    public static WSUtbetaling createUtbetaling5() {
        double trekk = SKATTE_PROSENT * BELOP;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(DAGPENGER, KONTO_NR, GRUNNBELOP, 1, 1.0, BELOP);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);

        return new WSUtbetaling()
                .withNettobelop(BELOP + trekk)
                .withGironr("44442255555")
                .withBruttobelop(BELOP)
                .withTrekk(trekk)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusDays(84))
                .withUtbetalingsPeriode(createPeriode(now().minusDays(100).toDateTime(), now().minusDays(94).toDateTime()))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling6() {
        double trekk = SKATTE_PROSENT * BELOP;
        Double belop = BELOP * 3;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(DAGPENGER, KONTO_NR, "Løpende", 1, 1.0, belop);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj(SKATT, KONTO_NR, FORSKUDDSTREKK_SKATT, 0, 0.0, trekk);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1, posteringsdetalj3);

        return new WSUtbetaling()
                .withNettobelop(belop + trekk)
                .withBruttobelop(belop)
                .withGironr("44442255555")
                .withTrekk(trekk)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(now().minusMonths(5))
                .withUtbetalingsPeriode(createPeriode(now().minusMonths(7).toDateTime(), now().minusMonths(6).toDateTime()))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling7() {
        double trekk = SKATTE_PROSENT * BELOP;
        Double belop = BELOP;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(DAGPENGER, KONTO_NR, "Løpende", 1, 1.0, belop);
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj(SKATT, KONTO_NR, FORSKUDDSTREKK, 0, 0.0, trekk);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1, posteringsdetalj3);

        return new WSUtbetaling()
                .withNettobelop(belop + trekk)
                .withBruttobelop(belop)
                .withGironr("44442255555")
                .withTrekk(trekk)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(now().minusMonths(5))
                .withUtbetalingsPeriode(createPeriode(now().minusMonths(7).toDateTime(), now().minusMonths(6).toDateTime()))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling8() {
        double utbetalt = BELOP * 0.7;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Høreapparater", KONTO_NR, "Høreapparat", 0, 0.0, utbetalt);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);

        return new WSUtbetaling()
                .withNettobelop(utbetalt)
                .withTrekk(0.0)
                .withGironr("44442255555")
                .withBruttobelop(utbetalt)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusMonths(5))
                .withUtbetalingsPeriode(createPeriode(now().minusMonths(7).toDateTime(), now().minusMonths(6).toDateTime()))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling9() {
        double utbetalt = BELOP * 0.7;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Høreapparater", KONTO_NR, "Høreapparat", 0, 0.0, utbetalt);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);

        return new WSUtbetaling()
                .withNettobelop(utbetalt)
                .withTrekk(0.0)
                .withGironr("44442255555")
                .withBruttobelop(utbetalt)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusMonths(24))
                .withUtbetalingsPeriode(createPeriode(now().minusMonths(7).toDateTime(), now().minusMonths(6).toDateTime()))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling10() {
        double utbetalt = BELOP * 0.87;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Høreapparater", KONTO_NR, "Høreapparat", 1, 1.0, utbetalt);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(utbetalt)
                .withTrekk(0.0)
                .withGironr("44442255555")
                .withBruttobelop(utbetalt)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(now())
                .withUtbetalingsPeriode(createPeriode(now().minusMonths(7).toDateTime(), now().minusMonths(6).toDateTime()));
        utbetaling.withBilagListe(bilag1);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling11() {
        double utbetalt = BELOP * 0.45;
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj("Høreapparater", KONTO_NR, "Høreapparat", 2, 1.0, utbetalt);
        WSBilag bilag1 = createBilag("bilag1", posteringsdetalj1);

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling.withNettobelop(utbetalt)
                .withTrekk(0.0)
                .withGironr("44442255555")
                .withBruttobelop(utbetalt)
                .withStatusBeskrivelse(UTBETALT)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createArbeidsgiverMottaker())
                .withUtbetalingDato(now().minusDays(1))
                .withUtbetalingsPeriode(createPeriode(now().minusMonths(8).toDateTime(), now().minusMonths(6).toDateTime()));
        utbetaling.withBilagListe(bilag1);
        return utbetaling;
    }


    public static WSPeriode createPeriode(DateTime fomDate, DateTime tomDate) {
        return new WSPeriode().withPeriodeFomDato(fomDate).withPeriodeTomDato(tomDate);
    }

    public static WSPosteringsdetaljer createPosteringsDetalj(String hovedBeskrivelse, String kontoNr, String underbeskrivelse, Integer antall, Double sats, Double belop) {
        return new WSPosteringsdetaljer()
                .withKontoBeskrHoved(hovedBeskrivelse)
                .withKontoBeskrUnder(underbeskrivelse)
                .withAntall(antall)
                .withSats(sats)
                .withKontonr(kontoNr)
                .withBelop(belop);
    }

    public static WSBilag createBilag(String melding, WSPosteringsdetaljer... posteringsdetaljer) {
        return new WSBilag()
                .withMeldingListe(new WSMelding().withMeldingtekst(melding))
                .withPosteringsdetaljerListe(posteringsdetaljer)
                .withBilagPeriode(new WSPeriode().withPeriodeFomDato(now().minusDays(7)).withPeriodeTomDato(now().minusDays(1)));
    }

    private static WSMottaker createTrygdetMottaker() {
        return new WSMottaker()
                .withMottakerId(fnr)
                .withMottakertypeKode("")
                .withNavn(NAVN);
    }

    private static WSMottaker createArbeidsgiverMottaker() {
        return new WSMottaker()
                .withMottakerId("51321")
                .withMottakertypeKode("")
                .withNavn("RIMI");
    }

}
