package no.nav.modiapersonoversikt.service.unleash;

import io.getunleash.Unleash;
import io.getunleash.repository.ClientFeaturesResponse;
import io.getunleash.repository.HttpFeatureFetcher;
import no.nav.common.health.selftest.SelfTestCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static io.getunleash.repository.FeatureToggleResponse.Status.NOT_CHANGED;
import static io.getunleash.repository.FeatureToggleResponse.Status.UNAVAILABLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UnleashServiceImplTest {

    @Mock
    private HttpFeatureFetcher toggleFetcher;
    @Mock
    private Unleash unleash;

    private final String api = "www.unleashurl.com";
    private UnleashService unleashService;

    @BeforeEach
    void init() throws Exception {
        openMocks(this).close();
        unleashService = new UnleashServiceImpl(toggleFetcher, unleash, api);
    }

    @Test
    void isEnabled() {
        when(unleash.isEnabled(Feature.SAMPLE_FEATURE.getPropertyKey())).thenReturn(true);

        boolean enabled = unleashService.isEnabled(Feature.SAMPLE_FEATURE);

        verify(unleash, times(1)).isEnabled(any());
        assertTrue(enabled);
    }

    @Test
    void isDisabled() {
        when(unleash.isEnabled(Feature.SAMPLE_FEATURE.getPropertyKey())).thenReturn(false);

        boolean enabled = unleashService.isEnabled(Feature.SAMPLE_FEATURE);

        verify(unleash, times(1)).isEnabled(any());
        assertFalse(enabled);
    }

    @Test
    void pingHappyCase() {
        when(toggleFetcher.fetchFeatures()).thenReturn(new ClientFeaturesResponse(NOT_CHANGED, 200));

        unleashService.ping().getCheck().checkHealth();
        SelfTestCheck pingResult = unleashService.ping();

        verify(toggleFetcher, times(1)).fetchFeatures();
        assertThat(pingResult.getCheck().checkHealth().isHealthy(), is(true));
    }

    @Test
    void pingUnavailable() {
        when(toggleFetcher.fetchFeatures()).thenReturn(new ClientFeaturesResponse(UNAVAILABLE, 200));

        unleashService.ping().getCheck().checkHealth();
        SelfTestCheck pingResult = unleashService.ping();

        verify(toggleFetcher, times(1)).fetchFeatures();
        assertThat(pingResult.getCheck().checkHealth().isHealthy(), is(false));
    }

}
