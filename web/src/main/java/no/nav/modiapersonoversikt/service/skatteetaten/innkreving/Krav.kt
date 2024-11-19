package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.joda.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Krav(
    val kravId: String,
    val kid: String,
    val kravType: String,
    val debitor: Debitor,
    val kreditor: Kreditor,
    val posteringer: List<KravPostering> = listOf(),
    val opprettetDato: LocalDateTime,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KravPostering(
    val kode: String,
    val beskrivelse: String,
    val opprinneligBelop: Double,
    val betaltBelop: Double,
    val gjenstaendeBelop: Double,
    val opprettetDato: LocalDateTime,
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
