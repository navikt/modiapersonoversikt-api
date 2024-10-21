package no.nav.modiapersonoversikt.service.unleash;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.UnleashContextProvider;
import io.getunleash.repository.HttpFeatureFetcher;
import io.getunleash.strategy.Strategy;
import io.getunleash.util.UnleashConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.service.unleash.strategier.ByEnhetStrategy;
import no.nav.modiapersonoversikt.service.unleash.strategier.ByEnvironmentStrategy;
import no.nav.modiapersonoversikt.service.unleash.strategier.IsNotProdStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class UnleashEndpointConfig {
    String api = EnvironmentUtils.getRequiredProperty("UNLEASH_SERVER_API_URL") + "/api";
    String apiToken = EnvironmentUtils.getRequiredProperty("UNLEASH_SERVER_API_TOKEN");

    @Bean
    public UnleashService unleashService(HttpFeatureFetcher featureFetcher, Unleash defaultUnleash) {
        return new UnleashServiceImpl(featureFetcher, defaultUnleash, api);
    }

    @Bean
    public HttpFeatureFetcher unleashHttpToggleFetcher(UnleashConfig unleashConfig) {
        return new HttpFeatureFetcher(unleashConfig);
    }

    @Bean
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
    public UnleashConfig unleashConfig(UnleashContextProvider unleashContextProvider) {
        return UnleashConfig.builder()
                .appName("modiapersonoversikt-api")
                .environment(System.getProperty("UNLEASH_ENVIRONMENT"))
                .instanceId(System.getProperty("APP_ENVIRONMENT_NAME", "local"))
                .unleashAPI(api)
                .apiKey(apiToken)
                .unleashContextProvider(unleashContextProvider)
                .synchronousFetchOnInitialisation(true)
                .build();
    }

    @Bean
    public UnleashContextProvider unleashContextProvider() {
        return new UnleashContextProviderImpl();
    }

}
