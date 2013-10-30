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
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.joda.time.DateTime.now;

public class UtbetalingService {

    @Inject
    private UtbetalingPortType utbetalingPortType;

    public List<Utbetaling> hentUtbetalinger(String fnr) {
        return transformUtbetalinger(getWSUtbetalinger(fnr));
    }

    protected List<Utbetaling> transformUtbetalinger(List<WSUtbetaling> wsUtbetalinger) {
        List<Utbetaling> utbetalinger = new ArrayList<>();
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            utbetalinger.add(new Utbetaling(wsUtbetaling));
        }
        return utbetalinger;
    }

    protected List<WSUtbetaling> getWSUtbetalinger(String fnr) {
        try {
            return utbetalingPortType.hentUtbetalingListe(createRequest(fnr)).getUtbetalingListe();
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

    protected WSHentUtbetalingListeRequest createRequest(String fnr) {
        return new WSHentUtbetalingListeRequest()
                .withMottaker(fnr)
                .withPeriode(new WSPeriode().withFom(now().minusMonths(3)).withTom(now()));
    }

}
