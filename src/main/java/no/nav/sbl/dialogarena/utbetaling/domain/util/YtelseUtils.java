package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSPerson;
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
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.intervalFromStartEndDate;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.minusDaysAndFixedAtMidnightAtDayBefore;
import static org.joda.time.LocalDate.now;

public class YtelseUtils {

    public static LocalDate defaultStartDato() {
        return now().minusDays(30);
    }

    public static LocalDate defaultSluttDato() {
        return now();
    }

    public static final Comparator<Hovedytelse> SISTE_HOVEDYTELSESDATO_FORST = (ytelse1, ytelse2) -> {
        DateTime ytelse1Hovedytelsedato = ytelse1.getHovedytelsedato().toLocalDate().toDateTimeAtStartOfDay();
        DateTime ytelse2Hovedytelsedato = ytelse2.getHovedytelsedato().toLocalDate().toDateTimeAtStartOfDay();

        int compareDato = -ytelse1Hovedytelsedato.compareTo(ytelse2Hovedytelsedato);
        if (compareDato == 0) {
            return ytelse1.getYtelse().compareToIgnoreCase(ytelse2.getYtelse());
        }
        return compareDato;
    };

    public static Mottakertype mottakertypeForAktoer(WSAktoer wsAktoer) {
        if (wsAktoer instanceof WSPerson) {
            return Mottakertype.BRUKER;
        }
        return Mottakertype.ANNEN_MOTTAKER;
    }

    public static List<Hovedytelse> hovedytelserFromPeriod(List<Hovedytelse> hovedytelser, LocalDate startDato, LocalDate sluttDato) {
        final Interval intervall = intervalFromStartEndDate(startDato, sluttDato);

        return hovedytelser
                .stream()
                .filter(hovedytelse -> intervall.contains(hovedytelse.getPosteringsDato()) || intervall.contains(hovedytelse.getUtbetalingsDato()))
                .collect(toList());
    }

    public static TreeMap<YearMonth, List<Hovedytelse>> ytelserGroupedByYearMonth(List<Hovedytelse> hovedytelser) {
        Comparator<YearMonth> eldsteForst = (o1, o2) -> o2.compareTo(o1);

        Map<YearMonth, List<Hovedytelse>> hovedytelserSortertPaaYearMonth = hovedytelser
                .stream()
                .collect(groupingBy(hovedytelse -> new YearMonth(hovedytelse.getHovedytelsedato().getYear(), hovedytelse.getHovedytelsedato().getMonthOfYear())));

        TreeMap<YearMonth, List<Hovedytelse>> yearMonthListTreeMap = new TreeMap<>(eldsteForst);
        yearMonthListTreeMap.putAll(hovedytelserSortertPaaYearMonth);
        return yearMonthListTreeMap;
    }

    protected static Predicate<List<Hovedytelse>> erISammePeriodeSom(Hovedytelse hovedytelse) {
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
     * true hvis hovedytelsen er mellom now() og antall dager tilbake i tid, gitt ved <em>numberOfDaysToShow</em><br>
     * F.eks<br>
     * now: 2015-03-04 <br>
     * numberOfDaysToShow: 30 <br>
     * gyldig periode: 2014-12-05 - 2015-01-04 <br>
     */
    public static Predicate<Hovedytelse> betweenNowAndDaysBefore(final int numberOfDaysToShow) {
        return hovedytelse -> {
            DateTime hovedytelseDato = hovedytelse.getHovedytelsedato();
            DateTime threshold = minusDaysAndFixedAtMidnightAtDayBefore(DateTime.now(), numberOfDaysToShow);
            return hovedytelseDato.isAfter(threshold);
        };
    }

    protected static List<List<Hovedytelse>> grupperHovedytelseBasertPaaYtelse(List<Hovedytelse> ytelser) {
        Map<String, List<Hovedytelse>> hovedytelserGruppertPaaYtelse = ytelser
                .stream()
                .collect(groupingBy(hovedytelse -> hovedytelse.getYtelse()));
        ArrayList<List<Hovedytelse>> lists = new ArrayList<>();
        lists.addAll(hovedytelserGruppertPaaYtelse.values());
        return lists;
    }
}
