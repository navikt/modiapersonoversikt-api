package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.UnleashContextProvider;
import no.finn.unleash.repository.HttpToggleFetcher;
import no.finn.unleash.repository.ToggleFetcher;
import no.finn.unleash.strategy.Strategy;
import no.finn.unleash.util.UnleashConfig;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnhetStrategy;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.IsNotProdStrategy;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashContextProviderImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashServiceMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class UnleashEndpointConfig {
    private static final String MOCK_KEY = "unleash.withmock";
    String api = System.getProperty("unleash.url");

    @Bean
    @Inject
    public UnleashService unleashService(ToggleFetcher toggleFetcher, Unleash defaultUnleash) {
        return createMetricsProxyWithInstanceSwitcher(
                "unleash",
                new UnleashServiceImpl(toggleFetcher, defaultUnleash, api),
                new UnleashServiceMock(),
                MOCK_KEY,
                UnleashService.class);
    }

    @Bean
    @Inject
    public ToggleFetcher unleashHttpToggleFetcher(UnleashConfig unleashConfig) {
        return new HttpToggleFetcher(unleashConfig);
    }

    @Bean
    @Inject
    public Unleash defaultUnleash(UnleashConfig unleashConfig) {
        return new DefaultUnleash(unleashConfig, addStrategies());
    }

    private Strategy[] addStrategies() {
        List<Strategy> list = new ArrayList<>(Arrays.asList(
                new ByEnvironmentStrategy(),
                new IsNotProdStrategy(),
                new ByEnhetStrategy()
        ));
        return list.toArray(new Strategy[0]);
    }

    @Bean
    @Inject
    public UnleashConfig unleashConfig (UnleashContextProvider unleashContextProvider){
        return UnleashConfig.builder()
                .appName("modiabrukerdialog")
                .instanceId(System.getProperty("environment.name", "local"))
                .unleashAPI(api)
                .unleashContextProvider(unleashContextProvider)
                .build();
    }

    @Bean
    @Inject
    public UnleashContextProvider unleashContextProvider(AnsattService ansattService) {
        return new UnleashContextProviderImpl(SubjectHandler.getSubjectHandler(), ansattService);
    }

}
