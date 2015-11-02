package no.nav.sbl.dialogarena.utbetaling.domain.testdata;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget.NUMBER_OF_DAYS_TO_SHOW;
import static org.joda.time.DateTime.now;


public class WSUtbetalingTestData {

    public static List<WSUtbetaling> getWsUtbetalinger(String fNr, DateTime startDato, DateTime sluttDato) {
        List<WSUtbetaling> utbetalinger = new ArrayList<>();
        utbetalinger.add(createOlaNordmannUtbetaling());
        utbetalinger.add(createOsloKommuneUtbetaling());
        utbetalinger.addAll(createKariNordmannUtbetaling());
        utbetalinger.add(createUtbetalingMedValgtUtbetalingOgPosteringsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW).plusDays(14), now().minusDays(NUMBER_OF_DAYS_TO_SHOW).plusDays(14)));

        final Interval periode = new Interval(startDato, sluttDato);
        Predicate<WSUtbetaling> innenPeriode = new Predicate<WSUtbetaling>() {
            public boolean evaluate(WSUtbetaling object) {
                return periode.contains(object.getUtbetalingsdato());
            }
        };
        return on(utbetalinger).filter(innenPeriode).collect();
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
                                        .withTrekkListe(new ArrayList<WSTrekk>())
                                        .withSkattListe(new WSSkatt().withSkattebeloep(1500.00), new WSSkatt().withSkattebeloep(-733.00))
                                        .withTrekksum(767.00)
                                        .withYtelseNettobeloep(7423.00),
                                LagTestWSYtelse.lagWSYtelse(ytelesestype, testWSPerson, ytelsesperiode, ytelseskomponenter)
                                        .withTrekkListe(new ArrayList<WSTrekk>())
                                        .withSkattListe(new WSSkatt().withSkattebeloep(-1500.00))
                                        .withSkattsum(-1500.00)
                                        .withYtelseNettobeloep(5156.00),
                                LagTestWSYtelse.lagWSYtelse(ytelesestype, testWSPerson, ytelsesperiode, ytelseskomponenter)
                                        .withTrekkListe(LagTestWSYtelse.lagWSTrekk("Kreditorsjekk", -900.00, "***REMOVED***"))
                                        .withTrekksum(-900.00)
                                        .withSkattListe(new WSSkatt().withSkattebeloep(-1500.00))
                                        .withSkattsum(-1500.00)
                                        .withYtelseNettobeloep(4256.00))
                        .withUtbetalingsdato(now().minusYears(1).plusMonths(2))
                        .withForfallsdato(now().minusYears(1).plusMonths(2))
                        .withUtbetaltTilKonto(new WSBankkonto().withKontonummer("***REMOVED******REMOVED***896").withKontotype("Konto - Utland"))
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
                                        .withTrekkListe(new ArrayList<WSTrekk>())
                                        .withSkattListe(new WSSkatt().withSkattebeloep(-2267.00))
                                        .withSkattsum(-2267.00)
                                        .withYtelseNettobeloep(4389.00)
                                        .withBilagsnummer("***REMOVED***"))
                        .withForfallsdato(now().minusYears(1).minusDays(2*NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTilKonto(new WSBankkonto().withKontonummer("***REMOVED******REMOVED***896").withKontotype("Konto - Utland"))
                        .withUtbetalingsmetode("Bankkonto")
                        .withUtbetalingsstatus("Under saksbehandling")
        );
    }

    public static WSUtbetaling createOsloKommuneUtbetaling() {
        return new WSUtbetaling()
                .withPosteringsdato(now().minusDays(19))
                .withUtbetaltTil(new WSOrganisasjon().withAktoerId("***REMOVED***").withNavn("Oslo kommune Utbetaling 4"))
                .withUtbetalingsmelding("Sykepenger")
                .withYtelseListe(
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Sykepenger"))
                                .withRettighetshaver(new WSPerson().withAktoerId("***REMOVED***").withNavn("Per Pettersen Eksempel 4"))
                                .withRefundertForOrg(new WSOrganisasjon().withAktoerId("***REMOVED***").withNavn("Plan- og bygningsetaten"))
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW).minusDays(15)).withTom(now().minusDays(NUMBER_OF_DAYS_TO_SHOW).minusDays(15)))
                                .withYtelseskomponentListe(
                                        LagTestWSYtelse.lagYtelseskomponent("Sykepenger, arbeidstakere", 15000.00))
                                .withYtelseskomponentersum(15000.00)
                                .withTrekkListe(LagTestWSYtelse.lagWSTrekk("Kreditortrekk", 900.00, "***REMOVED***"))
                                .withTrekksum(900.00)
                                .withSkattListe(new WSSkatt().withSkattebeloep(-2267.00))
                                .withSkattsum(-2267.00)
                                .withYtelseNettobeloep(13633.00)
                                .withBilagsnummer("***REMOVED***"))
                .withUtbetalingsdato(now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW))
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Konto - Norge").withKontonummer("22222222222"))
                .withUtbetalingsmetode("Bankkonto")
                .withUtbetalingsstatus("Utbetalt");
    }

    public static WSUtbetaling createOlaNordmannUtbetaling() {
        WSPerson personOlaNordmann = new WSPerson().withAktoerId("22222222222").withNavn("Ola Nordmann Utbetaling 2");
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
                                .withTrekkListe(new ArrayList<WSTrekk>())
                                .withTrekksum(0.00)
                                .withYtelseNettobeloep(21419.75)
                                .withBilagsnummer("30742-5731"))
                .withForfallsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW).plusDays(14))
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Utbetalingskort - Norge"))
                .withUtbetalingsmetode("Utbetalingskort")
                .withUtbetalingsstatus("Sendt kontofører, avventer forfallsdato");
    }

    public static WSUtbetaling createUtbetalingMedValgtUtbetalingOgPosteringsdato(DateTime posteringsdato, DateTime utbetalingsdato ) {
        WSPerson personOlaNordmann = new WSPerson().withAktoerId("22222222222").withNavn("Ola Nordmann Utbetaling 2");
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
                                .withTrekkListe(new ArrayList<WSTrekk>())
                                .withTrekksum(0.00)
                                .withYtelseNettobeloep(21419.75)
                                .withBilagsnummer("30742-5731"))
                .withForfallsdato(posteringsdato)
                .withUtbetalingsdato(utbetalingsdato)
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Konto - Norge").withKontonummer("22222222222"))
                .withUtbetalingsmetode("Bankkonto")
                .withUtbetalingsstatus("Utbetalt");
    }

}
