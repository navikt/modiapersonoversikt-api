package no.nav.modiapersonoversikt.consumer.ldap

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class LDAPConfig {
    @Bean
    open fun ldapService(): LDAPService = LDAPServiceImpl(LDAPContextProviderImpl())
}
