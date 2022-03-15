package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SfHenvendelseConfig {
    @Bean
    open fun sfHenvendelseApi(
        pdlOppslagService: PdlOppslagService,
        arbeidsfordelingService: ArbeidsfordelingService,
        ansattService: AnsattService,
        systemUserTokenProvider: SystemUserTokenProvider
    ): SfHenvendelseService {
        return SfHenvendelseServiceImpl(
            pdlOppslagService,
            arbeidsfordelingService,
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
