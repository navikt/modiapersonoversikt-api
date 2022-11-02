package no.nav.modiapersonoversikt.consumer.dkif

import DigDirServiceImpl
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class DkifConfig {
    @Bean(name = ["DkifSoap"])
    open fun defaultDkifService(dkifV1: DigitalKontaktinformasjonV1): Dkif.Service {
        return DkifServiceImpl(dkifV1)
    }

    @Bean(name = ["DkifRest"])
    open fun restDkifService(): Dkif.Service {
        return DkifServiceRestImpl(
            EnvironmentUtils.getRequiredProperty("DKIF_REST_URL")
        )
    }

    @Bean(name = ["DigDirRest"])
    open fun restDigDirService(machineToMachineTokenClient: MachineToMachineTokenClient): Dkif.Service {
        return DigDirServiceImpl(
            EnvironmentUtils.getRequiredProperty("DIG_DIR_REST_URL"),
            machineToMachineTokenClient
        )
    }

    @Bean
    @Primary
    open fun dkifService(
        @Qualifier("DkifSoap") soapService: Dkif.Service,
        @Qualifier("DigDirRest") restService: Dkif.Service,
    ): Dkif.Service {
        return object : Dkif.Service {
            private val digDirExperiment = Scientist.createExperiment<Dkif.DigitalKontaktinformasjon>(
                Scientist.Config(
                    name = "digdir",
                    experimentRate = Scientist.FixedValueRate(0.1)
                )
            )

            override fun hentDigitalKontaktinformasjon(fnr: String): Dkif.DigitalKontaktinformasjon {
                return digDirExperiment.run(
                    control = { soapService.hentDigitalKontaktinformasjon(fnr) },
                    experiment = { restService.hentDigitalKontaktinformasjon(fnr) },
                )
            }

            override fun ping(): SelfTestCheck {
                return SelfTestCheck(
                    "DkifExperiment",
                    false
                ) {
                    try {
                        soapService.ping()
                        restService.ping()
                        HealthCheckResult.healthy()
                    } catch (e: Exception) {
                        HealthCheckResult.unhealthy(e)
                    }
                }
            }
        }
    }
}
