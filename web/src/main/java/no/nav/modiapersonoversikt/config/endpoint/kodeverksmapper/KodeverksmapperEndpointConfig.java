package no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper;

import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable;
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class KodeverksmapperEndpointConfig {

    @Bean
    public Kodeverksmapper kodeverksmapper() {
        final Kodeverksmapper kodeverksmapper = lagEndpoint();
        return createTimerProxyForWebService("kodeverksmapper", kodeverksmapper, Kodeverksmapper.class);
    }

    @Bean
    public Pingable kodeverksmapperPing() {
        Kodeverksmapper endepunkt = lagEndpoint();
        return new ConsumerPingable(
                String.format("Koeverksmapper via %s", EnvironmentUtils.getRequiredProperty("KODEVERKSMAPPER_OPPGAVETYPE_URL")),
                false,
                endepunkt::ping
        );
    }

    private Kodeverksmapper lagEndpoint() {
        return new KodeverksmapperEndpoint(EnvironmentUtils.getRequiredProperty("KODEVERKSMAPPER_OPPGAVETYPE_URL"),
                EnvironmentUtils.getRequiredProperty("KODEVERKSMAPPER_UNDERKATEGORI_URL"),
                EnvironmentUtils.getRequiredProperty("KODEVERKSMAPPER_PING_URL")
        );
    }

    private Kodeverksmapper lagMockEnpoint() {
        return new Kodeverksmapper() {
            @Override
            public Map<String, String> hentOppgavetype() {
                Map<String, String> oppgavetyper = new HashMap<>();
                oppgavetyper.put("VUR_UFO", "VUR");
                return oppgavetyper;
            }

            @Override
            public Map<String, Behandling> hentUnderkategori() {
                Map<String, Behandling> underkategorier = new HashMap<>();
                underkategorier.put("UTLAND_BID", new Behandling().withBehandlingstype("ae0106"));
                return underkategorier;
            }

            @Override
            public void ping() {
            }
        };
    }
}
