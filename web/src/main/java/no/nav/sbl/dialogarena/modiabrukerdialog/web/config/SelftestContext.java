package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.common.DiskCheck;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.common.TruststoreCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SelftestContext {
    @Bean
    public SelfTestCheck diskCheck() {
        return DiskCheck.asSelftestCheck();
    }

    @Bean
    public SelfTestCheck truststoreCheck() {
        return TruststoreCheck.asSelftestCheck();
    }
}
