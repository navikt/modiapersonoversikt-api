package no.nav.modiapersonoversikt.service.ansattservice

import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.nom.NomClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AnsattServiceConfig {
    @Bean
    open fun ansattService(axsysClient: AxsysClient, nomClient: NomClient): AnsattService {
        return AnsattServiceImpl(axsysClient, nomClient)
    }
}