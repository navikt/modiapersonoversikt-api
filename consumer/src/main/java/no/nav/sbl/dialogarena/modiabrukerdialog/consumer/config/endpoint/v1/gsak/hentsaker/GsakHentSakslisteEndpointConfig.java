package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakHentSakslistePortTypeMock.createGsakHentSakslisteMock;

@Configuration
public class GsakHentSakslisteEndpointConfig {

    public static final String GSAK_SAKSLISTE_KEY = "start.gsak.saksliste.withmock";

    @Bean
    public Sak sakEndpoint() {
        return createSwitcher(
                createEndpoint(),
                createGsakHentSakslisteMock(),
                GSAK_SAKSLISTE_KEY,
                Sak.class
        );
    }

    @Bean
    public Pingable gsakSakslistePing(final Sak ws) {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "GSAK_SAKSLISTE_V1";
                try {
                    ws.finnGenerellSakListe(new WSFinnGenerellSakListeRequest().withBrukerId("10108000398"));
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static Sak createEndpoint() {
        return new CXFClient<>(Sak.class)
                .address(System.getProperty("gsak.saksliste.v1.url"))
                .wsdl("classpath:no/nav/virksomhet/tjenester/sak.wsdl")
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }
}
