package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.journal.v1.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock.getJournalPortTypeMock;

@Configuration
public class JoarkEndpointConfig {

    public static final String JOARK_KEY = "start.joark.withmock";

    private CXFClient<Journal_v1PortType> createJoarkPortType() {
        return new CXFClient<>(Journal_v1PortType.class)
                .address(System.getProperty("joark.ws.url"))
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .withHandler(new MDCOutHandler())
                .wsdl("classpath:joark/no/nav/tjeneste/virksomhet/journal/v1/journal.wsdl");
    }

    @Bean(name ="joarkPortType" )
    public Journal_v1PortType joarkPortType() throws HentJournalpostSikkerhetsbegrensning, HentJournalpostJournalpostIkkeFunnet, HentDokumentURLDokumentIkkeFunnet, HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet {
        final Journal_v1PortType prod = createJoarkPortType().build();
        return createSwitcher(
                prod,
                getJournalPortTypeMock(),
                JOARK_KEY,
                Journal_v1PortType.class
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
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, currentTimeMillis() - start));
                }
            }
        };
    }
}
