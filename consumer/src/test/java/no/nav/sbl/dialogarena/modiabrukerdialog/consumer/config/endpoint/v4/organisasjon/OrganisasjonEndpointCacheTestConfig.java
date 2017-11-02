package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v4.organisasjon;

import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static org.mockito.Mockito.mock;

@Configuration
public class OrganisasjonEndpointCacheTestConfig {

    @Bean
    public OrganisasjonV4 organisasjonV4PortType() {
        String key = "organisasjon-key";
        System.setProperty(key, "true");
        return createMetricsProxyWithInstanceSwitcher("", null, mock(OrganisasjonV4.class), key, OrganisasjonV4.class);
    }

}
