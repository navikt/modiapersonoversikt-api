package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;

import static java.util.Collections.sort;
import static java.util.stream.Collectors.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.isUnixEpoch;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.groupByHovedytelseAndPeriod;
import static no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.HovedYtelseVM.HovedYtelseComparator.HOVEDYTELSE_NAVN;
import static no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.UtbetalingStatuser.RETURNERT_TIL_NAV;


public class OppsummeringVM implements Serializable {

    public LocalDate sluttDato;
    public LocalDate startDato;
    public LocalDate visningSluttDato;
    public List<HovedYtelseVM> hovedytelser;
    public String utbetalt, trekk, brutto;

    public static Collector<Double, ?, Double> sumDouble = summingDouble((d) -> d.doubleValue());


    public OppsummeringVM(List<Hovedytelse> hovedytelser, LocalDate startDato, LocalDate sluttDato, LocalDate visningSluttDato) {
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        this.visningSluttDato = visningSluttDato;

        List<Hovedytelse> utbetalteHovedytelser = finnUtbetalteHovedytelser(hovedytelser);

        this.utbetalt = getBelopString(nettoUtbetaltForAlle(utbetalteHovedytelser));
        this.trekk = getBelopString(trekkBeloepForAlle(utbetalteHovedytelser));
        this.brutto = getBelopString(bruttoUtbetaltForAlle(utbetalteHovedytelser));
        this.hovedytelser = lagHovetytelseVMer(utbetalteHovedytelser);
    }

    private List<Hovedytelse> finnUtbetalteHovedytelser(List<Hovedytelse> hovedytelser) {
        return hovedytelser.stream()
                .filter(hovedytelse -> !RETURNERT_TIL_NAV.utbetalingstatus.equals(hovedytelse.getUtbetalingsstatus()))
                .filter(hovedytelse -> hovedytelse.getUtbetalingsDato() != null)
                .collect(toList());
    }

    public static Double bruttoUtbetaltForAlle(List<Hovedytelse> hovedytelser) {
        return hovedytelser
                .stream()
                .map(hovedytelse -> hovedytelse.getBruttoUtbetalt())
                .collect(sumDouble);
    }

    public static Double nettoUtbetaltForAlle(List<Hovedytelse> hovedytelser) {
        return hovedytelser
                .stream()
                .map(hovedytelse -> hovedytelse.getNettoUtbetalt())
                .collect(sumDouble);
    }

    public static Double trekkBeloepForAlle(List<Hovedytelse> hovedytelser) {
        return hovedytelser
                .stream()
                .map(hovedytelse -> hovedytelse.getSammenlagtTrekkBeloep())
                .collect(sumDouble);
    }

    /**
     * Slå sammen alle ytelsene i utbetalinger når de har samme hovedytelse og underytelse-tittel
     */
    private static List<HovedYtelseVM> lagHovetytelseVMer(List<Hovedytelse> ytelser) {
        List<HovedYtelseVM> hovedYtelseVMs = new ArrayList<>();

        for (List<Hovedytelse> grupperteHovedytelser : groupByHovedytelseAndPeriod(ytelser)) {
            Map<String, List<Underytelse>> indekserteUnderytelser = groupUnderytelseByType(grupperteHovedytelser);

            List<Underytelse> sammenlagteUnderytelser = combineUnderytelser(indekserteUnderytelser);

            Double brutto = bruttoUtbetaltForAlle(grupperteHovedytelser);
            Double trekk = trekkBeloepForAlle(grupperteHovedytelser);
            Double nettoUtbetalt = nettoUtbetaltForAlle(grupperteHovedytelser);

            DateTime startPeriode = getStartPeriode(grupperteHovedytelser);
            DateTime sluttPeriode = getSluttPeriode(grupperteHovedytelser);

            hovedYtelseVMs.add(new HovedYtelseVM(grupperteHovedytelser.get(0).getYtelse(),
                    sammenlagteUnderytelser,
                    brutto,
                    trekk,
                    nettoUtbetalt,
                    startPeriode,
                    sluttPeriode));
        }

        sort(hovedYtelseVMs, HOVEDYTELSE_NAVN);
        return hovedYtelseVMs;
    }

