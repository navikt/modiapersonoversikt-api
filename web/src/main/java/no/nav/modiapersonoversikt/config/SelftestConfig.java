package no.nav.modiapersonoversikt.config;

import no.nav.common.cxf.StsConfig;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.modiapersonoversikt.infrastructure.ping.DiskCheck;
import no.nav.modiapersonoversikt.infrastructure.ping.StsSoapCheck;
import no.nav.modiapersonoversikt.infrastructure.ping.TruststoreCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SelftestConfig {
    @Bean
    public SelfTestCheck diskCheck() {
        return DiskCheck.asSelftestCheck();
    }

    @Bean
    public SelfTestCheck truststoreCheck() {
        return TruststoreCheck.asSelftestCheck();
    }

    @Bean
    public SelfTestCheck stsSelftestCheck(StsConfig stsConfig) {
        return StsSoapCheck.asSelftestCheck(stsConfig);
    }
}
