package no.nav.modiapersonoversikt.service.sakstema

import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusSakstema
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime

object FilterUtils {
    private val PROD_SETTING_DATO: LocalDateTime by lazy {
        LocalDate
            .parse(getRequiredProperty("SAKSOVERSIKT_PRODSETTNINGSDATO"))
            .atStartOfDay()
    }

    @JvmStatic
    @JvmName("fjernGamleDokumentSoknadsstatus")
    fun fjernGamleDokumenter(saker: List<SoknadsstatusSakstema>): List<SoknadsstatusSakstema> {
        return saker.map { sak ->
            val filtrerteDokument =
                sak.dokumentMetadata.filter { dokument ->
                    val erFraSaf = dokument.baksystem.size == 1 && dokument.baksystem.contains(Baksystem.SAF)
                    val erFraForProdsetting: Boolean by lazy { dokument.dato.isBefore(PROD_SETTING_DATO) }
                    (erFraSaf && erFraForProdsetting).not()
                }
            sak.copy(dokumentMetadata = filtrerteDokument)
        }
    }
}
