package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash;

import no.finn.unleash.Unleash;
import no.finn.unleash.repository.FeatureToggleResponse;
import no.finn.unleash.repository.ToggleFetcher;
import no.nav.common.health.selftest.SelfTestCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static no.finn.unleash.repository.FeatureToggleResponse.Status.NOT_CHANGED;
import static no.finn.unleash.repository.FeatureToggleResponse.Status.UNAVAILABLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class UnleashServiceImplTest {

    @Mock
    private ToggleFetcher toggleFetcher;
    @Mock
    private Unleash unleash;

    private String api = "www.unleashurl.com";
    private UnleashService unleashService;

    @BeforeEach
    void init() {
        initMocks(this);
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
        when(toggleFetcher.fetchToggles()).thenReturn(new FeatureToggleResponse(NOT_CHANGED, 200));

        unleashService.ping().getCheck().checkHealth();
        SelfTestCheck pingResult = unleashService.ping();

        verify(toggleFetcher, times(1)).fetchToggles();
        assertThat(pingResult.getCheck().checkHealth().isHealthy(), is(true));
    }

    @Test
    void pingUnavailable() {
        when(toggleFetcher.fetchToggles()).thenReturn(new FeatureToggleResponse(UNAVAILABLE, 200));

        unleashService.ping().getCheck().checkHealth();
        SelfTestCheck pingResult = unleashService.ping();

        verify(toggleFetcher, times(1)).fetchToggles();
        assertThat(pingResult.getCheck().checkHealth().isHealthy(), is(false));
    }

}
