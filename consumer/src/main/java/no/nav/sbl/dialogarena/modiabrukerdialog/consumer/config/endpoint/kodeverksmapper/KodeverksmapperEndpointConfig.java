package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper;

import no.nav.modig.modia.ping.FailedPingResult;
import no.nav.modig.modia.ping.OkPingResult;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Timer;
import no.nav.sbl.util.EnvironmentUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class KodeverksmapperEndpointConfig {

    @Bean
    public Kodeverksmapper kodeverksmapper() {
        final Kodeverksmapper kodeverksmapper = lagEndpoint();
        return createTimerProxyForWebService("kodeverksmapper", kodeverksmapper, Kodeverksmapper.class);
    }

    @Bean
    public Pingable kodeverksmapperPing() {
        return new Pingable() {
            @Override
            public PingResult ping() {
                Kodeverksmapper kodeverksmapper = lagEndpoint();
                Timer timer = Timer.lagOgStartTimer();
                try {
                    kodeverksmapper.ping();
                    return new OkPingResult(timer.stoppOgHentTid());
                } catch (IOException e) {
                    return new FailedPingResult(e, timer.stoppOgHentTid());
                }
            }

            @Override
            public String name() {
                return "Kodeverksmapper";
            }

            @Override
            public String method() {
                return "ping";
            }

            @Override
            public String endpoint() {
                return EnvironmentUtils.getRequiredProperty("KODEVERKSMAPPER_PING_URL");
            }
        };
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
            public Map<String, String> hentOppgavetype() throws IOException {
                Map<String, String> oppgavetyper = new HashMap<>();
                oppgavetyper.put("VUR_UFO", "VUR");
                return oppgavetyper;
            }

            @Override
            public Map<String, Behandling> hentUnderkategori() throws IOException {
                Map<String, Behandling> underkategorier = new HashMap<>();
                underkategorier.put("UTLAND_BID", new Behandling().withBehandlingstype("ae0106"));
                return underkategorier;
            }

            @Override
            public void ping() throws IOException {
            }
        };
    }
}
