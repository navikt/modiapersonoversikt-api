package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static org.joda.time.DateTime.now;


public class WSUtbetalingTestData {

    public static List<WSUtbetaling> getWsUtbetalinger(String fNr, DateTime startDato, DateTime sluttDato) {
        List<WSUtbetaling> utbetalinger = new ArrayList<>();
        utbetalinger.add(createOlaNordmannUtbetaling());
        utbetalinger.add(createOsloKommuneUtbetaling());
        utbetalinger.add(createOsloKommuneUtbetaling());
        utbetalinger.add(createOsloKommuneUtbetaling());
        utbetalinger.add(createOsloKommuneUtbetaling());
        utbetalinger.add(createOsloKommuneUtbetaling());
        utbetalinger.addAll(kariNordmannUtbetaling());

        final Interval periode = new Interval(startDato, sluttDato);
        Predicate<WSUtbetaling> innenPeriode = new Predicate<WSUtbetaling>() {
            public boolean evaluate(WSUtbetaling object) {
                return periode.contains(object.getPosteringsdato());
            }
        };
        return on(utbetalinger).filter(innenPeriode).collect();
    }

    private static List<WSUtbetaling> kariNordmannUtbetaling() {
        return asList(new WSUtbetaling()
                        .withPosteringsdato(now().minusYears(1))
                        .withUtbetaltTil(new WSPerson().withAktoerId("33333333333").withNavn("Kari Nordmann Utbetaling 3"))
                        .withUtbetalingNettobeloep(19724.00)
                        .withUtbetalingsmelding("Alderspensjon med 3 mnd etterbetaling")
                        .withYtelseListe(
                                kariNordmannYtelse1(),
                                kariNordmannYtelse2(),
                                kariNordmannYtelse3(),
                                kariNordmannYtelse4())
                        .withUtbetalingsdato(now().minusYears(1).plusMonths(2))
                        .withForfallsdato(now().minusYears(1).plusMonths(2))
                        .withUtbetaltTilKonto(new WSBankkonto().withKontonummer("1234567890123456789025896").withKontotype("Konto - Utland"))
                        .withUtbetalingsmetode("Bankkonto")
                        .withUtbetalingsstatus("Utbetalt"),
                new WSUtbetaling()
                        .withPosteringsdato(now().minusYears(1).minusMonths(2))
                        .withUtbetaltTil(new WSPerson().withAktoerId("33333333333").withNavn("Kari Nordmann Utbetaling 3"))
                        .withUtbetalingNettobeloep(4389.00)
                        .withUtbetalingsmelding("Alderspensjon for desember")
                        .withYtelseListe(
                                new WSYtelse()
                                        .withYtelsestype(new WSYtelsestyper().withValue("Alderspensjon"))
                                        .withRettighetshaver(new WSPerson().withAktoerId("33333333333").withNavn("Kari Nordmann Utbetaling 3"))
                                        .withYtelsesperiode(new WSPeriode().withFom(now().minusYears(1).minusMonths(3)).withTom(now().minusYears(1).minusMonths(2)))
                                        .withYtelseskomponentListe(
                                                new WSYtelseskomponent()
                                                        .withYtelseskomponenttype("Grunnpensjon")
                                                        .withYtelseskomponentbeloep(5200.00),
                                                new WSYtelseskomponent()
                                                        .withYtelseskomponenttype("Særtillegg")
                                                        .withYtelseskomponentbeloep(1456.00))
                                        .withYtelseskomponentersum(6656.00)
                                        .withTrekkListe(new ArrayList<WSTrekk>())
                                        .withSkattListe(new WSSkatt().withSkattebeloep(-2267.00))
                                        .withSkattsum(-2267.00)
                                        .withYtelseNettobeloep(4389.00)
                                        .withBilagsnummer("10201445961"))
                        .withForfallsdato(now().minusYears(1).minusMonths(2))
                        .withUtbetaltTilKonto(new WSBankkonto().withKontonummer("1234567890123456789025896").withKontotype("Konto - Utland"))
                        .withUtbetalingsmetode("Bankkonto")
                        .withUtbetalingsstatus("Under saksbehandling")
        );
    }

