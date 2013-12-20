package no.nav.sbl.dialogarena.utbetaling.config;

import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.getWsUtbetalinger;

import javax.jws.WebParam;

import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtbetalingPortTypeStubConfig {

    private static final String FNR = "***REMOVED***";

    @Bean
    public UtbetalingPortType utbetalingPortType() {
        return new UtbetalingPortType() {
            @Override
            public WSHentUtbetalingListeResponse hentUtbetalingListe(@WebParam(name = "request", targetNamespace = "") WSHentUtbetalingListeRequest request) throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig, HentUtbetalingListeUgyldigDato {
                return new WSHentUtbetalingListeResponse().withUtbetalingListe(getWsUtbetalinger(FNR, request.getPeriode().getFom(), request.getPeriode().getTom()));
            }
        };
    }

}
