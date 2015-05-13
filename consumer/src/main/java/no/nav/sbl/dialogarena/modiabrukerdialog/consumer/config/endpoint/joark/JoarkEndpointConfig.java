package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.journal.v1.binding.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock.getJournalPortTypeMock;

@Configuration
public class JoarkEndpointConfig {

    @Value("${joark.ws.url}")
    private String joarkEndpoint;

    public static final String JOARK_KEY = "start.joark.withmock";

    private CXFClient<JournalV1> createJoarkPortType() {
        return new CXFClient<>(JournalV1.class)
                .address(joarkEndpoint)
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .withHandler(new MDCOutHandler());
        //TODO hvis man inkulder denne får man Could not find definition for service {http://nav.no/tjeneste/virksomhet/journal/v1}JournalV1Service.
        //TODO trolig fordi den heter JournalV1_Service
//                .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/journal/v1/journal.wsdl");
    }

    @Bean
    @Named("joarkPortType")
    public JournalV1 joarkPortType() throws HentJournalpostSikkerhetsbegrensning, HentJournalpostJournalpostIkkeFunnet, HentDokumentURLDokumentIkkeFunnet, HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        final JournalV1 prod = createJoarkPortType().build();
        return createSwitcher(
                prod,
                getJournalPortTypeMock(),
                JOARK_KEY,
                JournalV1.class
        );
    }

    @Bean
    public Pingable pingJoark() {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {

                long start = currentTimeMillis();
                String name = "JOARK";
                try {
                    joarkPortType().ping();
                    return asList(new PingResult(name, SERVICE_OK, currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, currentTimeMillis() - start));
                }
            }
        };
    }
}
