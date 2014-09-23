package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.ruting;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSBrukersok;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakRequest;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakRutingPortTypeMock.createRutingPortTypeMock;

@Configuration
public class GsakRutingEndpointConfig {

    public static final String GSAK_RUTING_KEY = "start.gsak.ruting.withmock";


    @Bean
    public Ruting rutingPortType() {
        return createSwitcher(createRutingPortType(), createRutingPortTypeMock(), GSAK_RUTING_KEY, Ruting.class);
    }

    @Bean
    public Pingable rutingPing() {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "GSAK_RUTING_V1";
                try {
                    Ruting ws = createRutingPortType();
                    ws.finnAnsvarligEnhetForSak(new WSFinnAnsvarligEnhetForSakRequest().withBrukersok(new WSBrukersok().withBrukerId("10108000398").withFagomradeKode("DAG")));
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static Ruting createRutingPortType() {
        return new CXFClient<>(Ruting.class)
                .address(System.getProperty("gsak.ruting.v1.url"))
                .wsdl("classpath:ruting/no/nav/virksomhet/tjenester/ruting/ruting.wsdl")
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }
}
