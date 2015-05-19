package no.nav.sbl.modiabrukerdialog.pip.geografisk.config;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class EnhetAttributeLocatorTestConfig {
    @Bean
    public GOSYSNAVansatt gosysnaVansatt() {
        return mock(GOSYSNAVansatt.class);
    }

    @Bean
    public GOSYSNAVOrgEnhet gosysnavOrgEnhet() {
        return mock(GOSYSNAVOrgEnhet.class);
    }
}
