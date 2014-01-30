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
    private static final String KONTO_NR2 = "1234 56 78900";
    private static final String NAVN = "Ola Nordmann";
    private static final String UTBETALT = "UTBETALT";
    private static final String MOTTATT_KONTOFORER = "MOTTATT KONTOFØRER";
    private static final String STATUS_KODE = "0018";
    private static final Double BELOP = 1000.0;
    private static final Double SKATTE_PROSENT = -0.35;
    private static final String GRUNNBELOP = "Grunnbeløp";
    private static final String FORSKUDDSTREKK_SKATT = "Forskuddstrekk skatt";
    private static final String FORSKUDDSTREKK = "Forskuddstrekk";
    private static final String SKATT = "Skattetrekk";
    private static final String DAGPENGER = "Dagpenger";
    private static final String ANDRE = "Andre trekk";
    private static final String AAP = "AAP - grunnsats";
    private static final String KONTANT = "KONTANTSTØTTE";
    private static final String ORDINAER = "ORDINÆR STØNAD";
    private static final String FORELDREPENGER = "Foreldrepenger";
    private static final String VALUTA = "NOK";
    private static final String PERSON_KONTONR = "321321321321";
    private static final String ANNEN_MOTTAKER_KONTONR = "123123123123";
    private static String fnr;

    public static List<WSUtbetaling> getWsUtbetalinger(String fNr, DateTime startDato, DateTime sluttDato) {
        fnr = fNr;
        List<WSUtbetaling> utbetalinger = new ArrayList<>();
        utbetalinger.addAll(asList(
                createUtbetaling1(),
                createUtbetaling2(),
                createUtbetaling3(),
                createUtbetaling4()
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
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(AAP, KONTO_NR, AAP, 10, 1406.0, 14060.00).withSpesifikasjon("Bar");
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj(ANDRE, KONTO_NR, ANDRE, 10, 47993.0, -479.93).withSpesifikasjon("Foo");
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj(SKATT, KONTO_NR, SKATT, 10, 1.0, -3685.00);
        WSBilag bilag1 = createBilag("", AAP, posteringsdetalj1, posteringsdetalj2, posteringsdetalj3);

        return new WSUtbetaling()
                .withNettobelop(9895.07)
                .withGironr(KONTO_NR)
                .withBruttobelop(14060.00)
                .withTrekk(-4164.93)
                .withStatusBeskrivelse(MOTTATT_KONTOFORER)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(new DateTime(2013, 12, 30, 0, 0))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2013, 12, 16, 0, 0), new DateTime(2013, 12, 29, 0, 0)))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling2() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(ORDINAER, KONTO_NR2, ORDINAER, 1, 1.0, 3303.00).withSpesifikasjon("Bar");
        WSBilag bilag1 = createBilag("", KONTANT, posteringsdetalj1);

        return new WSUtbetaling()
                .withNettobelop(3303.00)
                .withGironr(KONTO_NR2)
                .withBruttobelop(3303.00)
                .withTrekk(0.00)
                .withStatusBeskrivelse(MOTTATT_KONTOFORER)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(new DateTime(2013, 12, 11, 0, 0))
                .withUtbetalingsPeriode(createPeriode(new DateTime(1970, 1, 1, 0, 0), new DateTime(1970, 1, 1, 0, 0)))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling3() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(ORDINAER, KONTO_NR2, ORDINAER, 1, 1.0, 3303.00).withSpesifikasjon("Bar");
        WSBilag bilag1 = createBilag("", KONTANT, posteringsdetalj1);

        return new WSUtbetaling()
                .withNettobelop(3303.00)
                .withGironr(KONTO_NR2)
                .withBruttobelop(3303.00)
                .withTrekk(0.00)
                .withStatusBeskrivelse(MOTTATT_KONTOFORER)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(new DateTime(2013, 11, 21, 0, 0))
                .withUtbetalingsPeriode(createPeriode(new DateTime(1970, 1, 1, 0, 0), new DateTime(1970, 1, 1, 0, 0)))
                .withBilagListe(bilag1);
    }

    public static WSUtbetaling createUtbetaling4() {
        WSPosteringsdetaljer posteringsdetalj1 = createPosteringsDetalj(AAP, KONTO_NR, AAP, 10, 1406.0, 14060.00).withSpesifikasjon("Bar");
        WSPosteringsdetaljer posteringsdetalj2 = createPosteringsDetalj(ANDRE, KONTO_NR, ANDRE, 10, 47993.0, -479.93).withSpesifikasjon("Foo");
        WSPosteringsdetaljer posteringsdetalj3 = createPosteringsDetalj(SKATT, KONTO_NR, SKATT, 10, 1.0, -3685.00);
        WSBilag bilag1 = createBilag("", AAP, posteringsdetalj1, posteringsdetalj2, posteringsdetalj3);

        return new WSUtbetaling()
                .withNettobelop(9895.07)
                .withGironr(KONTO_NR)
                .withBruttobelop(14060.00)
                .withTrekk(-4164.93)
                .withStatusBeskrivelse(MOTTATT_KONTOFORER)
                .withStatusKode(STATUS_KODE)
                .withUtbetalingMottaker(createTrygdetMottaker())
                .withUtbetalingDato(new DateTime(2013, 11, 18, 0, 0))
                .withUtbetalingsPeriode(createPeriode(new DateTime(2013, 11, 4, 0, 0), new DateTime(2013, 11, 17, 0, 0)))
                .withBilagListe(bilag1);
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

    public static WSBilag createBilag(String melding, String ytelse, WSPosteringsdetaljer... posteringsdetaljer) {
        return new WSBilag()
                .withMeldingListe(new WSMelding().withMeldingtekst(melding))
                .withPosteringsdetaljerListe(posteringsdetaljer)
                .withYtelseBeskrivelse(ytelse)
                .withBilagPeriode(new WSPeriode().withPeriodeFomDato(now().minusDays(7)).withPeriodeTomDato(now().minusDays(1)));

    }

    private static WSMottaker createTrygdetMottaker() {
        return new WSMottaker()
                .withMottakerId(fnr)
                .withMottakertypeKode("")
                .withNavn(NAVN);
    }

    private static WSMottaker createAnnenMottaker() {
        return new WSMottaker()
                .withMottakerId("51321")
                .withMottakertypeKode("")
                .withNavn("RIMI");
    }

}
