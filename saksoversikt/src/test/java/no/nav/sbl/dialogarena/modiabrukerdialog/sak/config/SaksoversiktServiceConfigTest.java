package no.nav.sbl.dialogarena.modiabrukerdialog.sak.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock.KodeverkMock;
import no.nav.sbl.util.EnvironmentUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ModiaStubConfig.class,
        SaksoversiktServiceConfig.class,
        KodeverkMock.class
})
public class SaksoversiktServiceConfigTest {

    @Before
    public void setUp() {
        EnvironmentUtils.setProperty("appres.cms.url", "http://www.nav.no/", EnvironmentUtils.Type.PUBLIC);
    }

    @Test
    public void shouldSetupAppContext() { }

}
