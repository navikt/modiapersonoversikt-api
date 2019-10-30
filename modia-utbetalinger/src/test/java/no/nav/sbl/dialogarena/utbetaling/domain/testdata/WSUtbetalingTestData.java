package no.nav.sbl.dialogarena.utbetaling.domain.testdata;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.joda.time.DateTime.now;


public class WSUtbetalingTestData {
    public static final int NUMBER_OF_DAYS_TO_SHOW = 30;
    public static final String BILLAGSNUMMER = "5";
    public static final String KREDITOR = "2";
    public static final String OSLO_KOMMUNE_AKTOER_ID = "1";
    public static final String AKTOER_ID = "5";
    public static final String KONTONUMMER = "1234";

    public static List<WSUtbetaling> getWsUtbetalinger(String fNr, DateTime startDato, DateTime sluttDato) {
        List<WSUtbetaling> utbetalinger = new ArrayList<>();
        utbetalinger.add(createOlaNordmannUtbetaling());
        utbetalinger.add(createOsloKommuneUtbetaling());
        utbetalinger.addAll(createKariNordmannUtbetaling());
        utbetalinger.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW).plusDays(14), now().minusDays(NUMBER_OF_DAYS_TO_SHOW).plusDays(14), null));
        utbetalinger.add(createUtbetalingUtenUtbetalingsdato(now(), now()));
        utbetalinger.add(createRefusjonSykepenger());

        final Interval periode = new Interval(startDato, sluttDato);
        Predicate<WSUtbetaling> innenPeriode = object -> periode.contains(object.getUtbetalingsdato());
        return utbetalinger.stream().filter(innenPeriode).collect(toList());
    }


    public static List<WSUtbetaling> createKariNordmannUtbetaling() {
        WSYtelsestyper ytelesestype = new WSYtelsestyper().withValue("Alderspensjon");
        WSPerson testWSPerson = new WSPerson().withAktoerId("33333333333").withNavn("Kari Nordmann Utbetaling 3");
        WSPeriode ytelsesperiode = new WSPeriode().withFom(now().minusYears(1).minusDays(NUMBER_OF_DAYS_TO_SHOW)).withTom(now().minusYears(1));
        List<WSYtelseskomponent> ytelseskomponenter = Arrays.asList(
                LagTestWSYtelse.lagYtelseskomponent("Grunnpermisjon", 5200.00),
                LagTestWSYtelse.lagYtelseskomponent("Særtillegg", 1456.00));

        return asList(new WSUtbetaling()
                        .withPosteringsdato(now().minusYears(1))
                        .withUtbetaltTil(testWSPerson)
                        .withUtbetalingsmelding("Alderspensjon med 3 mnd etterbetaling")
                        .withYtelseListe(
                                LagTestWSYtelse.lagWSYtelse(ytelesestype, testWSPerson, ytelsesperiode, ytelseskomponenter)
                                        .withSkattListe(new WSSkatt().withSkattebeloep(1500.00))
                                        .withTrekksum(-1500.00)
                                        .withYtelseNettobeloep(5156.00),
                                LagTestWSYtelse.lagWSYtelse(ytelesestype, testWSPerson, ytelsesperiode, ytelseskomponenter)
                                        .withTrekkListe(new ArrayList<>())
                                        .withSkattListe(new WSSkatt().withSkattebeloep(1500.00), new WSSkatt().withSkattebeloep(-733.00))
                                        .withTrekksum(767.00)
                                        .withYtelseNettobeloep(7423.00),
                                LagTestWSYtelse.lagWSYtelse(ytelesestype, testWSPerson, ytelsesperiode, ytelseskomponenter)
                                        .withTrekkListe(new ArrayList<>())
                                        .withSkattListe(new WSSkatt().withSkattebeloep(-1500.00))
                                        .withSkattsum(-1500.00)
                                        .withYtelseNettobeloep(5156.00),
                                LagTestWSYtelse.lagWSYtelse(ytelesestype, testWSPerson, ytelsesperiode, ytelseskomponenter)
                                        .withTrekkListe(LagTestWSYtelse.lagWSTrekk("Kreditorsjekk", -900.00, KREDITOR))
                                        .withTrekksum(-900.00)
                                        .withSkattListe(new WSSkatt().withSkattebeloep(-1500.00))
                                        .withSkattsum(-1500.00)
                                        .withYtelseNettobeloep(4256.00))
                        .withUtbetalingsdato(now().minusYears(1).plusMonths(2))
                        .withForfallsdato(now().minusYears(1).plusMonths(2))
                        .withUtbetaltTilKonto(new WSBankkonto().withKontonummer(KONTONUMMER).withKontotype("Konto - Utland"))
                        .withUtbetalingsmetode("Bankkonto")
                        .withUtbetalingsstatus("Utbetalt"),
                new WSUtbetaling()
                        .withPosteringsdato(now().minusYears(1).minusDays(2*NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(testWSPerson)
                        .withUtbetalingNettobeloep(4389.00)
                        .withUtbetalingsmelding("Alderspensjon for desember")
                        .withYtelseListe(
                                new WSYtelse()
                                        .withYtelsestype(new WSYtelsestyper().withValue("Alderspensjon"))
                                        .withRettighetshaver(testWSPerson)
                                        .withYtelsesperiode(new WSPeriode().withFom(now().minusYears(1).minusDays(3*NUMBER_OF_DAYS_TO_SHOW)).withTom(now().minusYears(1).minusMonths(2)))
                                        .withYtelseskomponentListe(
                                                LagTestWSYtelse.lagYtelseskomponent("Grunnpermisjon", 5200.00),
                                                LagTestWSYtelse.lagYtelseskomponent("Særtillegg", 1456.00))
                                        .withYtelseskomponentersum(6656.00)
                                        .withTrekkListe(new ArrayList<>())
                                        .withSkattListe(new WSSkatt().withSkattebeloep(-2267.00))
                                        .withSkattsum(-2267.00)
                                        .withYtelseNettobeloep(4389.00)
                                        .withBilagsnummer(BILLAGSNUMMER))
                        .withForfallsdato(now().minusYears(1).minusDays(2*NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTilKonto(new WSBankkonto().withKontonummer(WSUtbetalingTestData.KONTONUMMER).withKontotype("Konto - Utland"))
                        .withUtbetalingsmetode("Bankkonto")
                        .withUtbetalingsstatus("Under saksbehandling")
        );
    }

    private static WSUtbetaling createOsloKommuneUtbetaling() {
        return new WSUtbetaling()
                .withPosteringsdato(now().minusDays(19))
                .withUtbetaltTil(new WSOrganisasjon().withAktoerId(OSLO_KOMMUNE_AKTOER_ID).withNavn("Oslo kommune Utbetaling 4"))
                .withUtbetalingsmelding("Sykepenger")
                .withYtelseListe(
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Sykepenger"))
                                .withRettighetshaver(new WSPerson().withAktoerId(AKTOER_ID).withNavn("Per Pettersen Eksempel 4"))
                                .withRefundertForOrg(new WSOrganisasjon().withAktoerId(OSLO_KOMMUNE_AKTOER_ID).withNavn("Plan- og bygningsetaten"))
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW).minusDays(15)).withTom(now().minusDays(NUMBER_OF_DAYS_TO_SHOW).minusDays(15)))
                                .withYtelseskomponentListe(
                                        LagTestWSYtelse.lagYtelseskomponent("Sykepenger, arbeidstakere", 15000.00))
                                .withYtelseskomponentersum(15000.00)
                                .withTrekkListe(LagTestWSYtelse.lagWSTrekk("Kreditortrekk", 900.00, KREDITOR))
                                .withTrekksum(900.00)
                                .withSkattListe(new WSSkatt().withSkattebeloep(-2267.00))
                                .withSkattsum(-2267.00)
                                .withYtelseNettobeloep(13633.00)
                                .withBilagsnummer(BILLAGSNUMMER))
                .withUtbetalingsdato(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW))
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Konto - Norge").withKontonummer(KONTONUMMER))
                .withUtbetalingsmetode("Bankkonto")
                .withUtbetalingsstatus("Utbetalt");
    }

    private static WSUtbetaling createOlaNordmannUtbetaling() {
        WSPerson personOlaNordmann = new WSPerson().withAktoerId(AKTOER_ID).withNavn("Ola Nordmann Utbetaling 2");
        return new WSUtbetaling()
                .withPosteringsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                .withUtbetaltTil(personOlaNordmann)
                .withUtbetalingsmelding("Utbetalt dagpenger")
                .withYtelseListe(
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Dagpenger"))
                                .withRettighetshaver(personOlaNordmann)
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(3*NUMBER_OF_DAYS_TO_SHOW)).withTom(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW)))
                                .withYtelseskomponentListe(
                                        LagTestWSYtelse.lagYtelseskomponent("Dagpenger", 21419.75, 389.45, 55.0)
                                                .withSatstype("DAG"))
                                .withYtelseskomponentersum(21419.75)
                                .withSkattListe(new WSSkatt().withSkattebeloep(2267.00))
                                .withSkattsum(2267.00)
                                .withTrekkListe(new ArrayList<>())
                                .withTrekksum(0.00)
                                .withYtelseNettobeloep(21419.75)
                                .withBilagsnummer("30742-5731"))
                .withForfallsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW).plusDays(14))
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Utbetalingskort - Norge"))
                .withUtbetalingsmetode("Utbetalingskort")
                .withUtbetalingsstatus("Sendt kontofører, avventer forfallsdato");
    }

    public static WSUtbetaling createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(DateTime utbetalingsdato, DateTime forfallsdato, DateTime posteringsdato ) {
        WSPerson personOlaNordmann = new WSPerson().withAktoerId(AKTOER_ID).withNavn("Ola Nordmann Utbetaling 2");
        return new WSUtbetaling()
                .withPosteringsdato(posteringsdato)
                .withUtbetaltTil(personOlaNordmann)
                .withUtbetalingsmelding("Utbetalt dagpenger")
                .withYtelseListe(
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Dagpenger"))
                                .withRettighetshaver(personOlaNordmann)
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(3*NUMBER_OF_DAYS_TO_SHOW)).withTom(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW)))
                                .withYtelseskomponentListe(
                                        LagTestWSYtelse.lagYtelseskomponent("Dagpenger", 21419.75, 389.45, 55.0)
                                                .withSatstype("DAG"))
                                .withYtelseskomponentersum(21419.75)
                                .withSkattListe(new WSSkatt().withSkattebeloep(2267.00))
                                .withSkattsum(2267.00)
                                .withTrekkListe(new ArrayList<>())
                                .withTrekksum(0.00)
                                .withYtelseNettobeloep(21419.75)
                                .withBilagsnummer("30742-5731"),
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Arbeidsavklaringspenger"))
                                .withRettighetshaver(personOlaNordmann)
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(3*NUMBER_OF_DAYS_TO_SHOW)).withTom(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW)))
                                .withYtelseskomponentListe(
                                        LagTestWSYtelse.lagYtelseskomponent("Arbeidsavklaringspenger", 2000.00, 00.00, 00.0)
                                                .withSatstype("AAP"))
                                .withYtelseskomponentersum(2000.00)
                                .withSkattListe(new WSSkatt().withSkattebeloep(00.00))
                                .withSkattsum(00.00)
                                .withTrekkListe(new ArrayList<>())
                                .withTrekksum(0.00)
                                .withYtelseNettobeloep(2000.00)
                                .withBilagsnummer("30742-5731"))
                .withForfallsdato(forfallsdato)
                .withUtbetalingsdato(utbetalingsdato)
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Konto - Norge").withKontonummer(KONTONUMMER))
                .withUtbetalingsmetode("Bankkonto")
                .withUtbetalingsstatus("Utbetalt");
    }

    public static WSUtbetaling createRefusjonSykepenger() {
        WSPerson personOlaNordmann = new WSPerson().withAktoerId(AKTOER_ID).withNavn("Utbetaling uten utbetalingsdato");

        return new WSUtbetaling()
                .withPosteringsdato(now())
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Konto - Norge").withKontonummer(KONTONUMMER))
                .withUtbetalingsmetode("Bankkonto")
                .withUtbetalingsstatus("")
                .withUtbetaltTil(personOlaNordmann)
                .withUtbetalingsmelding("")
                .withYtelseListe(
                        new WSYtelse()
                        .withYtelsestype(new WSYtelsestyper().withValue("Sykepenger refusjon arbeidsgiver"))
                        .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(2)).withTom(now()))
                        .withYtelseskomponentListe(
                                new WSYtelseskomponent()
                                        .withYtelseskomponenttype("Oppgavepliktig")
                                        .withSatsbeloep(0.0)
                                        .withYtelseskomponentbeloep(6960.0))
                        .withYtelseskomponentListe(
                                new WSYtelseskomponent()
                                        .withYtelseskomponenttype("Oppgavepliktig")
                                        .withSatsbeloep(0.0)
                                        .withYtelseskomponentbeloep(6960.0)
                        )
                        .withYtelseskomponentersum(13920.0)
                        .withTrekksum(-0.0)
                        .withSkattListe(new WSSkatt().withSkattebeloep(-1398.0))
                        .withSkattListe(new WSSkatt().withSkattebeloep(-1398.0))
                        .withSkattsum(-2798.0)
                        .withYtelseNettobeloep(11124.0)
                        .withBilagsnummer(BILLAGSNUMMER),
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Dagpenger"))
                                .withRettighetshaver(personOlaNordmann)
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(3*NUMBER_OF_DAYS_TO_SHOW)).withTom(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW)))
                                .withYtelseskomponentListe(
                                        LagTestWSYtelse.lagYtelseskomponent("Dagpenger", 21419.75, 389.45, 55.0)
                                                .withSatstype("DAG"))
                                .withYtelseskomponentersum(21419.75)
                                .withSkattListe(new WSSkatt().withSkattebeloep(2267.00))
                                .withSkattsum(2267.00)
                                .withTrekkListe(new ArrayList<>())
                                .withTrekksum(0.00)
                                .withYtelseNettobeloep(21419.75)
                                .withBilagsnummer("30742-5731")
                );
    }

    public static WSUtbetaling createUtbetalingUtenUtbetalingsdato(DateTime forfallsdato, DateTime posteringsdato ) {
        WSPerson personOlaNordmann = new WSPerson().withAktoerId(AKTOER_ID).withNavn("Utbetaling uten utbetalingsdato");
        return new WSUtbetaling()
                .withUtbetalingsdato(null)
                .withForfallsdato(forfallsdato)
                .withPosteringsdato(posteringsdato)
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Konto - Norge").withKontonummer(KONTONUMMER))
                .withUtbetalingsmetode("Bankkonto")
                .withUtbetalingsstatus("")
                .withUtbetaltTil(personOlaNordmann)
                .withUtbetalingsmelding("")
                .withYtelseListe(
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Dagpenger"))
                                .withRettighetshaver(personOlaNordmann)
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(3*NUMBER_OF_DAYS_TO_SHOW)).withTom(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW)))
                                .withYtelseskomponentListe(
                                        LagTestWSYtelse.lagYtelseskomponent("Dagpenger", 2222.22, 389.45, 55.0)
                                                .withSatstype("DAG"))
                                .withYtelseskomponentersum(2222.22)
                                .withSkattListe(new WSSkatt().withSkattebeloep(2267.00))
                                .withSkattsum(2267.00)
                                .withTrekkListe(new ArrayList<>())
                                .withTrekksum(0.00)
                                .withYtelseNettobeloep(2222.22)
                                .withBilagsnummer("30742-57312"),
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Arbeidsavklaringspenger"))
                                .withRettighetshaver(personOlaNordmann)
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(3*NUMBER_OF_DAYS_TO_SHOW)).withTom(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW)))
                                .withYtelseskomponentListe(
                                        LagTestWSYtelse.lagYtelseskomponent("Arbeidsavklaringspenger", 2000.00, 00.00, 00.0)
                                                .withSatstype("AAP"))
                                .withYtelseskomponentersum(2000.00)
                                .withSkattListe(new WSSkatt().withSkattebeloep(00.00))
                                .withSkattsum(00.00)
                                .withTrekkListe(new ArrayList<>())
                                .withTrekksum(0.00)
                                .withYtelseNettobeloep(2000.00)
                                .withBilagsnummer("30742-57312"));

    }

}
