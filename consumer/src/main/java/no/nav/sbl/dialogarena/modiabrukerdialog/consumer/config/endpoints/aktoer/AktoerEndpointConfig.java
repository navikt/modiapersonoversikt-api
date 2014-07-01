package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.aktoer;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktoerPortTypeMock;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class AktoerEndpointConfig {

    public static final String AKTOER_KEY = "start.aktoer.withmock";

    @Bean
    public AktoerPortType aktoerPortType() {
        final AktoerPortType mock = new AktoerPortTypeMock().getAktoerPortTypeMock();
        final AktoerPortType prod = null;
        return new AktoerPortType() {
            @Override
            public HentAktoerIdForIdentResponse hentAktoerIdForIdent(@WebParam(name = "request", targetNamespace = "") HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest) throws HentAktoerIdForIdentPersonIkkeFunnet {
                if (mockErTillattOgSlaattPaaForKey(AKTOER_KEY)) {
                    return mock.hentAktoerIdForIdent(hentAktoerIdForIdentRequest);
                }
                return prod.hentAktoerIdForIdent(hentAktoerIdForIdentRequest);
            }

            @Override
            public void ping() {
                if (mockErTillattOgSlaattPaaForKey(AKTOER_KEY)) {
                    mock.ping();
                } else {
                    prod.ping();
                }
            }
        };
    }

}
