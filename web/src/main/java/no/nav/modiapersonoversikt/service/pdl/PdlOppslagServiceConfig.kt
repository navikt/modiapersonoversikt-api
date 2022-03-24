package no.nav.modiapersonoversikt.service.pdl

import no.nav.common.sts.SystemUserTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PdlOppslagServiceConfig {
    @Bean
    open fun pdlOppslagService(sts: SystemUserTokenProvider): PdlOppslagService = PdlOppslagServiceImpl(sts)
}
