package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingDTO

private data class ReferatMedMelding(
    val indeks: Int,
    val referat: HenvendelseDTO,
    val melding: MeldingDTO,
    val meldingsId: String,
)

private fun ReferatMedMelding.erHovedreferat(): Boolean = referat.kjedeId.isBlank() || referat.kjedeId == meldingsId

private val eldsteForst =
    compareBy<ReferatMedMelding> { it.melding.sendtDato.toInstant() }
        .thenBy { it.meldingsId }
        .thenBy { it.indeks }

internal fun grupperSamtalereferater(henvendelser: List<HenvendelseDTO>): List<HenvendelseDTO> {
    val referater =
        henvendelser
            .withIndex()
            .filter { it.value.henvendelseType == HenvendelseDTO.HenvendelseType.SAMTALEREFERAT }
            .map { (indeks, referat) ->
                val melding = referat.meldinger?.singleOrNull()
                requireNotNull(melding) { "Samtalereferat har ikke nøyaktig én melding" }
                val meldingsId = melding.meldingsId
                require(!meldingsId.isNullOrBlank()) { "Samtalereferat har manglende meldings-ID" }
                ReferatMedMelding(indeks, referat, melding, meldingsId)
            }
    // Hovedreferatet kan ha tom kjedeId; de andre referatene peker på meldingsId-en til dette referatet.
    val referaterPerKjedeId =
        referater.groupBy { referat ->
            if (referat.erHovedreferat()) referat.meldingsId else referat.referat.kjedeId
        }
    val samledeReferaterPerIndeks =
        referaterPerKjedeId
            .map { (kjedeId, referaterIKjeden) ->
                val hovedreferater = referaterIKjeden.filter { it.erHovedreferat() }
                require(hovedreferater.size <= 1) { "Samtalereferater har flere hovedmeldinger med samme ID" }

                val hovedreferat = hovedreferater.singleOrNull()
                // Uten hovedreferat velger vi det eldste, men beholder opprinnelig kjedeId som tråd-ID mot Salesforce.
                val referatSomVises = hovedreferat ?: referaterIKjeden.minWith(eldsteForst)
                val referaterIRekkefolge =
                    if (hovedreferat == null) {
                        referaterIKjeden.sortedWith(eldsteForst)
                    } else {
                        listOf(hovedreferat) + referaterIKjeden.filterNot { it.indeks == hovedreferat.indeks }
                    }
                referatSomVises.indeks to samleKjede(kjedeId, referatSomVises.referat, referaterIRekkefolge)
            }.toMap()

    return henvendelser.mapIndexedNotNull { indeks, henvendelse ->
        if (henvendelse.henvendelseType == HenvendelseDTO.HenvendelseType.SAMTALEREFERAT) {
            samledeReferaterPerIndeks[indeks]
        } else {
            henvendelse
        }
    }
}

private fun samleKjede(
    kjedeId: String,
    referatSomVises: HenvendelseDTO,
    referater: List<ReferatMedMelding>,
): HenvendelseDTO {
    require(referater.all { it.referat.fnr == referatSomVises.fnr && it.referat.aktorId == referatSomVises.aktorId }) {
        "Samtalereferater i samme kjede har ulike eiere"
    }
    val meldinger = referater.map { it.melding }
    require(referater.map { it.meldingsId }.distinct().size == referater.size) {
        "Samtalereferat har manglende eller duplikat meldings-ID"
    }
    val nyesteSendtDato = meldinger.maxOf { it.sendtDato.toInstant() }
    val nyesteReferater = referater.filter { it.melding.sendtDato.toInstant() == nyesteSendtDato }
    require(nyesteReferater.map { it.referat.gjeldendeTemagruppe }.distinct().size == 1) {
        "Samtalereferater med samme sendetidspunkt har ulike temagrupper"
    }
    val nyesteReferat = nyesteReferater.maxBy { it.referat.opprettetDato.toInstant() }.referat

    return referatSomVises.copy(
        kjedeId = kjedeId,
        gjeldendeTemagruppe = nyesteReferat.gjeldendeTemagruppe,
        opprettetDato = nyesteReferat.opprettetDato,
        meldinger = meldinger,
        journalposter = referater.flatMap { it.referat.journalposter.orEmpty() },
        markeringer = referater.flatMap { it.referat.markeringer.orEmpty() },
        kasseringsDato = referater.mapNotNull { it.referat.kasseringsDato }.minOrNull(),
        feilsendt = referater.any { it.referat.feilsendt },
        sladding = if (referater.any { it.referat.sladding == true }) true else referatSomVises.sladding,
        avsluttetDato = referater.mapNotNull { it.referat.avsluttetDato }.maxOrNull(),
    )
}
