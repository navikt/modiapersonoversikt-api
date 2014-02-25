package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class UtbetalingsResultat implements Serializable {

    public final String fnr;
    public final Set<Interval> intervaller;
    public List<Utbetaling> utbetalinger;

    public UtbetalingsResultat(String fnr, LocalDate startDato, LocalDate sluttDato, List<Utbetaling> utbetalinger) {
        this.fnr = fnr;
        this.intervaller = new HashSet<>(asList(new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateTimeAtStartOfDay())));
        this.utbetalinger = utbetalinger;
    }
}
