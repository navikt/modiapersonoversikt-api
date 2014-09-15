package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.aktor;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktoerPortTypeMock;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;


@Configuration
public class AktorEndpointConfig {

    public static final String AKTOER_KEY = "start.aktoer.withmock";

    @Value("${aktorid.ws.url}")
    private String aktoerUrl;

    private AktoerPortType aktoerPort() {
        return new CXFClient<>(AktoerPortType.class)
                .address(aktoerUrl)
                .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/aktoer/v1/Aktoer.wsdl")
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .withHandler(new MDCOutHandler())
                .build();
    }

    @Bean
    public AktoerPortType aktoerPortType() {
        final AktoerPortType mock = new AktoerPortTypeMock().getAktoerPortTypeMock();
        final AktoerPortType prod = aktoerPort();
        return new AktoerPortType() {
            @Override
            public HentAktoerIdForIdentResponse hentAktoerIdForIdent(@WebParam(name = "request", targetNamespace = "") HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest)
                    throws HentAktoerIdForIdentPersonIkkeFunnet {
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
