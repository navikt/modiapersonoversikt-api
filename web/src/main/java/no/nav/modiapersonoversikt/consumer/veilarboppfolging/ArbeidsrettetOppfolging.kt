package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.Veileder
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

object ArbeidsrettetOppfolging {
    @CacheConfig(cacheNames = ["oppfolgingsinfoCache"], keyGenerator = "userkeygenerator")
    interface Service {
        @Cacheable
        fun hentOppfolgingsinfo(fodselsnummer: Fnr): Info

        @Cacheable
        fun hentOppfolgingStatus(fodselsnummer: Fnr): Status
        fun ping()
    }

    data class Status(
        val underOppfolging: Boolean,
        val erManuell: Boolean,
    )

    data class Info(
        val erUnderOppfolging: Boolean,
        val erManuell: Boolean,
        val veileder: Veileder?,
        val oppfolgingsenhet: Enhet?
    )

    data class EnhetOgVeileder(
        val oppfolgingsenhet: Enhet?,
        val veilederId: String?
    )

    data class Enhet(
        val enhetId: String,
        val navn: String
    )
}
