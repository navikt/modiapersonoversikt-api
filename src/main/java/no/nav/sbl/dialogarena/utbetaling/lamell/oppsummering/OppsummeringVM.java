package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.modig.lang.collections.iter.ReduceFunction;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collector;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.summingDouble;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.isUnixEpoch;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.groupByHovedytelseAndPeriod;
import static no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.HovedYtelseVM.HovedYtelseComparator.HOVEDYTELSE_NAVN;


public class OppsummeringVM implements Serializable {

    public LocalDate sluttDato;
    public LocalDate startDato;
    public List<HovedYtelseVM> hovedytelser;
    public String utbetalt, trekk, brutto;

    public static Collector<Double, ?, Double> sumDouble = summingDouble((d) -> d.doubleValue());


    public OppsummeringVM(List<Hovedytelse> hovedytelser, LocalDate startDato, LocalDate sluttDato) {
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        this.utbetalt = getBelopString(hovedytelser
                .stream()
                .map(hovedytelse -> hovedytelse.getNettoUtbetalt())
                .collect(sumDouble)
        );

        this.trekk = getBelopString(hovedytelser
                .stream()
                .map(hovedytelse -> hovedytelse.getSammenlagtTrekkBeloep())
                .collect(sumDouble)
        );
        this.brutto = getBelopString(hovedytelser
                .stream()
                .map(hovedytelse -> hovedytelse.getBruttoUtbetalt())
                .collect(sumDouble)
        );
        this.hovedytelser = lagHovetytelseVMer(hovedytelser);
    }

    /**
     * Slå sammen alle ytelsene i utbetalinger når de har samme hovedytelse og underytelse-tittel
     */
    private static List<HovedYtelseVM> lagHovetytelseVMer(List<Hovedytelse> ytelser) {
        List<HovedYtelseVM> hovedYtelseVMs = new ArrayList<>();

        for (List<Hovedytelse> grupperteHovedytelser : groupByHovedytelseAndPeriod(ytelser)) {
            Map<String, List<?>> indekserteUnderytelser = groupUnderytelseByType(grupperteHovedytelser);

            List<Underytelse> sammenlagteUnderytelser = combineUnderytelser(indekserteUnderytelser);

            Double brutto = grupperteHovedytelser
                    .stream()
                    .map(hovedytelse -> hovedytelse.getBruttoUtbetalt())
                    .collect(sumDouble);
            Double trekk = grupperteHovedytelser
                    .stream()
                    .map(hovedytelse -> hovedytelse.getSammenlagtTrekkBeloep())
                    .collect(sumDouble);
            Double nettoUtbetalt = grupperteHovedytelser
                    .stream()
                    .map(hovedytelse -> hovedytelse.getNettoUtbetalt())
                    .collect(sumDouble);

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
        } else if(isUnixEpoch(startDato) && isUnixEpoch(sluttDato)) {
            return new StringResourceModel("utbetaling.lamell.total.oppsummering.udefinertperiode", null).getString();
        }
        return Datoformat.kortUtenLiteral(startDato.toDateTimeAtStartOfDay()) + " - " +
                Datoformat.kortUtenLiteral(sluttDato.toDateTimeAtCurrentTime());
    }

    public static final ReduceFunction<List<Record<?>>, List<Record<Underytelse>>> TO_TOTAL_OF_UNDERYTELSER = new ReduceFunction<List<Record<?>>, List<Record<Underytelse>>>() {
        @Override
        public List<Record<Underytelse>> reduce(List<Record<Underytelse>> accumulator, List<Record<?>> ytelser) {
            if (!ytelser.isEmpty()) {
                Double sum = on(ytelser).map(Underytelse.ytelseBeloep).reduce(sumDouble);
                accumulator.add((Record<Underytelse>) ytelser.get(0).with(Underytelse.ytelseBeloep, sum));
            }
            return accumulator;
        }

        @Override
        public List<Record<Underytelse>> identity() {
            return new ArrayList<>();
        }
    };

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

    protected static List<Underytelse> combineUnderytelser(Map<String, List<?>> indekserteUnderytelser) {
        List<Underytelse> sammenlagteUnderytelser = on(indekserteUnderytelser.values()).reduce(TO_TOTAL_OF_UNDERYTELSER);
        sammenlagteUnderytelser = sammenlagteUnderytelser
                .stream()
                .sorted(((o1, o2) -> o1.ytelseBe))

                on(sammenlagteUnderytelser).collect(reverseOrder(compareWith(Underytelse.ytelseBeloep)));
        return sammenlagteUnderytelser;
    }

    protected static Map<String, List<?>> groupUnderytelseByType(List<Hovedytelse> sammen) {

        //1 YtelseType - [1...2...3]

        sammen.stream().map(hovedytelse -> )

//        return sammen.stream().flatMap(hovedytelse -> hovedytelse.getUnderytelseListe()).collec
        return on(sammen).flatmap(Hovedytelse.underytelseListe).reduce(indexBy(Underytelse.ytelsesType));
    }
}
