package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.modig.lang.collections.iter.ReduceFunction;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.modig.lang.collections.TransformerUtils.first;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.END;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.START;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.HovedytelseUtils.grupperPaaHovedytelseOgPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.HovedYtelseVM.HovedYtelseComparator.HOVEDYTELSE_NAVN;


public class OppsummeringVM implements Serializable {

    public LocalDate sluttDato;
    public LocalDate startDato;
    public List<HovedYtelseVM> hovedytelser;
    public String utbetalt, trekk, brutto;

    public OppsummeringVM(List<Record<Hovedytelse>> hovedytelser, LocalDate startDato, LocalDate sluttDato) {
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        this.utbetalt = getBelopString(on(hovedytelser).map(Hovedytelse.nettoUtbetalt).reduce(sumDouble));
        this.trekk = getBelopString(on(hovedytelser).map(Hovedytelse.sammenlagtTrekkBeloep).reduce(sumDouble));
        this.brutto = getBelopString(on(hovedytelser).map(Hovedytelse.bruttoUtbetalt).reduce(sumDouble));
        this.hovedytelser = lagHovetytelseVMer(hovedytelser);
    }

    /**
     * Slå sammen alle ytelsene i utbetalinger når de har samme hovedytelse og underytelse-tittel
     */
    private static List<HovedYtelseVM> lagHovetytelseVMer(List<Record<Hovedytelse>> ytelser) {
        List<HovedYtelseVM> hovedYtelseVMs = new ArrayList<>();

        for (List<Record<Hovedytelse>> grupperteHovedytelser : grupperPaaHovedytelseOgPeriode(ytelser)) {
            Map<String, List<Record<?>>> indekserteUnderytelser = groupUnderytelseByType(grupperteHovedytelser);

            List<Record<Underytelse>> sammenlagteUnderytelser = combineUnderytelser(indekserteUnderytelser);

            Double brutto = on(grupperteHovedytelser).map(Hovedytelse.bruttoUtbetalt).reduce(sumDouble);
            Double trekk = on(grupperteHovedytelser).map(Hovedytelse.sammenlagtTrekkBeloep).reduce(sumDouble);
            Double nettoUtbetalt = on(grupperteHovedytelser).map(Hovedytelse.nettoUtbetalt).reduce(sumDouble);

            DateTime startPeriode = getStartPeriode(grupperteHovedytelser);
            DateTime sluttPeriode = getSluttPeriode(grupperteHovedytelser);

            hovedYtelseVMs.add(new HovedYtelseVM(grupperteHovedytelser.get(0).get(Hovedytelse.ytelse),
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

    protected static DateTime getSluttPeriode(List<Record<Hovedytelse>> grupperteHovedytelser) {
        return on(grupperteHovedytelser).collect(reverseOrder(compareWith(first(Hovedytelse.ytelsesperiode).then(END)))).get(0).get(Hovedytelse.ytelsesperiode).getEnd();
    }

    protected static DateTime getStartPeriode(List<Record<Hovedytelse>> grupperteHovedytelser) {
        return on(grupperteHovedytelser).collect(compareWith(first(Hovedytelse.ytelsesperiode).then(START))).get(0).get(Hovedytelse.ytelsesperiode).getStart();
    }

    protected static List<Record<Underytelse>> combineUnderytelser(Map<String, List<Record<?>>> indekserteUnderytelser) {
        List<Record<Underytelse>> sammenlagteUnderytelser = on(indekserteUnderytelser.values()).reduce(TO_TOTAL_OF_UNDERYTELSER);
        sammenlagteUnderytelser = on(sammenlagteUnderytelser).collect(reverseOrder(compareWith(Underytelse.ytelseBeloep)));
        return sammenlagteUnderytelser;
    }

    protected static Map<String, List<Record<?>>> groupUnderytelseByType(List<Record<Hovedytelse>> sammen) {
        return on(sammen).flatmap(Hovedytelse.underytelseListe).reduce(indexBy(Underytelse.ytelsesType));
    }
}
