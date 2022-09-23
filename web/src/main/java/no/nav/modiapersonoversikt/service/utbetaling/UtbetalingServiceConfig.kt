package no.nav.modiapersonoversikt.service.utbetaling

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV2Api
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.ConvertionUtils.toJodaTime
import no.nav.modiapersonoversikt.utils.UnleashProxySwitcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
open class UtbetalingServiceConfig {
    @Bean
    open fun utbetalingerService(
        unleashService: UnleashService,
        apiClient: UtbetaldataV2Api,
    ): UtbetalingService = UnleashProxySwitcher.createSwitcher(
        featureToggle = Feature.REST_UTBETALING_SWITCHER,
        unleashService = unleashService,
        ifEnabled = UtbetalingServiceImpl(apiClient),
        ifDisabled = asUtbetalingService(WSUtbetalingServiceImpl())
    )

    private fun asUtbetalingService(service: WSUtbetalingService) = object : UtbetalingService {
        override fun hentUtbetalinger(
            fnr: Fnr,
            startDato: LocalDate,
            sluttDato: LocalDate
        ): List<UtbetalingDomain.Utbetaling> {
            return WSUtbetalingMapper.hentUtbetalinger(
                service.hentWSUtbetalinger(
                    fnr.get(),
                    startDato.toJodaTime(),
                    sluttDato.toJodaTime()
                )
            )
        }

        override fun ping(): SelfTestCheck {
            return SelfTestCheck("Utbetaling - SOAP", false) {
                try {
                    service.ping()
                    HealthCheckResult.healthy()
                } catch (e: Exception) {
                    HealthCheckResult.unhealthy(e)
                }
            }
        }
    }
}
