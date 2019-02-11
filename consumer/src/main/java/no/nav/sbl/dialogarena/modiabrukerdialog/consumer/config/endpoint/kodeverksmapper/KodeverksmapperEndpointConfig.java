package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper;

import no.nav.modig.modia.ping.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Timer;
import no.nav.sbl.util.EnvironmentUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class KodeverksmapperEndpointConfig {

    public static final String KODEVERKSMAPPER_KEY = "start.kodeverksmapper.withmock";

    @Bean
    public Kodeverksmapper kodeverksmapper() {
        final Kodeverksmapper kodeverksmapper = lagEndpoint();
        final Kodeverksmapper kodeverksmapperMock = lagMockEnpoint();

        return createMetricsProxyWithInstanceSwitcher("kodeverksmapper", kodeverksmapper,
                kodeverksmapperMock, KODEVERKSMAPPER_KEY, Kodeverksmapper.class);
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
                return getRequiredProperty("kodeverksmapper.ping.url");
            }
        };
    }

    private Kodeverksmapper lagEndpoint() {
        return new KodeverksmapperEndpoint(getRequiredProperty("kodeverksmapper.oppgavetype.url"),
                getRequiredProperty("kodeverksmapper.underkategori.url"),
                getRequiredProperty("kodeverksmapper.ping.url")
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
