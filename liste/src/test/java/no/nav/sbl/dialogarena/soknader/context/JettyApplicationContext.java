package no.nav.sbl.dialogarena.soknader.context;

import no.nav.sbl.dialogarena.soknader.SoeknaderTestApplication;
import no.nav.sbl.dialogarena.soknader.context.mock.SoknaderMockContext;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.mockito.Mockito.mock;

@Configuration
@Import({SoknaderMockContext.class})
public class JettyApplicationContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SoeknaderTestApplication application() {
        return new SoeknaderTestApplication();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return mock(SakOgBehandlingPortType.class);
    }

}
