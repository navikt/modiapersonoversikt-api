package no.nav.sbl.dialogarena.utbetaling.config;

import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;

import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.getWsUtbetalinger;

@Configuration
public class UtbetalingStubConfig {

    private static final String FNR = "12345612345";

    @Bean
    public no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling utbetaling() {
        return new no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling() {
            @Override
            public WSHentUtbetalingListeResponse hentUtbetalingListe(@WebParam(name = "request", targetNamespace = "") WSHentUtbetalingListeRequest request) throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig, HentUtbetalingListeUgyldigDato {
                return new WSHentUtbetalingListeResponse().withUtbetalingListe(getWsUtbetalinger(FNR));
            }


        };
    }

}
