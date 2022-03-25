package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SfHenvendelseConfig {
    @Bean
    open fun sfHenvendelseApi(
        pdlOppslagService: PdlOppslagService,
        norgApi: NorgApi,
        ansattService: AnsattService,
        systemUserTokenProvider: SystemUserTokenProvider
    ): SfHenvendelseService {
        return SfHenvendelseServiceImpl(
            pdlOppslagService,
            norgApi,
            ansattService,
            systemUserTokenProvider
        )
    }

    @Bean
    open fun sfHenvendelseApiPing(service: SfHenvendelseService): ConsumerPingable {
        return ConsumerPingable(
            "Salesforce - Henvendelse",
            service::ping
        )
    }
}
