package no.nav.modiapersonoversikt.service.unleash;

import no.finn.unleash.Unleash;
import no.finn.unleash.repository.FeatureToggleResponse;
import no.finn.unleash.repository.ToggleFetcher;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable;
import org.slf4j.Logger;

import java.net.ConnectException;

import static no.finn.unleash.repository.FeatureToggleResponse.Status.UNAVAILABLE;
import static org.slf4j.LoggerFactory.getLogger;

public class UnleashServiceImpl implements UnleashService {

    private static final Logger log = getLogger(UnleashServiceImpl.class);

    private final Unleash defaultUnleash;
    private final ToggleFetcher toggleFetcher;
    private final ConsumerPingable pingDelegate;

    String api;

    public UnleashServiceImpl(ToggleFetcher toggleFetcher, Unleash defaultUnleash, String api) {
        this.toggleFetcher = toggleFetcher;
        this.defaultUnleash = defaultUnleash;
        this.api = api;

        this.pingDelegate = new ConsumerPingable("Unleash", false, () -> {
            FeatureToggleResponse featureToggleResponse = this.toggleFetcher.fetchToggles();
            FeatureToggleResponse.Status status = featureToggleResponse.getStatus();
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
