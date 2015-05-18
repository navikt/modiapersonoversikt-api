package no.nav.sbl.modiabrukerdialog.pip.journalforing.config;

import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.DefaultJournalfortTemaAttributeLocatorDelegate;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.JournalfortTemaAttributeLocatorDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JournalfortTemaPipConfig {

    @Bean
    public JournalfortTemaAttributeLocatorDelegate enhetAttributeLocatorDelegate() {
        return new DefaultJournalfortTemaAttributeLocatorDelegate();
    }

}
