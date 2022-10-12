package no.nav.modiapersonoversikt.service.pdl

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.utils.DownstreamApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PdlOppslagServiceConfig {
    @Bean
    open fun pdlOppslagService(sts: SystemUserTokenProvider): PdlOppslagService = PdlOppslagServiceImpl(sts)

    companion object {
        val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("PDL_SCOPE"))
    }
}
