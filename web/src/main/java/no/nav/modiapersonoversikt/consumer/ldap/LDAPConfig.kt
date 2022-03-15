package no.nav.modiapersonoversikt.consumer.ldap

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class LDAPConfig {
    @Bean
    open fun ldapService(): LDAPService = LDAPServiceImpl(LDAPContextProviderImpl())
}
