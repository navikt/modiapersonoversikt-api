package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoApiServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoApiServiceMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class OppfolgingsinfoApiEndpointConfig {
    private static final String MOCK_KEY = "veilarboppfolging.api.withmock";
    private String api = System.getProperty("veilarboppfolging.api.url");

    @Bean
    public OppfolgingsinfoApiService lagOppfølgingsapi() {
        return createMetricsProxyWithInstanceSwitcher(
                "veilarboppfolgingApi",
                new OppfolgingsinfoApiServiceImpl(api),
                new OppfolgingsinfoApiServiceMock(),
                MOCK_KEY,
                OppfolgingsinfoApiService.class
        );
    }

}
