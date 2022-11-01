package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.*
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.impl.PolicyDecisionPointImpl
import no.nav.personoversikt.common.kabac.impl.PolicyEnforcementPointImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Configuration
open class TilgangskontrollConfig {
    val log: Logger = LoggerFactory.getLogger(Tilgangskontroll::class.java)

    @Bean
    open fun decisionPoint(
        pdl: PdlOppslagService,
        skjermingApi: SkjermedePersonerApi,
        norg: NorgApi,
        ansattService: AnsattService,
        henvendelseService: SfHenvendelseService,
        ldap: LDAPService
    ): Kabac.PolicyDecisionPoint {
        return PolicyDecisionPointImpl().apply {
            install(AuthContextPip)
            install(NavIdentPip)
            install(BrukersFnrPip(pdl))
            install(BrukersAktorIdPip(pdl))
            install(BrukersDiskresjonskodePip(pdl))
            install(BrukersSkjermingPip(skjermingApi))
            install(BrukersEnhetPip(norg))
            install(BrukersGeografiskeTilknyttningPip(pdl))
            install(BrukersRegionEnhetPip(norg))
            install(VeiledersEnheterPip(ansattService))
            install(VeiledersRegionEnheterPip(norg))
            install(VeiledersRollerPip(ansattService))
            install(VeiledersTemaPip(ansattService))
            install(HenvendelseEierPip(henvendelseService))
            install(InternalTilgangPip())
        }
    }

    @Bean
    open fun enforcementPoint(decisionPoint: Kabac.PolicyDecisionPoint): Kabac.PolicyEnforcementPoint {
        return PolicyEnforcementPointImpl(
            bias = Decision.Type.DENY,
            policyDecisionPoint = decisionPoint
        )
    }

    @Bean
    open fun tilgangskontroll(enforcementPoint: Kabac.PolicyEnforcementPoint): Tilgangskontroll {
        return TilgangskontrollKabac(enforcementPoint) {
            log.error(it)
            ResponseStatusException(HttpStatus.FORBIDDEN, it)
        }
    }
}
