package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.UtbetalingPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;

@Configuration
public class UtbetalingEndpointConfig {

    public static final String UTBETALING_KEY = "start.utbetaling.withmock";

    @Value("${utbetalingendpoint.v2.url}")
    private URL endpoint;

    @Bean
    public UtbetalingPortType utbetalingPortType() {
        final UtbetalingPortType portType = new UtbetalingPortTypeImpl(endpoint).utbetalingPortType();
        final UtbetalingPortType portTypeMock = new UtbetalingPortTypeMock().utbetalingPortType();
        return new UtbetalingPortType() {

            @Cacheable("endpointCache")
            @Override
            public WSHentUtbetalingListeResponse hentUtbetalingListe(@WebParam(name = "request", targetNamespace = "") WSHentUtbetalingListeRequest request) throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig, HentUtbetalingListeUgyldigDato {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(UTBETALING_KEY)) {
                    return portTypeMock.hentUtbetalingListe(request);
                }
                return portType.hentUtbetalingListe(request);
            }
        };
    }

    @Bean
    public Pingable utbetalingPing() {
        return new MockPingable("UtbetalingEndpoint");
    }

}
