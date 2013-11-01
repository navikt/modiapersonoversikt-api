package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.AktorPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktorPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.ConfigUtil.setUseMock;

@Configuration
public class AktorEndpointConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AktorEndpointConfig.class);
    @Value("${aktor.url}")
    private URL aktorEndpoint;

    private boolean useMock;
    private AktorPortTypeImpl portType = new AktorPortTypeImpl(aktorEndpoint);
    private AktorPortTypeMock portTypeMock = new AktorPortTypeMock();

    public AktorEndpointConfig() {
        useMock = setUseMock("start.aktor.withintegration", LOG);
    }

    @Bean
    public AktoerPortType aktorPortType() {
        if (useMock) {
            return portTypeMock.aktorPortType();
        }
        return portType.aktorPortType();
    }

    @Bean
    public Pingable aktorIdPing() {
        if (useMock) {
            return new MockPingable("AktorEndpointConfig");
        }
        return portType.aktorIdPing();
    }

//    private AktoerPortType getAktoerPortType(final AktoerPortType aktoerPortType) {
//        return new AktoerPortType() {
//
//            @Override
//            @Cacheable("aktoridCache")
//            public HentAktoerIdForIdentResponse hentAktoerIdForIdent(@WebParam(name = "request", targetNamespace = "") HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest) throws HentAktoerIdForIdentPersonIkkeFunnet {
//                return aktoerPortType.hentAktoerIdForIdent(hentAktoerIdForIdentRequest);
//            }
//
//            @Override
//            public void ping() {
//                aktoerPortType.ping();
//            }
//        };
//    }

}
