package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash;

import no.finn.unleash.Unleash;
import no.finn.unleash.repository.FeatureToggleResponse;
import no.finn.unleash.repository.ToggleFetcher;
import no.nav.modig.modia.ping.FailedPingResult;
import no.nav.modig.modia.ping.OkPingResult;
import no.nav.modig.modia.ping.PingResult;
import org.slf4j.Logger;

import java.net.ConnectException;

import static no.finn.unleash.repository.FeatureToggleResponse.Status.CHANGED;
import static no.finn.unleash.repository.FeatureToggleResponse.Status.NOT_CHANGED;
import static org.slf4j.LoggerFactory.getLogger;

public class UnleashServiceImpl implements UnleashService {

    private static final Logger log = getLogger(UnleashServiceImpl.class);

    private Unleash defaultUnleash;
    private ToggleFetcher toggleFetcher;

    String api;

    public UnleashServiceImpl(ToggleFetcher toggleFetcher, Unleash defaultUnleash, String api) {
        this.toggleFetcher = toggleFetcher;
        this.defaultUnleash = defaultUnleash;
        this.api = api;
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
    public PingResult ping() {
        long start = System.currentTimeMillis();
        try {
            FeatureToggleResponse featureToggleResponse = this.toggleFetcher.fetchToggles();
            FeatureToggleResponse.Status status = featureToggleResponse.getStatus();
            if (status == CHANGED || status == NOT_CHANGED) {
                return new OkPingResult(System.currentTimeMillis() - start);
            } else {
                return new FailedPingResult(new ConnectException("Ping mot Unleash på " + api + ". Ga status " + status), System.currentTimeMillis() - start);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return new FailedPingResult(e, System.currentTimeMillis() - start);
        }
    }

    @Override
    public String name() {
        return "Unleash";
    }

    @Override
    public String method() {
        return "fetchToggles";
    }

    @Override
    public String endpoint() {
        return api;
    }
}
