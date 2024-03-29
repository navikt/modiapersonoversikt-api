package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SfHenvendelseConfig {
    @Bean
    open fun sfHenvendelseApi(
        pdlOppslagService: PdlOppslagService,
        norgApi: NorgApi,
        ansattService: AnsattService,
        oboTokenClient: OnBehalfOfTokenClient,
        machineToMachineTokenClient: MachineToMachineTokenClient,
    ): SfHenvendelseService {
        return SfHenvendelseServiceImpl(
            oboTokenClient = oboTokenClient.bindTo(SfHenvendelseApiFactory.downstreamApi()),
            machineToMachineTokenClient = machineToMachineTokenClient.bindTo(SfHenvendelseApiFactory.downstreamApi()),
            pdlOppslagService = pdlOppslagService,
            norgApi = norgApi,
            ansattService = ansattService,
        )
    }

    @Bean
    open fun sfHenvendelseApiPing(service: SfHenvendelseService): ConsumerPingable {
        return ConsumerPingable(
            "Salesforce - Henvendelse",
            service::ping,
        )
    }
}
