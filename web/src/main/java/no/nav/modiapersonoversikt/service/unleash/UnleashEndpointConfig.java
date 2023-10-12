package no.nav.modiapersonoversikt.service.unleash;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.UnleashContextProvider;
import io.getunleash.repository.OkHttpFeatureFetcher;
import io.getunleash.strategy.Strategy;
import io.getunleash.util.UnleashConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import no.nav.modiapersonoversikt.service.unleash.strategier.ByEnhetStrategy;
import no.nav.modiapersonoversikt.service.unleash.strategier.ByEnvironmentStrategy;
import no.nav.modiapersonoversikt.service.unleash.strategier.IsNotProdStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class UnleashEndpointConfig {
    String api = EnvironmentUtils.getRequiredProperty("UNLEASH_SERVER_API_URL") + "/api";
    String apiToken = EnvironmentUtils.getRequiredProperty("UNLEASH_SERVER_API_TOKEN");

    @Bean
    @Autowired
    public UnleashService unleashService(OkHttpFeatureFetcher featureFetcher, Unleash defaultUnleash) {
        UnleashServiceImpl unleashService = new UnleashServiceImpl(featureFetcher, defaultUnleash, api);
        return createTimerProxyForWebService("unleash", unleashService, UnleashService.class);
    }

    @Bean
    @Autowired
    public OkHttpFeatureFetcher unleashHttpToggleFetcher(UnleashConfig unleashConfig) {
        return new OkHttpFeatureFetcher(unleashConfig);
    }

    @Bean
    @Autowired
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
    @Autowired
    public UnleashConfig unleashConfig(UnleashContextProvider unleashContextProvider) {
        return UnleashConfig.builder()
                .appName("modiapersonoversikt")
                .instanceId(System.getProperty("APP_ENVIRONMENT_NAME", "local"))
                .unleashAPI(api)
                .apiKey(apiToken)
                .unleashContextProvider(unleashContextProvider)
                .unleashFeatureFetcherFactory(OkHttpFeatureFetcher::new)
                .build();
    }

    @Bean
    @Autowired
    public UnleashContextProvider unleashContextProvider(AnsattService ansattService) {
        return new UnleashContextProviderImpl(ansattService);
    }

}
