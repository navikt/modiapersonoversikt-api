package no.nav.modiapersonoversikt.service.unleash.strategier;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static no.nav.common.test.SystemProperties.setTemporaryProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ByEnvironmentStrategyTest {


    private final ByEnvironmentStrategy strategy = new ByEnvironmentStrategy();
    private final String ENVIRONMENT_PROPERTY = "APP_ENVIRONMENT_NAME";

    @Test
    void environmentTest() {
        assertThat(strategy.isEnabled(null), is(false));

        assertEnabled("t6", "t6");
        assertDisabled("p", "q0");
        assertDisabled(null, "q0");
        assertDisabled(null, "");
        assertDisabled("q0", null);
        assertDisabled("", null);
        assertDisabled("", "");
        assertDisabled("", ",,,");

        assertEnabled("q1", "q6,q1,t6");
    }

    private void assertEnabled(String actualEnvironment, String environmentParameter) {
        assertEnvironment(actualEnvironment, environmentParameter, true);
    }

    private void assertDisabled(String actualEnvironment, String environmentParameter) {
        assertEnvironment(actualEnvironment, environmentParameter, false);
    }

    private void assertEnvironment(String actualEnvironment, String toggleParameter, boolean expectedStatus){
        setTemporaryProperty(ENVIRONMENT_PROPERTY, actualEnvironment, () -> {
            HashMap<String, String > params = new HashMap<>() {{
                put("miljø", toggleParameter);
            }};
            assertThat(strategy.isEnabled(params), is(expectedStatus));
        });
    }
}
