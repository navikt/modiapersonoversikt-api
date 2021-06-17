package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.utbetalinger;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.utbetalinger.Hovedytelse;
import org.joda.time.LocalDate;

import java.util.List;

public interface UtbetalingerService {
    List<Hovedytelse> hentUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato, String utbetalingstype);
    void ping();
}
