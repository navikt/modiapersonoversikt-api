package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.transform.Mergeable;
import org.apache.commons.collections15.Transformer;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseComparator.MERGEABLE_TITTEL;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.HOVEDYTELSE;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UNDERYTELSER;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.MergeUtil.merge;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.HovedYtelseVM.HovedYtelseComparator.HOVEDYTELSE_NAVN;


public class OppsummeringVM implements Serializable {

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
        this.hovedytelser = transformer(utbetalinger);
    }

    /**
     * Slå sammen alle ytelsene i utbetalinger når de har samme hovedytelse og underytelse-tittel
     */
    private static List<HovedYtelseVM> transformer(List<Utbetaling> utbetalinger) {
        Map<String, List<Utbetaling>> map = on(utbetalinger).reduce(indexBy(HOVEDYTELSE));

        List<HovedYtelseVM> hovedYtelseVMs = new ArrayList<>();
        for (Map.Entry<String, List<Utbetaling>> entry : map.entrySet()) {
            List<Mergeable<Underytelse>> underytelser = on(entry.getValue()).flatmap(UNDERYTELSER).collectIn(new ArrayList<Mergeable<Underytelse>>());
            List<Underytelse> sammenlagteUnderytelser = merge(underytelser, MERGEABLE_TITTEL, MERGEABLE_TITTEL);
            hovedYtelseVMs.add(new HovedYtelseVM(entry.getKey(), sammenlagteUnderytelser));
        }
        sort(hovedYtelseVMs, HOVEDYTELSE_NAVN);
        return hovedYtelseVMs;
    }

    public String getOppsummertPeriode() {
        if (startDato.getMonthOfYear() == sluttDato.getMonthOfYear()
                && startDato.getYear() == sluttDato.getYear()) {
            return startDato.toString("MMMM yyyy", Locale.getDefault());
        }
        return KORT.transform(startDato.toDateTimeAtStartOfDay()) + " - " + KORT.transform(sluttDato.toDateTime(new LocalTime(23, 59)));
    }
}
