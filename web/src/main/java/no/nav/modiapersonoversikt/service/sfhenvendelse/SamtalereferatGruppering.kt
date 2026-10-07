package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO

private fun HenvendelseDTO.erHovedreferat(): Boolean = kjedeId.isBlank() || kjedeId == meldinger?.singleOrNull()?.meldingsId

internal fun grupperSamtalereferater(henvendelser: List<HenvendelseDTO>): List<HenvendelseDTO> {
    val referater = henvendelser.filter { it.henvendelseType == HenvendelseDTO.HenvendelseType.SAMTALEREFERAT }
    val heads = referater.filter { it.erHovedreferat() }
    val headsWithId =
        heads.map { head ->
            val melding = head.meldinger?.singleOrNull()
            require(!melding?.meldingsId.isNullOrBlank()) {
                "Samtalereferat mangler entydig hovedmelding"
            }
            head to requireNotNull(melding?.meldingsId)
        }
    require(headsWithId.map { it.second }.distinct().size == headsWithId.size) {
        "Samtalereferater har flere hovedmeldinger med samme ID"
    }
    val headId = headsWithId.toMap()
    val headById = headsWithId.associate { (head, id) -> id to head }
    val children = referater.filterNot { it.erHovedreferat() }.groupBy { it.kjedeId }
    require(children.keys.all { it in headById }) { "Samtalereferat mangler hovedmelding" }

    return henvendelser.mapNotNull { henvendelse ->
        if (henvendelse.henvendelseType != HenvendelseDTO.HenvendelseType.SAMTALEREFERAT) {
            henvendelse
        } else if (!henvendelse.erHovedreferat()) {
            null
        } else {
            val id = headId.getValue(henvendelse)
            val kjede = listOf(henvendelse) + children[id].orEmpty()
            require(
                kjede.all {
                    it.fnr == henvendelse.fnr &&
                        it.aktorId == henvendelse.aktorId
                },
            ) { "Samtalereferater i samme kjede har ulike eiere" }
            require(kjede.all { it.meldinger?.size == 1 }) {
                "Samtalereferat har ikke nøyaktig én melding"
            }
            val referaterMedMelding = kjede.map { it to it.meldinger.orEmpty().single() }
            val meldinger = referaterMedMelding.map { it.second }
            require(
                meldinger.all { !it.meldingsId.isNullOrBlank() } &&
                    meldinger.map { it.meldingsId }.distinct().size == meldinger.size,
            ) { "Samtalereferat har manglende eller duplikat meldings-ID" }
            val nyesteSendtDato = meldinger.maxOf { it.sendtDato.toInstant() }
            val nyesteReferater = referaterMedMelding.filter { (_, melding) -> melding.sendtDato.toInstant() == nyesteSendtDato }
            require(nyesteReferater.map { it.first.gjeldendeTemagruppe }.distinct().size == 1) {
                "Samtalereferater med samme sendetidspunkt har ulike temagrupper"
            }

            henvendelse.copy(
                kjedeId = id,
                gjeldendeTemagruppe = nyesteReferater.first().first.gjeldendeTemagruppe,
                meldinger = meldinger,
                journalposter = kjede.flatMap { it.journalposter.orEmpty() },
                markeringer = kjede.flatMap { it.markeringer.orEmpty() },
                kasseringsDato = kjede.mapNotNull { it.kasseringsDato }.minOrNull(),
                feilsendt = kjede.any { it.feilsendt },
                sladding = if (kjede.any { it.sladding == true }) true else henvendelse.sladding,
                avsluttetDato = kjede.mapNotNull { it.avsluttetDato }.maxOrNull(),
            )
        }
    }
}
