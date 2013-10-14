package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class ForeldrepengerPanelMockContext {

    @Bean
    public ForeldrepengerLoader foreldrepengerLoader() {
        return mock(ForeldrepengerLoader.class);
    }

    @Bean
    public ForeldrepengerServiceBi foreldrepengerServiceBi() {
        return mock(ForeldrepengerServiceBi.class);
    }

}
