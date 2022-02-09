package no.nav.modiapersonoversikt.legacy.sak.config

import no.nav.modiapersonoversikt.legacy.sak.service.BulletproofKodeverkService
import no.nav.modiapersonoversikt.legacy.sak.service.DokumentMetadataService
import no.nav.modiapersonoversikt.legacy.sak.service.PesysService
import no.nav.modiapersonoversikt.legacy.sak.service.SakOgBehandlingService
import no.nav.modiapersonoversikt.legacy.sak.service.SaksService
import no.nav.modiapersonoversikt.legacy.sak.service.SakstemaGrupperer
import no.nav.modiapersonoversikt.legacy.sak.service.SakstemaService
import no.nav.modiapersonoversikt.legacy.sak.service.filter.Filter
import no.nav.modiapersonoversikt.legacy.sak.service.saf.ExperimentSafService
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafGraphqlServiceImpl
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafServiceImpl
import no.nav.modiapersonoversikt.legacy.sak.utils.TemagrupperHenter
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.UnleashProxySwitcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakServiceConfig {
    @Bean
    open fun sakOgBehandlingFilter(): Filter {
        return Filter()
    }

    @Bean
    open fun sakOgBehandlingService(): SakOgBehandlingService {
        return SakOgBehandlingService()
    }

    @Bean
    open fun pesysService(): PesysService {
        return PesysService()
    }

    @Bean
    open fun sakstemaService(): SakstemaService {
        return SakstemaService()
    }

    @Bean
    open fun saksService(): SaksService {
        return SaksService()
    }

    @Bean
    open fun dokumentMetadataService(safService: SafService): DokumentMetadataService {
        return DokumentMetadataService(safService)
    }

    @Bean
    open fun temagrupperHenter(): TemagrupperHenter {
        return TemagrupperHenter()
    }

    @Bean
    open fun sakstemaGrupperer(): SakstemaGrupperer {
        return SakstemaGrupperer()
    }

    @Bean
    open fun bulletproofKodeverkService(): BulletproofKodeverkService {
        return BulletproofKodeverkService()
    }

    @Bean
    open fun safService(unleashService: UnleashService): SafService {
        val graphqlSaf = SafGraphqlServiceImpl()
        return UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.USE_GRAPHQL_SAF,
            unleashService = unleashService,
            ifEnabled = graphqlSaf,
            ifDisabled = ExperimentSafService(
                control = SafServiceImpl(),
                experiment = graphqlSaf,
                unleashService = unleashService
            )
        )
    }
}
