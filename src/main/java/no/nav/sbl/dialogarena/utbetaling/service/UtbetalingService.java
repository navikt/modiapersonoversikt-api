package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import org.joda.time.LocalDate;

import java.util.List;

public interface UtbetalingService {
    List<Hovedytelse> hentUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato);

    void ping();
}
