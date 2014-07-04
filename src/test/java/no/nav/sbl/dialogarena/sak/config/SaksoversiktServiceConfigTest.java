package no.nav.sbl.dialogarena.sak.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ModiaStubConfig.class,
        SaksoversiktServiceConfig.class
})
public class SaksoversiktServiceConfigTest {

    @Test
    public void shouldSetupAppContext() { }

}
