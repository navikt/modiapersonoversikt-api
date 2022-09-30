package no.nav.modiapersonoversikt.service.unleash.strategier;

import no.finn.unleash.UnleashContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static no.nav.modiapersonoversikt.service.unleash.strategier.ByEnhetStrategy.ENHETER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ByEnhetStrategyTest {

    private final ByEnhetStrategy byEnhetStrategy = new ByEnhetStrategy();
    private final UnleashContext context = mock(UnleashContext.class);

    @Test
    void enhetTest() {
        assertEnabled("0118", "0118");
        assertEnabled("0118", "1234,0118,0000");
        assertEnabled("0118,1234,1111", "1234");
        assertEnabled("0118,1234,1111", "0118,1234,1111");
        assertDisabled("0118", "1234, 4455");
        assertDisabled("0118", "");
        assertDisabled("0118", null);
        assertDisabled("", "1234");
        assertDisabled(null, "1234");
        assertDisabled("", "");
        assertDisabled(null, null);
        assertDisabled("", ",,,");
        assertDisabled(",,,", ",,,");
    }

    @Test
    void kallMedIngenContextGirFalse() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put(ENHETER, "0118");
        }};

        assertThat(byEnhetStrategy.isEnabled(parameters), is(false));
    }

    private void assertEnabled(String enheter, String parameter) {
        addEnhetToContextProperties(enheter);

        assertEnhet(parameter, true);
    }

    private void assertDisabled(String enheter, String parameter) {
        addEnhetToContextProperties(enheter);

        assertEnhet(parameter, false);
    }

    private void addEnhetToContextProperties(String enheter) {
        HashMap<String, String> props = new HashMap<String, String>() {{
            put(ENHETER, enheter);
        }};
        when(context.getProperties()).thenReturn(props);
    }

    private void assertEnhet(String parameter, boolean expected) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("valgtEnhet", parameter);
        }};
        assertThat(byEnhetStrategy.isEnabled(params, context), is(expected));
    }
}