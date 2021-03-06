package no.nav.modiapersonoversikt.legacy.utbetaling.service;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.joda.time.LocalDate;

import java.util.List;

public interface UtbetalingService {
    List<WSUtbetaling> hentWSUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato);

    void ping();
}
