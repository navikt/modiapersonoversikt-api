package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonIkkeTilgang;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import org.joda.time.LocalDate;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.Transformers.TO_HOVEDYTELSE;
import static org.slf4j.LoggerFactory.getLogger;

public class UtbetalingService {

    private static final Logger logger = getLogger(UtbetalingService.class);

    @Inject
    private UtbetalingV1 utbetalingV1;

    public List<Record<Hovedytelse>> hentUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return on(getWSUtbetalinger(fnr, startDato, sluttDato)).flatmap(TO_HOVEDYTELSE).collect();
    }

    public void ping() {
        utbetalingV1.ping();
    }

    protected List<WSUtbetaling> getWSUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        logger.info("---- Spør etter utebetalinger. Fnr: {}. ----", fnr);
        try {
            return utbetalingV1.hentUtbetalingsinformasjon(createRequest(fnr, startDato, sluttDato)).getUtbetalingListe();
        } catch (HentUtbetalingsinformasjonPeriodeIkkeGyldig ex) {
            throw new ApplicationException("Utbetalingsperioden er ikke gyldig. ", ex);
        } catch (HentUtbetalingsinformasjonPersonIkkeFunnet ex) {
            throw new ApplicationException("Person ikke funnet. ", ex);
        } catch (HentUtbetalingsinformasjonIkkeTilgang ex) {
            throw new ApplicationException("Ikke tilgang. ", ex);
        } catch (Exception e) {
            throw new SystemException("Henting av utbetalinger for bruker med fnr " + fnr + " mellom " + startDato + " og " + sluttDato + " feilet.", e);
        }
    }

    protected WSHentUtbetalingsinformasjonRequest createRequest(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return new WSHentUtbetalingsinformasjonRequest()
            .withId(new WSIdent()
                    .withIdent(fnr)
                    .withIdentType(new WSIdenttyper().withValue("FNR"))
                    .withRolle(new WSIdentroller().withValue("RETTIGHETSHAVER")))
            .withPeriode(createPeriode(startDato, sluttDato));
    }


    protected WSForespurtPeriode createPeriode(LocalDate startDato, LocalDate sluttDato) {
        return new WSForespurtPeriode()
            .withFom(startDato.toDateTimeAtStartOfDay())
            .withTom(sluttDato.toDateTimeAtStartOfDay());
    }

}
