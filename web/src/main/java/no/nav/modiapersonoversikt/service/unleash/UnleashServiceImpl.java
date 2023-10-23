package no.nav.modiapersonoversikt.service.unleash;

import io.getunleash.Unleash;
import io.getunleash.repository.ClientFeaturesResponse;
import io.getunleash.repository.FeatureToggleResponse;
import io.getunleash.repository.HttpFeatureFetcher;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable;
import org.slf4j.Logger;

import java.net.ConnectException;

import static io.getunleash.repository.FeatureToggleResponse.Status.UNAVAILABLE;
import static org.slf4j.LoggerFactory.getLogger;

public class UnleashServiceImpl implements UnleashService {

    private static final Logger log = getLogger(UnleashServiceImpl.class);

    private final Unleash defaultUnleash;
    private final HttpFeatureFetcher toggleFetcher;
    private final ConsumerPingable pingDelegate;

    String api;

    public UnleashServiceImpl(HttpFeatureFetcher toggleFetcher, Unleash defaultUnleash, String api) {
        this.toggleFetcher = toggleFetcher;
        this.defaultUnleash = defaultUnleash;
        this.api = api;

        this.pingDelegate = new ConsumerPingable("Unleash", false, () -> {
            ClientFeaturesResponse featuresResponse = this.toggleFetcher.fetchFeatures();
            FeatureToggleResponse.Status status = featuresResponse.getStatus();
            if (status.equals(UNAVAILABLE)) {
                throw new ConnectException("Ping mot Unleash på " + api + ". Ga status " + status);
            }
        });
    }

    @Override
    public boolean isEnabled(Feature feature) {
        return defaultUnleash.isEnabled(feature.getPropertyKey());
    }

    @Override
    public boolean isEnabled(String feature) {
        return defaultUnleash.isEnabled(feature);
    }

    @Override
    public SelfTestCheck ping() {
        return pingDelegate.ping();
    }
}
