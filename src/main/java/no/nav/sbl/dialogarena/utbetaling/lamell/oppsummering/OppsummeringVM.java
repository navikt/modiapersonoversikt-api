package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.HovedYtelseVM.NAVN;


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
        this.hovedytelser = on(utbetalinger).map(TIL_HOVEDYTELSE).collect(compareWith(NAVN));
    }

    public String getOppsummertPeriode() {
        if (startDato.getMonthOfYear() == sluttDato.getMonthOfYear()) {
            return startDato.toString("MMMM", Locale.getDefault());
        }
        return KORT.transform(startDato.toDateTimeAtStartOfDay()) + " - " + KORT.transform(sluttDato.toDateMidnight().toDateTime());
    }

    private static final Transformer<Utbetaling, HovedYtelseVM> TIL_HOVEDYTELSE = new Transformer<Utbetaling, HovedYtelseVM>() {
        @Override
        public HovedYtelseVM transform(Utbetaling utbetaling) {
            return new HovedYtelseVM(utbetaling.getHovedytelse(), utbetaling.getUnderytelser());
        }
    };

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
