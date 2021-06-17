package no.nav.modiapersonoversikt.legacy.utbetaling.domain.util;

import no.nav.modiapersonoversikt.legacy.utbetaling.domain.Hovedutbetaling;
import no.nav.modiapersonoversikt.legacy.utbetaling.domain.Hovedytelse;
import no.nav.modiapersonoversikt.legacy.utbetaling.domain.Mottakertype;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.legacy.utbetaling.domain.transform.Transformers.SAMMENLAGT_UTBETALING_TRANSFORMER;
import static no.nav.modiapersonoversikt.legacy.utbetaling.domain.transform.Transformers.TO_HOVEDYTELSE;
import static no.nav.modiapersonoversikt.legacy.utbetaling.domain.util.DateUtils.minusDaysAndFixedAtMidnightAtDayBefore;
import static org.joda.time.LocalDate.now;

public class YtelseUtils {

    public static final int ANTALL_DAGER_FRAMOVER_I_TID = 100;

    public static LocalDate defaultStartDato() {
        return now().minusDays(30);
    }

    public static LocalDate defaultSluttDato() {
        return now().plusDays(ANTALL_DAGER_FRAMOVER_I_TID);
    }

    public static LocalDate defaultVisningSluttDato() {
        return now();
    }

    public static final Comparator<Hovedytelse> SISTE_HOVEDYTELSESDATO_FORST = (ytelse1, ytelse2) -> {
        DateTime ytelse1Hovedytelsedato = ytelse1.getHovedytelsedato().toLocalDate().toDateTimeAtStartOfDay();
        DateTime ytelse2Hovedytelsedato = ytelse2.getHovedytelsedato().toLocalDate().toDateTimeAtStartOfDay();

        int compareDato = ytelse2Hovedytelsedato.compareTo(ytelse1Hovedytelsedato);
        if (compareDato == 0) {
            return ytelse1.getYtelse().compareToIgnoreCase(ytelse2.getYtelse());
        }
        return compareDato;
    };

    public static final Comparator<Hovedutbetaling> SISTE_UTBETALING_FORST = (hovedutbetaling1, hovedutbetaling2) -> {
        DateTime ytelse1Hovedytelsedato = hovedutbetaling1.getHovedytelsesdato().toLocalDate().toDateTimeAtStartOfDay();
        DateTime ytelse2Hovedytelsedato = hovedutbetaling2.getHovedytelsesdato().toLocalDate().toDateTimeAtStartOfDay();

        int compareDato = ytelse2Hovedytelsedato.compareTo(ytelse1Hovedytelsedato);
        if (compareDato == 0) {
            String ytelsenavn1 = hovedutbetaling1.getHovedytelser().get(0).getYtelse();
            String ytelsenavn2 = hovedutbetaling2.getHovedytelser().get(0).getYtelse();
            return ytelsenavn1.compareTo(ytelsenavn2);
        }

        return compareDato;
    };

    public static List<Hovedytelse> getHovedytelseListe(List<WSUtbetaling> utbetalingerMedPosteringInnenPerioden) {
        return utbetalingerMedPosteringInnenPerioden.stream()
                .flatMap(wsUtbetaling -> TO_HOVEDYTELSE.apply(wsUtbetaling).stream())
                .sorted(SISTE_HOVEDYTELSESDATO_FORST)
                .collect(toList());
    }

    public static List<Hovedutbetaling> getHovedUtbetalinger(List<WSUtbetaling> utbetalingerMedPosteringInnenPerioden) {
        return utbetalingerMedPosteringInnenPerioden.stream()
                .map(SAMMENLAGT_UTBETALING_TRANSFORMER)
                .collect(toList());
    }

    public static Mottakertype mottakertypeForAktoer(WSAktoer wsAktoer) {
        if (wsAktoer instanceof WSPerson) {
            return Mottakertype.BRUKER;
        }
        return Mottakertype.ANNEN_MOTTAKER;
    }

    public static List<Hovedytelse> hovedytelserInnenforIntervall(List<Hovedytelse> hovedytelser, Interval intervall) {
        return hovedytelser
                .stream()
                .filter(hovedytelse -> intervall.contains(hovedytelse.getPosteringsDato()) || intervall.contains(hovedytelse.getUtbetalingsDato()))
                .collect(toList());
    }

