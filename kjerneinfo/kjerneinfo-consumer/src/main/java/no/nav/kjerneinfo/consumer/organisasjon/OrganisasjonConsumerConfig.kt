package no.nav.kjerneinfo.consumer.organisasjon

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.sts.SystemUserTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrganisasjonConsumerConfig {
    @Autowired
    private lateinit var systemUserTokenProvider: SystemUserTokenProvider

    @Bean
    open fun organisasjonService(): OrganisasjonService {
        return OrganisasjonServiceImpl(organisasjonV1RestClient())
    }

    @Bean
    open fun organisasjonV1RestClient(): OrganisasjonV1RestClient {
        return OrganisasjonRestClientImpl(systemUserTokenProvider)
    }

    @Bean
    open fun eregOrganisasjonCheck(): SelfTestCheck {
        return SelfTestCheck(String.format("EREG Organisasjon  %s", ORG_NUMMER_NAV),
                false
        ) {
            try {
                val (_, navn) = organisasjonV1RestClient().hentKjernInfoFraRestClient(ORG_NUMMER_NAV)
                if (navn.navnelinje1 == ORG_NAVN_NAV)
                    HealthCheckResult.healthy()
                else
                    HealthCheckResult.unhealthy("Feil ved henting av orgnavn fra Ereg. Sjekke tjeneste logg")
            } catch (e: Exception) {
                HealthCheckResult.unhealthy(e)
            }
        }
    }

    companion object {
        private const val ORG_NUMMER_NAV = "990983666"
        private const val ORG_NAVN_NAV = "NAV IKT"
    }
}
