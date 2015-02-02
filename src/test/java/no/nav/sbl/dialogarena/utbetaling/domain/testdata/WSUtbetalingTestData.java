package no.nav.sbl.dialogarena.utbetaling.domain.testdata;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;


public class WSUtbetalingTestData {

    public static final String KONTO_NR = "***REMOVED***";
    public static final String NAVN = "Kjell Olsen";
    public static final String UTBETALT = "UTBETALT";
    public static final String MOTTATT_KONTOFORER = "MOTTATT KONTOFØRER";
    public static final String STATUS_KODE = "0018";
    public static final Double BELOP = 1000.0;
    public static final Double SKATTE_PROSENT = -0.35;
    public static final String GRUNNBELOP = "Grunnbeløp";
    public static final String FORSKUDDSTREKK_SKATT = "Forskuddstrekk skatt";
    public static final String FORSKUDDSTREKK = "Forskuddstrekk";
    public static final String SKATT = "Skatt";
    public static final String DAGPENGER = "Dagpenger";
    public static final String FORELDREPENGER = "Foreldrepenger";
    public static final String VALUTA = "NOK";
    public static final DateTime forsteDesember = new DateTime(2013, 12, 1, 12, 0);
    public static final String SPESIFIKASJON = "";
    public static final String SPESIFIKASJON_1 = "Ekstra detaljinfo";
    public static final String UFORE = "UFØRE";
    public static final String TILLEGGSYTELSE = "TILLEGGSYTELSE";
    public static final String TILLEGGSYTELSE_TILBAKEBETALT = "Tilleggsytelse tilbakebetalt";
    public static final String YTELSE = "YTELSE";
    private static String fnr;

    public static List<WSUtbetaling> getWsUtbetalinger(String fNr, DateTime startDato, DateTime sluttDato) {
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
        utbetalinger.add(createUtbetaling9());
        utbetalinger.add(createUtbetaling10());
        utbetalinger.add(createUtbetaling11());

        final Interval periode = new Interval(startDato, sluttDato);
        Predicate<WSUtbetaling> innenPeriode = new Predicate<WSUtbetaling>() {
        	public boolean evaluate(WSUtbetaling object) {
        		return periode.contains(object.getUtbetalingsdato());
        	}
        };
        return on(utbetalinger).filter(innenPeriode).collect();
    }

    public static WSUtbetaling createUtbetaling1() {
        double trekk = BELOP * SKATTE_PROSENT;
        Double belop = BELOP;

        WSUtbetaling utbetaling = new WSUtbetaling();

        utbetaling
                .withUtbetalingNettobeloep((2 * belop + trekk))
                .withUtbetalingsmelding("Dette er bilagsmelding1, Dette er bilagsmelding 2")
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling2() {
        double belop0 = BELOP * 1.5;
        double trekk = SKATTE_PROSENT * BELOP * 2;

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep((2 * belop0 + trekk))
                .withUtbetalingsmelding("bilag2")
                .withUtbetalingsdato(forsteDesember);

        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling3() {
        double trekk = SKATTE_PROSENT * BELOP;

        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep((2 * BELOP + trekk))
                .withUtbetalingsmelding("bilag1")
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling4() {
        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep(BELOP)
                .withUtbetalingsmelding("bilag2")
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling5() {
        double trekk = SKATTE_PROSENT * BELOP;
        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep(trekk)
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling6() {
        double trekk = SKATTE_PROSENT * BELOP;
        Double belop = BELOP * 3;
        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep((2 * belop + trekk))
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }
    public static WSUtbetaling createUtbetaling7() {
        double trekk = SKATTE_PROSENT * BELOP;
        Double belop = BELOP;
        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep((2 * belop + trekk))
                .withUtbetalingsmelding("bilag22")
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }
    public static WSUtbetaling createUtbetaling8() {
        double utbetalt = BELOP * 0.7;
        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep(utbetalt)
                .withUtbetalingsmelding("bilag2")
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling9() {
        double utbetalt = BELOP * 0.7;
        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep(utbetalt)
                .withUtbetalingsmelding("bilag1")
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling10() {
        double utbetalt = BELOP * 0.87;
        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep(utbetalt)
                .withUtbetalingsmelding("bilag0")
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }

    public static WSUtbetaling createUtbetaling11() {
        double utbetalt = BELOP * 0.45;
        WSUtbetaling utbetaling = new WSUtbetaling();
        utbetaling
                .withUtbetalingNettobeloep(utbetalt)
                .withUtbetalingsmelding("bilag1")
                .withUtbetalingsdato(forsteDesember);
        return utbetaling;
    }
}
