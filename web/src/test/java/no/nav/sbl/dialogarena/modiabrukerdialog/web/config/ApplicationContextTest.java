package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContext.class)
public class ApplicationContextTest extends TestSecurityBaseClass {

    static {
        System.setProperty("tjenestebuss.url", "http://changeme");
        System.setProperty("ctjenestebuss.username", "me");
        System.setProperty("ctjenestebuss.password", "secret");
    }

    @Test
    public void shouldSetupAppContext() {
    }

}