    public String getOppsummertPeriode() {
        if (isPeriodeWithinSameMonthAndYear()) {
            return startDato.toString("MMMM yyyy", Locale.getDefault());
        } else if (isUnixEpoch(startDato) && isUnixEpoch(sluttDato)) {
            return new StringResourceModel("utbetaling.lamell.total.oppsummering.udefinertperiode", null).getString();
        }
        return Datoformat.kortUtenLiteral(startDato.toDateTimeAtStartOfDay()) + " - " +
                Datoformat.kortUtenLiteral(visningSluttDato.toDateTimeAtCurrentTime());
    }


    protected boolean isPeriodeWithinSameMonthAndYear() {
        return startDato.getMonthOfYear() == sluttDato.getMonthOfYear()
                && startDato.getYear() == sluttDato.getYear();
    }

    protected static DateTime getSluttPeriode(List<Hovedytelse> grupperteHovedytelser) {
        Comparator<Hovedytelse> senesteYtelsesStartForst = (h1, h2) -> h2.getYtelsesperiode().getEnd().compareTo(h1.getYtelsesperiode().getEnd());
        return grupperteHovedytelser
                .stream()
                .sorted(senesteYtelsesStartForst)
                .findFirst()
                .map(hovedytelse -> hovedytelse.getYtelsesperiode().getStart())
                .get();
    }

    protected static DateTime getStartPeriode(List<Hovedytelse> grupperteHovedytelser) {
        Comparator<Hovedytelse> tidligsteYtelsesStartForst = (h1, h2) -> h1.getYtelsesperiode().getStart().compareTo(h2.getYtelsesperiode().getStart());
        return grupperteHovedytelser
                .stream()
                .sorted(tidligsteYtelsesStartForst)
                .findFirst()
                .map(hovedytelse -> hovedytelse.getYtelsesperiode().getStart())
                .get();
    }

    protected static List<Underytelse> combineUnderytelser(Map<String, List<Underytelse>> indekserteUnderytelser) {

        BiFunction<ArrayList<Underytelse>, List<Underytelse>, ArrayList<Underytelse>> accumulator = (acc, ytelser) -> {

            if (!ytelser.isEmpty()) {
                Double sum = ytelser.stream().map(underytelse -> underytelse.getYtelseBeloep()).collect(sumDouble);
                Underytelse sammenlagtUnderytelse = kopierUnderytelse(ytelser.get(0))
                        .withYtelseBeloep(sum);

                acc.add(sammenlagtUnderytelse);
            }
            return acc;
        };

        BinaryOperator<ArrayList<Underytelse>> combiner = (underytelsesliste1, underytelsesliste2) -> {
            ArrayList<Underytelse> res = new ArrayList<>();
            res.addAll(underytelsesliste1);
            res.addAll(underytelsesliste2);
            return res;
        };

        List<Underytelse> sammenlagteUnderytelser = indekserteUnderytelser.values()
                .stream()
                .reduce(new ArrayList<>(), accumulator, combiner);

        return sammenlagteUnderytelser
                .stream()
                .sorted((o1, o2) -> o2.getYtelseBeloep().compareTo(o1.getYtelseBeloep()))
                .collect(toList());
    }

    protected static Underytelse kopierUnderytelse(Underytelse underytelse) {
        return new Underytelse()
                .withYtelsesType(underytelse.getYtelsesType())
                .withSatsBeloep(underytelse.getSatsBeloep())
                .withSatsType(underytelse.getSatsType())
                .withYtelseBeloep(underytelse.getYtelseBeloep())
                .withSatsAntall(underytelse.getSatsAntall());
    }

    protected static Map<String, List<Underytelse>> groupUnderytelseByType(List<Hovedytelse> sammen) {
        List<Underytelse> alleUnderytelser = sammen
                .stream()
                .flatMap(hovedytelse -> hovedytelse.getUnderytelseListe().stream())
                .collect(toList());

        return alleUnderytelser
                .stream()
                .collect(groupingBy(underytelse -> underytelse.getYtelsesType()));
    }
}
