package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.strategier;

import no.finn.unleash.UnleashContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ByEnhetStrategyTest {

    private ByEnhetStrategy byEnhetStrategy = new ByEnhetStrategy();
    private UnleashContext context = mock(UnleashContext.class);

    @Test
    void enhetTest() {
        assertEnabled("0118", "0118");
        assertEnabled("0118", "1234,0118,0000");
        assertDisabled("0118", "1234, 4455");
        assertDisabled("0118", "");
        assertDisabled("0118", null);
        assertDisabled("", "1234");
        assertDisabled(null, "1234");
        assertDisabled("", "");
        assertDisabled(null, null);
        assertDisabled("", ",,,");
    }

    @Test
    void kallMedIngenContextGirFalse() {
        HashMap<String, String> parameters = new HashMap<String, String>() {{
            put("valgtEnhet", "0118");
        }};

        assertThat(byEnhetStrategy.isEnabled(parameters), is(false));
    }

    private void assertEnabled(String valgtEnhet, String parameter) {
        addEnhetToContextProperties(valgtEnhet);

        assertEnhet(parameter, true);
    }

    private void assertDisabled(String valgtEnhet, String parameter) {
        addEnhetToContextProperties(valgtEnhet);

        assertEnhet(parameter, false);
    }

    private void addEnhetToContextProperties(String valgtEnhet) {
        HashMap<String, String> props = new HashMap<String, String>() {{
            put("valgtEnhet", valgtEnhet);
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