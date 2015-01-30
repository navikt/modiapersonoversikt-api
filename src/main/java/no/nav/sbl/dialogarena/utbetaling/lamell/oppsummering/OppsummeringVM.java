package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.UnderytelseGammel;
import no.nav.sbl.dialogarena.utbetaling.domain.util.HovedytelseUtils;
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
import static no.nav.sbl.dialogarena.utbetaling.domain.UnderytelseGammel.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.PERIODE;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UNDERYTELSER;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.END;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.START;
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
    private static List<HovedYtelseVM> lagHovetytelseVMer(List<Record<Hovedytelse>> utbetalinger) {
        List<HovedYtelseVM> hovedYtelseVMs = new ArrayList<>();
        for (List<Record<Hovedytelse>> sammen : grupperPaaHovedytelseOgPeriode(utbetalinger)) {
            Map<String, List<Record<Underytelse>>> indekserteUnderytelser = on(sammen).flatmap(UNDERYTELSER).reduce(indexBy(UNDERYTELSE_TITTEL));

            List<UnderytelseGammel> sammenlagteUnderytelser = on(indekserteUnderytelser.values()).reduce(SUM_UNDERYTELSER);
            sort(sammenlagteUnderytelser, UNDERYTELSE_COMPARE_BELOP);
            sort(sammenlagteUnderytelser, UNDERYTELSE_SKATT_NEDERST);

            Double brutto = on(sammenlagteUnderytelser).map(UTBETALT_BELOP).reduce(sumDouble);
            Double trekk = on(sammenlagteUnderytelser).map(TREKK_BELOP).reduce(sumDouble);
            Double utbetalt = brutto + trekk;

            DateTime startPeriode = on(sammen).collect(compareWith(first(PERIODE).then(START))).get(0).getPeriode().getStart();
            DateTime sluttPeriode = on(sammen).collect(reverseOrder(compareWith(first(PERIODE).then(END)))).get(0).getPeriode().getEnd();

            hovedYtelseVMs.add(new HovedYtelseVM(sammen.get(0).getHovedytelse(), sammenlagteUnderytelser, brutto, trekk, utbetalt, startPeriode, sluttPeriode));
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
}
