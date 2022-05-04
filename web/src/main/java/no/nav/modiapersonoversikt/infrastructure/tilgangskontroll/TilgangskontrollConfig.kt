package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.modiapersonoversikt.consumer.abac.AbacClient
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TilgangskontrollConfig {
    @Bean
    open fun tilgangskontroll(
        abacClient: AbacClient,
        ldapService: LDAPService,
        ansattService: AnsattService,
        norgApi: NorgApi,
        pdlOppslagService: PdlOppslagService,
        sfHenvendelseService: SfHenvendelseService,
        unleashService: UnleashService
    ): Tilgangskontroll {
        val context = TilgangskontrollContextImpl(
            abacClient,
            ldapService,
            ansattService,
            norgApi,
            pdlOppslagService,
            sfHenvendelseService,
            unleashService
        )
        return Tilgangskontroll(context)
    }
}
