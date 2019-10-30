package no.nav.sykmeldingsperioder.consumer.utbetalinger;


import no.nav.sykmeldingsperioder.domain.utbetalinger.Hovedytelse;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

import static no.nav.sykmeldingsperioder.consumer.utbetalinger.Transformers.TO_HOVEDYTELSE;
import static no.nav.sykmeldingsperioder.consumer.utbetalinger.UtbetalingUtils.utbetalingInnenforSokeperioden;
import static no.nav.sykmeldingsperioder.consumer.utbetalinger.UtbetalingerMockFactory.getWsUtbetalinger;

public class UtbetalingerMockService implements UtbetalingerService {

    @Override
    public List<Hovedytelse> hentUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato, String utbetalingstype) {
        return getWsUtbetalinger(fnr, startDato.toDateTimeAtStartOfDay(), sluttDato.toDateTimeAtStartOfDay()).stream()
                .filter(utbetaling -> utbetalingInnenforSokeperioden(utbetaling, startDato, sluttDato))
                .map(TO_HOVEDYTELSE)
                .filter(UtbetalingUtils::harGyldigUtbetaling)
                .collect(Collectors.toList());
    }

    @Override
    public void ping() {
    }
}
