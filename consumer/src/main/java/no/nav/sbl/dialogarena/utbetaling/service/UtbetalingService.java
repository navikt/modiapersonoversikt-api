package no.nav.sbl.dialogarena.utbetaling.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class UtbetalingService {

    @Inject
    private UtbetalingPortType utbetalingPortType;

    public List<Utbetaling> hentUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return transformUtbetalinger(getWSUtbetalinger(fnr, startDato, sluttDato), fnr);
    }

    private List<Utbetaling> transformUtbetalinger(List<WSUtbetaling> wsUtbetalinger, String fnr) {
        List<Utbetaling> utbetalinger = new ArrayList<>();
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            utbetalinger.add(new Utbetaling(fnr, wsUtbetaling));
        }
        return utbetalinger;
    }

    private List<WSUtbetaling> getWSUtbetalinger(String fnr, LocalDate startDato, LocalDate sluttDato) {
        try {
            return utbetalingPortType.hentUtbetalingListe(createRequest(fnr, startDato, sluttDato)).getUtbetalingListe();
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

    private WSHentUtbetalingListeRequest createRequest(String fnr, LocalDate startDato, LocalDate sluttDato) {
        return new WSHentUtbetalingListeRequest()
                .withMottaker(fnr)
                .withPeriode(new WSPeriode().withFom(startDato.toDateTimeAtStartOfDay()).withTom(sluttDato.toDateTime(new LocalTime(23, 59))));
    }

}