    private static WSYtelse kariNordmannYtelse4() {
        return new WSYtelse()
                .withYtelsestype(new WSYtelsestyper().withValue("Alderspensjon"))
                .withRettighetshaver(new WSPerson().withAktoerId("33333333333").withNavn("Kari Nordmann Utbetaling 3"))
                .withYtelsesperiode(new WSPeriode().withFom(now().minusYears(1).plusMonths(3)).withTom(now().minusYears(1).plusMonths(4)))
                .withYtelseskomponentListe(
                        new WSYtelseskomponent()
                                .withYtelseskomponenttype("Grunnpensjon")
                                .withYtelseskomponentbeloep(5200.00),
                        new WSYtelseskomponent()
                                .withYtelseskomponenttype("Særtillegg")
                                .withYtelseskomponentbeloep(1456.00))
                .withYtelseskomponentersum(6656.00)
                .withTrekkListe(new WSTrekk().withTrekktype("Kreditorsjekk").withTrekkbeloep(-900.00).withKreditor("00911111111"))
                .withTrekksum(-900.00)
                .withSkattListe(new WSSkatt().withSkattebeloep(-1500.00))
                .withSkattsum(-1500.00)
                .withYtelseNettobeloep(4256.00)
                .withBilagsnummer("10201436985");
    }

    private static WSYtelse kariNordmannYtelse3() {
        return new WSYtelse()
                .withYtelsestype(new WSYtelsestyper().withValue("Alderspensjon"))
                .withRettighetshaver(new WSPerson().withAktoerId("33333333333").withNavn("Kari Nordmann Utbetaling 3"))
                .withYtelsesperiode(new WSPeriode().withFom(now().minusYears(1).plusMonths(1)).withTom(now().minusYears(1).plusMonths(2)))
                .withYtelseskomponentListe(
                        new WSYtelseskomponent()
                                .withYtelseskomponenttype("Grunnpensjon")
                                .withYtelseskomponentbeloep(5200.00),
                        new WSYtelseskomponent()
                                .withYtelseskomponenttype("Særtillegg")
                                .withYtelseskomponentbeloep(1456.00))
                .withYtelseskomponentersum(6656.00)
                .withTrekkListe(new ArrayList<WSTrekk>())
                .withSkattListe(new WSSkatt().withSkattebeloep(-1500.00))
                .withSkattsum(-1500.00)
                .withYtelseNettobeloep(5156.00)
                .withBilagsnummer("10201436985");
    }

    private static WSYtelse kariNordmannYtelse2() {
        return new WSYtelse()
                .withYtelsestype(new WSYtelsestyper().withValue("Alderspensjon"))
                .withRettighetshaver(new WSPerson().withAktoerId("33333333333").withNavn("Kari Nordmann Utbetaling 3"))
                .withYtelsesperiode(new WSPeriode().withFom(now().minusYears(1)).withTom(now().minusYears(1).plusMonths(1)))
                .withYtelseskomponentListe(
                        new WSYtelseskomponent()
                                .withYtelseskomponenttype("Grunnpensjon")
                                .withYtelseskomponentbeloep(5200.00),
                        new WSYtelseskomponent()
                                .withYtelseskomponenttype("Særtillegg")
                                .withYtelseskomponentbeloep(1456.00))
                .withYtelseskomponentersum(6656.00)
                .withTrekkListe(new ArrayList<WSTrekk>())
                .withSkattListe(new WSSkatt().withSkattebeloep(-1500.00))
                .withSkattsum(-1500.00)
                .withYtelseNettobeloep(5156.00)
                .withBilagsnummer("10201436985");
    }

    private static WSYtelse kariNordmannYtelse1() {
        return new WSYtelse()
                .withYtelsestype(new WSYtelsestyper().withValue("Alderspensjon"))
                .withRettighetshaver(new WSPerson().withAktoerId("33333333333").withNavn("Kari Nordmann Utbetaling 3"))
                .withYtelsesperiode(new WSPeriode().withFom(now().minusYears(1).minusMonths(1)).withTom(now().minusYears(1)))
                .withYtelseskomponentListe(
                        new WSYtelseskomponent()
                                .withYtelseskomponenttype("Grunnpensjon")
                                .withYtelseskomponentbeloep(5200.00),
                        new WSYtelseskomponent()
                                .withYtelseskomponenttype("Særtillegg")
                                .withYtelseskomponentbeloep(1456.00))
                .withYtelseskomponentersum(6656.00)
                .withSkattListe(new WSSkatt().withSkattebeloep(-1500.00))
                .withSkattsum(-1500.00)
                .withYtelseNettobeloep(5156.00)
                .withBilagsnummer("10201436985");
    }