    public static SortedMap<YearMonth, List<Hovedutbetaling>> hovedutbetalingerGroupedByYearMonth(List<Hovedutbetaling> hovedutbetalinger) {
        Comparator<YearMonth> eldsteForst = (o1, o2) -> o2.compareTo(o1);

        Map<YearMonth, List<Hovedutbetaling>> hovedutbetalingerSortertPaaYearMonth = hovedutbetalinger.stream()
                .collect(groupingBy(hovedutbetaling -> new YearMonth(hovedutbetaling.getHovedytelsesdato().getYear(), hovedutbetaling.getHovedytelsesdato().getMonthOfYear())));

        TreeMap<YearMonth, List<Hovedutbetaling>> yearMonthListTreeMap = new TreeMap<>(eldsteForst);
        yearMonthListTreeMap.putAll(hovedutbetalingerSortertPaaYearMonth);
        return yearMonthListTreeMap;
    }

    public static List<Hovedytelse> hentAlleSynligeHovedytelser(List<Hovedutbetaling> hovedutbetalinger) {
        return hovedutbetalinger.stream()
                .flatMap(hovedutbetaling -> hovedutbetaling.getSynligeHovedytelser().stream())
                .collect(toList());
    }

    private static Predicate<List<Hovedytelse>> erISammePeriodeSom(Hovedytelse hovedytelse) {
        return utbetalinger -> {
            DateTime start = hovedytelse.getYtelsesperiode().getStart().minusDays(1);
            return utbetalinger
                    .stream()
                    .anyMatch((periode) -> !periode.getYtelsesperiode().getEnd().isBefore(start));
        };
    }

    /**
     * Grupper på hovdytelser
     * Innenfor hver hovedytelse, grupper på perioder
     */
    public static List<List<Hovedytelse>> groupByHovedytelseAndPeriod(List<Hovedytelse> utbetalinger) {
        Comparator<Hovedytelse> forsteHovedytelseForst = (h1, h2) -> h1.getYtelsesperiode().getStart().compareTo(h2.getYtelsesperiode().getStart());
        Stream<List<Hovedytelse>> sortertOgGruppertEtterHovedytelse = grupperHovedytelseBasertPaaYtelse(utbetalinger)
                .stream()
                .map(sorter(forsteHovedytelseForst));

        BinaryOperator<List<List<Hovedytelse>>> combiner = (hovedytelsesliste1, hovedytelsesliste2) -> {
            ArrayList<List<Hovedytelse>> res = new ArrayList<>();
            res.addAll(hovedytelsesliste1);
            res.addAll(hovedytelsesliste2);
            return res;
        };

        BiFunction<List<List<Hovedytelse>>, Hovedytelse, List<List<Hovedytelse>>> accumulator = (accu, hovedytelse) -> {
            Optional<List<Hovedytelse>> sammePeriode = accu.stream().filter(erISammePeriodeSom(hovedytelse)).findFirst();
            List<Hovedytelse> liste;
            if (sammePeriode.isPresent()) {
                liste = sammePeriode.get();
            } else {
                liste = new ArrayList<>();
                accu.add(liste);
            }
            liste.add(hovedytelse);
            return accu;
        };

        return sortertOgGruppertEtterHovedytelse
                .flatMap(sammeHovedytelse -> sammeHovedytelse.stream().reduce(new ArrayList<>(), accumulator, combiner).stream())
                .collect(toList());
    }

    private static Function<List<Hovedytelse>, List<Hovedytelse>> sorter(Comparator<Hovedytelse> forsteHovedytelseForst) {
        return hovedytelseListe -> hovedytelseListe.stream().sorted(forsteHovedytelseForst).collect(toList());
    }

    /**
     * true hvis hovedutbetalingen er mellom now() og antall dager tilbake i tid, gitt ved <em>numberOfDaysToShow</em><br>
     * F.eks<br>
     * now: 2015-03-04 <br>
     * numberOfDaysToShow: 30 <br>
     * gyldig periode: 2014-12-05 - 2015-01-04 <br>
     */
    public static Predicate<Hovedutbetaling> betweenNowAndDaysBefore(final int numberOfDaysToShow) {
        return hovedytelse -> {
            DateTime hovedytelseDato = hovedytelse.getHovedytelsesdato();
            DateTime threshold = minusDaysAndFixedAtMidnightAtDayBefore(DateTime.now(), numberOfDaysToShow);
            return hovedytelseDato.isAfter(threshold);
        };
    }

    private static List<List<Hovedytelse>> grupperHovedytelseBasertPaaYtelse(List<Hovedytelse> ytelser) {
        Map<String, List<Hovedytelse>> hovedytelserGruppertPaaYtelse = ytelser
                .stream()
                .collect(groupingBy(Hovedytelse::getYtelse));
        return new ArrayList<>(hovedytelserGruppertPaaYtelse.values());
    }
}
