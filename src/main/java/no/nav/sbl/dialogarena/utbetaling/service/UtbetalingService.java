package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSPeriode;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.joda.time.LocalDate;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformer.lagUtbetalinger;
import static org.slf4j.LoggerFactory.getLogger;

public class UtbetalingService {

    private static final Logger logger = getLogger(UtbetalingService.class);

    @Inject
    private UtbetalingPortType utbetalingPortType;

    public List<Utbetaling> hentUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return lagUtbetalinger(getWSUtbetalinger(fnr, startDato, sluttDato), fnr);
    }

    private List<WSUtbetaling> getWSUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        try {
            logger.info("---- Spør etter utebetalinger. Fnr: {}. ----", fnr);
            return utbetalingPortType.hentUtbetalingListe(createRequest(fnr, startDato, sluttDato)).getUtbetalingListe();
        } catch (HentUtbetalingListeMottakerIkkeFunnet hulmif) {
            logger.debug("Mottaker med fnr {} ble ikke funnet i utbetalingstjenesten. Returnerer tom liste.", fnr);
            return emptyList();
        } catch (Exception e) {
            throw new ApplicationException("Henting av utbetalinger for bruker med fnr " + fnr + " mellom " + startDato + " og " + sluttDato + " feilet.", e);
        }
    }

    private WSHentUtbetalingListeRequest createRequest(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return new WSHentUtbetalingListeRequest()
                .withMottaker(fnr)
                .withPeriode(new WSPeriode().withFom(startDato.toDateTimeAtStartOfDay()).withTom(sluttDato.toDateTimeAtStartOfDay()));
    }

}
