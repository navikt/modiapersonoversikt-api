package no.nav.sbl.dialogarena.utbetaling.service;

import java.util.List;
import javax.inject.Inject;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSPeriode;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.apache.commons.collections15.Transformer;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import static no.nav.modig.lang.collections.IterUtils.on;

public class UtbetalingService {

    @Inject
    private UtbetalingPortType utbetalingPortType;

    public List<Utbetaling> hentUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        List<WSUtbetaling> wsUtbetalinger = getWSUtbetalinger(fnr, startDato, sluttDato);
        return on(wsUtbetalinger).map(tilUtbetaling(fnr)).collect();
    }

    private static Transformer<WSUtbetaling, Utbetaling> tilUtbetaling(final String fnr) {
        return new Transformer<WSUtbetaling, Utbetaling>() {
            @Override
            public Utbetaling transform(WSUtbetaling wsUtbetaling) {
                return Utbetaling.getBuilder().createUtbetaling();
            }
        };
    }

    private List<WSUtbetaling> getWSUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        try {
            return utbetalingPortType.hentUtbetalingListe(createRequest(fnr, startDato, sluttDato)).getUtbetalingListe();
        } catch (Exception e) {
            throw new ApplicationException("Henting av utbetalinger for bruker med fnr " + fnr + " mellom " + startDato + " og " + sluttDato + " feilet.", e);
        }
    }

    private WSHentUtbetalingListeRequest createRequest(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return new WSHentUtbetalingListeRequest()
                .withMottaker(fnr)
                .withPeriode(new WSPeriode().withFom(startDato.toDateTimeAtStartOfDay()).withTom(sluttDato.toDateTime(new LocalTime(23, 59))));
    }

}
