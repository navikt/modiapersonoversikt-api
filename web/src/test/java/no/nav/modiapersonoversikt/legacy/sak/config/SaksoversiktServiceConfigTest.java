package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.legacy.sak.SakServiceConfig;
import no.nav.modiapersonoversikt.legacy.sak.mock.KodeverkMock;
import no.nav.modiapersonoversikt.service.saf.SafConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ModiaStubConfig.class,
        SakServiceConfig.class,
        SafConfig.class,
        KodeverkMock.class
})
public class SaksoversiktServiceConfigTest {

    @Test
    public void shouldSetupAppContext() {
        System.out.println("Testing that spring-config works");
    }

}
