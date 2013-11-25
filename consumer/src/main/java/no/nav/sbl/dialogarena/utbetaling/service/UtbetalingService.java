package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSPeriode;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UtbetalingService {

    @Inject
    private no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling utbetaling;

    public List<Utbetaling> hentUtbetalinger(String fnr, DateTime startDato, DateTime sluttDato) {
        return transformUtbetalinger(getWSUtbetalinger(fnr, startDato, sluttDato));
    }

    private List<Utbetaling> transformUtbetalinger(List<WSUtbetaling> wsUtbetalinger) {
        List<Utbetaling> utbetalinger = new ArrayList<>();
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            utbetalinger.add(new Utbetaling(wsUtbetaling));
        }
        return utbetalinger;
    }

    private List<WSUtbetaling> getWSUtbetalinger(String fnr, DateTime startDato, DateTime sluttDato) {
        try {
            return utbetaling.hentUtbetalingListe(createRequest(fnr, startDato, sluttDato)).getUtbetalingListe();
        } catch (HentUtbetalingListeMottakerIkkeFunnet hentUtbetalingListeMottakerIkkeFunnet) {
            throw new ApplicationException("Utbetalingservice : Mottaker ikke funnet", hentUtbetalingListeMottakerIkkeFunnet);
        } catch (HentUtbetalingListeForMangeForekomster hentUtbetalingListeForMangeForekomster) {
            throw new ApplicationException("Utbetalingservice : For mange forekomster", hentUtbetalingListeForMangeForekomster);
        } catch (HentUtbetalingListeBaksystemIkkeTilgjengelig hentUtbetalingListeBaksystemIkkeTilgjengelig) {
            throw new ApplicationException("Utbetalingservice : Baksystem ikke tilgjengelig", hentUtbetalingListeBaksystemIkkeTilgjengelig);
        } catch (HentUtbetalingListeUgyldigDato hentUtbetalingListeUgyldigDato) {
            throw new ApplicationException("Utbetalingservice : Ugyldig dato", hentUtbetalingListeUgyldigDato);
        } catch (Exception e) {
            throw new ApplicationException("Utbetalingservice : Ukjent feil oppsto", e);
        }
    }

    private WSHentUtbetalingListeRequest createRequest(String fnr, DateTime startDato, DateTime sluttDato) {
        return new WSHentUtbetalingListeRequest()
                .withMottaker(fnr)
                .withPeriode(new WSPeriode().withFom(startDato).withTom(sluttDato));
    }

}
