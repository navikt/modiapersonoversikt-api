package no.nav.sbl.dialogarena.soknader.panel.context;

import no.nav.sbl.dialogarena.soknader.panel.SoeknaderTestApplication;
import no.nav.sbl.dialogarena.soknader.panel.context.mock.SoknaderMockContext;
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
    public SoeknaderTestApplication application() {
        return new SoeknaderTestApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return mock(SakOgBehandlingPortType.class);
    }
}
