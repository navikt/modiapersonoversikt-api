package no.nav.modiapersonoversikt.service.utbetaling

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import java.time.LocalDate

@CacheConfig(cacheNames = ["utbetalingCache"], keyGenerator = "userkeygenerator")
interface UtbetalingService : Pingable {
    @Cacheable
    fun hentUtbetalinger(fnr: Fnr, startDato: LocalDate, sluttDato: LocalDate): List<UtbetalingDomain.Utbetaling>
}
