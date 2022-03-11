package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.legacy.sak.mock.KodeverkMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ModiaStubConfig.class,
        SaksoversiktServiceConfig.class,
        SakServiceConfig.class,
        KodeverkMock.class
})
public class SaksoversiktServiceConfigTest {

    @Test
    public void shouldSetupAppContext() {
        System.out.println("Testing that spring-config works");
    }

}
