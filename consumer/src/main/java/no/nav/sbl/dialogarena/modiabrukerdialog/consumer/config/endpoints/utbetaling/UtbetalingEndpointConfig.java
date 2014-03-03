package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.jws.WebParam;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class UtbetalingEndpointConfig {

    public static final String UTBETALING_KEY = "start.utbetaling.withmock";

    @Inject
    @Qualifier("utbetalingPortTypeWrapperMock")
    private Wrapper<UtbetalingPortType> mockedPortTypeWrapper;

    @Inject
    @Qualifier("utbetalingPortTypeWrapper")
    private Wrapper<UtbetalingPortType> portTypeWrapper;

    @Bean(name = "utbetalingPortType")
    public UtbetalingPortType utbetalingPortType() {
        return new UtbetalingPortType() {

            @Cacheable("utbetalingCache")
            @Override
            public WSHentUtbetalingListeResponse hentUtbetalingListe(@WebParam(name = "request", targetNamespace = "") WSHentUtbetalingListeRequest request)
                    throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig, HentUtbetalingListeUgyldigDato {
                if (mockErTillattOgSlaattPaaForKey(UTBETALING_KEY)) {
                    return mockedPortTypeWrapper.wrappedObject.hentUtbetalingListe(request);
                }
                return portTypeWrapper.wrappedObject.hentUtbetalingListe(request);
            }
        };
    }

    @Bean
    public Pingable utbetalingPing() {
        return new MockPingable("UtbetalingEndpoint");
    }

}
