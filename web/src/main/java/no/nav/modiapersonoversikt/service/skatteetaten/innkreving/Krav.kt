package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.joda.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Krav(
    val kravId: String? = null,
    val kid: String? = null,
    val kravType: String? = null,
    val debitor: Debitor? = null,
    val kreditor: Kreditor? = null,
    val kravLinjer: List<KravLinje> = listOf(),
    val opprettetDato: LocalDateTime? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KravLinje(
    val kode: String? = null,
    val beskrivelse: String? = null,
    val opprinneligBelop: Double,
    val betaltBelop: Double? = null,
    val gjenstaendeBelop: Double? = null,
    val opprettetDato: LocalDateTime? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Debitor(
    val debitorId: String,
    val name: String,
    val identType: IdentType,
    val ident: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Kreditor(
    val kreditorId: String,
    val name: String,
    val identType: IdentType,
    val ident: String,
)

enum class IdentType {
    FNR,
    ORG_NR,
}
