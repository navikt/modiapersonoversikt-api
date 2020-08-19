package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.UnleashContextProvider;
import no.finn.unleash.repository.HttpToggleFetcher;
import no.finn.unleash.repository.ToggleFetcher;
import no.finn.unleash.strategy.Strategy;
import no.finn.unleash.util.UnleashConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashContextProviderImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnhetStrategy;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.IsNotProdStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class UnleashEndpointConfig {
    String api = EnvironmentUtils.getRequiredProperty("UNLEASH_API_URL");

    @Bean
    @Inject
    public UnleashService unleashService(ToggleFetcher toggleFetcher, Unleash defaultUnleash) {
        UnleashServiceImpl unleashService = new UnleashServiceImpl(toggleFetcher, defaultUnleash, api);
        return createTimerProxyForWebService("unleash", unleashService, UnleashService.class);
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
    public UnleashConfig unleashConfig(UnleashContextProvider unleashContextProvider) {
        return UnleashConfig.builder()
                .appName("modiabrukerdialog")
                .instanceId(System.getProperty("APP_ENVIRONMENT_NAME", "local"))
                .unleashAPI(api)
                .unleashContextProvider(unleashContextProvider)
                .build();
    }

    @Bean
    @Inject
    public UnleashContextProvider unleashContextProvider(AnsattService ansattService) {
        return new UnleashContextProviderImpl(ansattService);
    }

}