    private static WSUtbetaling createOsloKommuneUtbetaling() {
        return new WSUtbetaling()
                .withPosteringsdato(now().minusDays(19))
                .withUtbetaltTil(new WSOrganisasjon().withAktoerId("00999999999").withNavn("Oslo kommune Utbetaling 4"))
                .withUtbetalingNettobeloep(11833.00)
                .withUtbetalingsmelding("Sykepenger")
                .withYtelseListe(
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Sykepenger"))
                                .withRettighetshaver(new WSPerson().withAktoerId("***REMOVED***").withNavn("Per Pettersen Eksempel 4"))
                                .withRefundertForOrg(new WSOrganisasjon().withAktoerId("***REMOVED***").withNavn("Plan- og bygningsetaten"))
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusMonths(2).minusDays(15)).withTom(now().minusMonths(1).minusDays(15)))
                                .withYtelseskomponentListe(
                                        new WSYtelseskomponent()
                                                .withYtelseskomponenttype("Sykepenger, arbeidstakere")
                                                .withYtelseskomponentbeloep(15000.00))
                                .withYtelseskomponentersum(15000.00)
                                .withTrekkListe(
                                        new WSTrekk()
                                                .withTrekktype("Kreditortrekk")
                                                .withTrekkbeloep(-900.00)
                                                .withKreditor("00911111111"))
                                .withTrekksum(-900.00)
                                .withSkattListe(new WSSkatt().withSkattebeloep(-2267.00))
                                .withSkattsum(-2267.00)
                                .withYtelseNettobeloep(11833.00)
                                .withBilagsnummer("10201498456"))
                .withUtbetalingsdato(now().minusMonths(2))
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Konto - Norge").withKontonummer("22222222222"))
                .withUtbetalingsmetode("Bankkonto")
                .withUtbetalingsstatus("Utbetalt")

                ;
    }

    private static WSUtbetaling createOlaNordmannUtbetaling() {
        return new WSUtbetaling()
                .withPosteringsdato(now().minusMonths(1))
                .withUtbetaltTil(new WSPerson()
                        .withAktoerId("22222222222")
                        .withNavn("Ola Nordmann Utbetaling 2"))
                .withUtbetalingNettobeloep(19152.75)
                .withUtbetalingsmelding("Utbetalt dagpenger")
                .withYtelseListe(
                        new WSYtelse()
                                .withYtelsestype(new WSYtelsestyper().withValue("Pleie-, omsorgs- og opplæringspenger"))
                                .withRettighetshaver(new WSPerson()
                                        .withAktoerId("22222222222")
                                        .withNavn("Ola Nordmann Utbetaling 2"))
                                .withYtelsesperiode(new WSPeriode().withFom(now().minusMonths(3)).withTom(now().minusMonths(2)))
                                .withYtelseskomponentListe(
                                        new WSYtelseskomponent()
                                                .withYtelseskomponenttype("Dagpenger")
                                                .withSatsbeloep(389.45)
                                                .withSatstype("DAG")
                                                .withSatsantall(55.0)
                                                .withYtelseskomponentbeloep(21419.75))
                                .withYtelseskomponentersum(21419.75)
                                .withTrekkListe(new ArrayList<WSTrekk>())
                                .withSkattListe(new WSSkatt().withSkattebeloep(-2267.00))
                                .withSkattsum(-2267.00)
                                .withYtelseNettobeloep(19152.75)
                                .withBilagsnummer("30742-5731"))
                .withForfallsdato(now().minusMonths(1).plusDays(14))
                .withUtbetaltTilKonto(new WSBankkonto().withKontotype("Utbetalingskort - Norge"))
                .withUtbetalingsmetode("Utbetalingskort")
                .withUtbetalingsstatus("Returnert for saksbehandling");
    }

}
