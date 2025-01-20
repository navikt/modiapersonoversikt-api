package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Krav(
    val kravId: String,
    val kid: String,
    val kravType: String,
    val debitor: Debitor,
    val kreditor: Kreditor,
    val posteringer: List<KravPostering> = listOf(),
    @Schema(type = "string", format = "date", example = "2025-01-01")
    val opprettetDato: LocalDate? = null,
)

@Serializable
data class KravPostering(
    val kode: String,
    val beskrivelse: String,
    val opprinneligBelop: Double,
    val betaltBelop: Double,
    val gjenstaendeBelop: Double,
    @Schema(type = "string", format = "date", example = "2025-01-01")
    val opprettetDato: LocalDate? = null,
)

@Serializable
data class Debitor(
    val debitorId: String,
    val name: String,
    val identType: IdentType,
    val ident: String,
)

@Serializable
data class Kreditor(
    val kreditorId: String,
    val name: String,
    val identType: IdentType,
    val ident: String,
)

@Serializable
enum class IdentType {
    FNR,
    ORG_NR,
}
