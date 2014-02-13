package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
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
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.SUM_UNDERYTELSER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.TREKK_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UNDERYTELSE_COMPARE_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UNDERYTELSE_SKATT_NEDERST;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UNDERYTELSE_TITTEL;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UTBETALT_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UNDERYTELSER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UTBETALINGSDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.grupperPaaHovedytelseOgPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.HovedYtelseVM.HovedYtelseComparator.HOVEDYTELSE_NAVN;


public class OppsummeringVM implements Serializable {

    public List<Utbetaling> utbetalinger;
    public LocalDate sluttDato;
    public LocalDate startDato;
    public List<HovedYtelseVM> hovedytelser;
    public String utbetalt, trekk, brutto;

    public OppsummeringVM(List<Utbetaling> utbetalinger, LocalDate startDato, LocalDate sluttDato) {
        this.utbetalinger = utbetalinger;
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        this.utbetalt = getBelopString(on(utbetalinger).map(NETTO).reduce(sumDouble));
        this.trekk = getBelopString(on(utbetalinger).map(BEREGNET_TREKK).reduce(sumDouble));
        this.brutto = getBelopString(on(utbetalinger).map(BRUTTO).reduce(sumDouble));
        this.hovedytelser = lagHovetytelseVMer(utbetalinger);
    }

    /**
     * Slå sammen alle ytelsene i utbetalinger når de har samme hovedytelse og underytelse-tittel
     */
    private static List<HovedYtelseVM> lagHovetytelseVMer(List<Utbetaling> utbetalinger) {
        List<HovedYtelseVM> hovedYtelseVMs = new ArrayList<>();
        for (List<Utbetaling> sammen : grupperPaaHovedytelseOgPeriode(utbetalinger)) {
            Map<String, List<Underytelse>> indekserteUnderytelser = on(sammen).flatmap(UNDERYTELSER).reduce(indexBy(UNDERYTELSE_TITTEL));

            List<Underytelse> sammenlagteUnderytelser = on(indekserteUnderytelser.values()).reduce(SUM_UNDERYTELSER);
            sort(sammenlagteUnderytelser, UNDERYTELSE_COMPARE_BELOP);
            sort(sammenlagteUnderytelser, UNDERYTELSE_SKATT_NEDERST);

            Double brutto = on(sammenlagteUnderytelser).map(UTBETALT_BELOP).reduce(sumDouble);
            Double trekk = on(sammenlagteUnderytelser).map(TREKK_BELOP).reduce(sumDouble);
            Double utbetalt = brutto + trekk;

            DateTime min = on(sammen).collect(compareWith(UTBETALINGSDATO)).get(0).getUtbetalingsdato();
            DateTime max = on(sammen).collect(reverseOrder(compareWith(UTBETALINGSDATO))).get(0).getUtbetalingsdato();

            hovedYtelseVMs.add(new HovedYtelseVM(sammen.get(0).getHovedytelse(), sammenlagteUnderytelser, brutto, trekk, utbetalt, min, max));
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

    private static final Transformer<Utbetaling, Double> BRUTTO = new Transformer<Utbetaling, Double>() {
        @Override
        public Double transform(Utbetaling utbetaling) {
            return utbetaling.getBrutto();
        }
    };
    private static final Transformer<Utbetaling, Double> NETTO = new Transformer<Utbetaling, Double>() {
        @Override
        public Double transform(Utbetaling utbetaling) {
            return utbetaling.getUtbetalt();
        }
    };
    private static final Transformer<Utbetaling, Double> BEREGNET_TREKK = new Transformer<Utbetaling, Double>() {
        @Override
        public Double transform(Utbetaling utbetaling) {
            return utbetaling.getTrekk() == 0.0 ?
                    utbetaling.getBrutto() - utbetaling.getUtbetalt() :
                    utbetaling.getTrekk();
        }
    };
}
