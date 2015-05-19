package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.kodeverk;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.common.kodeverk.CachingKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.DefaultKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.metrics.TimingMetricsProxy.createMetricsProxy;
import static org.apache.cxf.ws.security.SecurityConstants.MUST_UNDERSTAND;

@Configuration
public class KodeverkV2EndpointConfig {

    public static final String KODEVERK_KEY = "start.kodeverk.withmock";

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        KodeverkPortType prod = createMetricsProxy(lagKodeverkPortType(), KodeverkPortType.class);
        KodeverkPortType mock = KodeverkV2PortTypeMock.kodeverkPortType();
        
        return createSwitcher(prod, mock, KODEVERK_KEY, KodeverkPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        KodeverkClient prod = lagKodeverkClient();
        KodeverkClient mock = KodeverkV2PortTypeMock.kodeverkClient();
        return createSwitcher(prod, mock, KODEVERK_KEY, KodeverkClient.class);
    }

    @Bean
    public Pingable pingKodeverk(final KodeverkPortType ws) {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = currentTimeMillis();
                String name = "KODEVERK_V2";
                try {
                    ws.ping();
                    return asList(new PingResult(name, SERVICE_OK, currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, currentTimeMillis() - start));
                }
            }
        };
    }

    private KodeverkPortType lagKodeverkPortType() {
        return new CXFClient<>(KodeverkPortType.class)
                .wsdl("classpath:kodeverk/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl")
                .address(System.getProperty("kodeverkendpoint.v2.url"))
                .setProperty(MUST_UNDERSTAND, false)
                .build();
    }

    private KodeverkClient lagKodeverkClient() {
        return new CachingKodeverkClient(new DefaultKodeverkClient(kodeverkPortType()), new Optional.None<File>());
    }

}
