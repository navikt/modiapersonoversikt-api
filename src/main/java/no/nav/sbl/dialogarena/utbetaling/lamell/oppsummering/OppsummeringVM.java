package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.modig.lang.collections.ComparatorUtils;
import no.nav.modig.lang.collections.iter.ReduceFunction;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.UnderytelseGammel;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.*;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.modig.lang.collections.TransformerUtils.first;
import static no.nav.sbl.dialogarena.utbetaling.domain.UnderytelseGammel.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.END;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.START;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.HovedytelseUtils.grupperPaaHovedytelseOgPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.HovedYtelseVM.HovedYtelseComparator.HOVEDYTELSE_NAVN;


public class OppsummeringVM implements Serializable {

    public transient List<Record<Hovedytelse>> utbetalinger;
    public LocalDate sluttDato;
    public LocalDate startDato;
    public List<HovedYtelseVM> hovedytelser;
    public String utbetalt, trekk, brutto;

    public OppsummeringVM(List<Record<Hovedytelse>> hovedytelser, LocalDate startDato, LocalDate sluttDato) {
        this.utbetalinger = hovedytelser;
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        this.utbetalt = getBelopString(on(hovedytelser).map(Hovedytelse.ytelseNettoBeloep).reduce(sumDouble));
        this.trekk = getBelopString(on(hovedytelser).map(Hovedytelse.sumTrekk).reduce(sumDouble));
        this.brutto = getBelopString(on(hovedytelser).map(Hovedytelse.ytelseBruttoBeloep).reduce(sumDouble));
        this.hovedytelser = lagHovetytelseVMer(hovedytelser);
    }

    /**
     * Slå sammen alle ytelsene i utbetalinger når de har samme hovedytelse og underytelse-tittel
     */
    private static List<HovedYtelseVM> lagHovetytelseVMer(List<Record<Hovedytelse>> ytelser) {
        List<HovedYtelseVM> hovedYtelseVMs = new ArrayList<>();
        for (List<Record<Hovedytelse>> sammen : grupperPaaHovedytelseOgPeriode(ytelser)) {

            Map<String, List<Record<?>>> indekserteUnderytelser = on(sammen).flatmap(Hovedytelse.underytelseListe).reduce(indexBy(Underytelse.ytelsesType));



            List<Record<Underytelse>> sammenlagteUnderytelser = on(indekserteUnderytelser.values()).reduce(toTotalOfUnderytelser);
            sammenlagteUnderytelser = on(sammenlagteUnderytelser).collect(reverseOrder(compareWith(Underytelse.ytelseBeloep)));

            Double brutto = on(sammenlagteUnderytelser).map(Underytelse.ytelseBeloep).reduce(sumDouble);
            Double trekk = on(sammenlagteUnderytelser).map(TREKK_BELOP).reduce(sumDouble);
            Double utbetalt = brutto + trekk;

            DateTime startPeriode = on(sammen).collect(compareWith(first(Hovedytelse.ytelsesperiode).then(START))).get(0).get(Hovedytelse.ytelsesperiode).getStart();
            DateTime sluttPeriode = on(sammen).collect(reverseOrder(compareWith(first(Hovedytelse.ytelsesperiode).then(END)))).get(0).get(Hovedytelse.ytelsesperiode).getEnd();

            hovedYtelseVMs.add(new HovedYtelseVM(sammen.get(0).get(Hovedytelse.ytelse), sammenlagteUnderytelser, brutto, trekk, utbetalt, startPeriode, sluttPeriode));
        }
        sort(hovedYtelseVMs, HOVEDYTELSE_NAVN);
        return hovedYtelseVMs;
    }

    public String getOppsummertPeriode() {
        if (startDato.getMonthOfYear() == sluttDato.getMonthOfYear()
                && startDato.getYear() == sluttDato.getYear()) {
            return startDato.toString("MMMM yyyy", Locale.getDefault());
        }
        return Datoformat.kortUtenLiteral(startDato.toDateTimeAtStartOfDay()) + " - " +
                Datoformat.kortUtenLiteral(sluttDato.toDateTimeAtCurrentTime());
    }
//
//    /**
//     * Sorterer basert på beløpet, slik at negative tall havner nedenfor de positive
//     */
//    public static final Comparator<UnderytelseGammel> UNDERYTELSE_COMPARE_BELOP = new Comparator<UnderytelseGammel>() {
//        @Override
//        public int compare(UnderytelseGammel o1, UnderytelseGammel o2) {
//            return Double.compare(o2.getBelop(), o1.getBelop());
//        }
//    };
//
//    /**
//     * Sorterer underytelser med tittel som inneholder "skatt", og er negativt, nederst.
//     */
//    public static final Comparator<UnderytelseGammel> UNDERYTELSE_SKATT_NEDERST = new Comparator<UnderytelseGammel>(){
//        private static final String SKATT = "skatt";
//
//        @Override
//        public int compare(UnderytelseGammel o1, UnderytelseGammel o2) {
//            if (o1.getTittel().toLowerCase().contains(SKATT) && o1.getBelop() <= 0) {
//                return 1;
//            } else if (o2.getTittel().toLowerCase().contains(SKATT) && o2.getBelop() <= 0) {
//                return -1;
//            }
//            return 0;
//        }


    public static ReduceFunction<List<Record<?>>, List<Record<Underytelse>>> toTotalOfUnderytelser = new ReduceFunction<List<Record<?>>, List<Record<Underytelse>>>() {
        @Override
        public List<Record<Underytelse>> reduce(List<Record<Underytelse>> accumulator, List<Record<?>> ytelser) {
            if (ytelser.isEmpty()) {
                return accumulator;
            }
            Double sum = on(ytelser).map(Underytelse.ytelseBeloep).reduce(sumDouble);
            Record<Underytelse> summertUnderytelse = (Record<Underytelse>) ytelser.get(0)
                    .with(Underytelse.ytelseBeloep, sum);

            accumulator.add(summertUnderytelse);
            return accumulator;
        }

        @Override
        public List<UnderytelseGammel> identity() {
            return new ArrayList<>();
        }
    };
}
