package no.nav.sbl.dialogarena.sak.config;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        ModiaStubConfig.class,
        SaksoversiktServiceConfig.class
})
public class SaksoversiktServiceConfigTest {

    @Test
    public void shouldSetupAppContext() { }

}
