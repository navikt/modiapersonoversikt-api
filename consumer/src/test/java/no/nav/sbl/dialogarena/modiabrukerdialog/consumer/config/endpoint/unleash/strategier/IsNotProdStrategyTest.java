package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.strategier;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.strategier.IsNotProdStrategy;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static no.nav.sbl.dialogarena.test.SystemProperties.setTemporaryProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class IsNotProdStrategyTest {

    IsNotProdStrategy strategy = new IsNotProdStrategy();
    private final String ENVIRONMENT_PROPERTY = "environment.name";

    @Test
    void setIsNotProdStrategyTest(){
        assertIsEnabled("p", false);
        assertIsEnabled("t1", true);
        assertIsEnabled("q0", true);
        assertIsEnabled("u10", true);
        assertIsEnabled("local", true);
        assertIsEnabled("", true);
        assertIsEnabled(null, true);
    }

    private void assertIsEnabled(String actualEnvironment, boolean expectedStatus){
        setTemporaryProperty(ENVIRONMENT_PROPERTY, actualEnvironment, () -> {
            HashMap<String, String > params = new HashMap<>();
            assertThat(strategy.isEnabled(params), is(expectedStatus));
        });
    }

}