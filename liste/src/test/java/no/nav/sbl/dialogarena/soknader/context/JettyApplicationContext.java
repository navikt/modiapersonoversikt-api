package no.nav.sbl.dialogarena.soknader.context;

import no.nav.sbl.dialogarena.soknader.SoknaderTestApplication;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.mockito.Mockito.mock;

@Configuration
@Import({SoknaderContext.class})
public class JettyApplicationContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SoknaderTestApplication application() {
        return new SoknaderTestApplication();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return mock(SakOgBehandlingPortType.class);
    }

}
