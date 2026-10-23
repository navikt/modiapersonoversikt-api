package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO

private fun HenvendelseDTO.erHovedreferat(): Boolean = kjedeId.isBlank() || kjedeId == meldinger?.singleOrNull()?.meldingsId

internal fun grupperSamtalereferater(henvendelser: List<HenvendelseDTO>): List<HenvendelseDTO> {
    val referater = henvendelser.filter { it.henvendelseType == HenvendelseDTO.HenvendelseType.SAMTALEREFERAT }
    val hovedreferater = referater.filter { it.erHovedreferat() }
    val hovedreferaterMedId =
        hovedreferater.map { hovedreferat ->
            val melding = hovedreferat.meldinger?.singleOrNull()
            require(!melding?.meldingsId.isNullOrBlank()) {
                "Samtalereferat mangler entydig hovedmelding"
            }
            hovedreferat to requireNotNull(melding?.meldingsId)
        }
    require(hovedreferaterMedId.map { it.second }.distinct().size == hovedreferaterMedId.size) {
        "Samtalereferater har flere hovedmeldinger med samme ID"
    }
    val hovedreferatId = hovedreferaterMedId.toMap()
    val hovedreferatPerId = hovedreferaterMedId.associate { (hovedreferat, id) -> id to hovedreferat }
    val underreferater = referater.filterNot { it.erHovedreferat() }.groupBy { it.kjedeId }
    val indeksForEldsteReferatUtenHovedmelding =
        henvendelser
            .withIndex()
            .filter {
                it.value.henvendelseType == HenvendelseDTO.HenvendelseType.SAMTALEREFERAT &&
                    !it.value.erHovedreferat() &&
                    it.value.kjedeId !in hovedreferatPerId
            }.groupBy { it.value.kjedeId }
            .mapValues { (_, referaterUtenHovedmelding) ->
                require(referaterUtenHovedmelding.all { it.value.meldinger?.size == 1 }) {
                    "Samtalereferat har ikke nøyaktig én melding"
                }
                require(
                    referaterUtenHovedmelding.all {
                        !it.value.meldinger
                            ?.single()
                            ?.meldingsId
                            .isNullOrBlank()
                    },
                ) {
                    "Samtalereferat har manglende meldings-ID"
                }
                referaterUtenHovedmelding
                    .minWith(
                        compareBy<IndexedValue<HenvendelseDTO>> {
                            it.value.meldinger!!
                                .single()
                                .sendtDato
                                .toInstant()
                        }.thenBy {
                            it.value.meldinger!!
                                .single()
                                .meldingsId
                        }.thenBy { it.index },
                    ).index
            }

    return henvendelser.mapIndexedNotNull { index, henvendelse ->
        if (henvendelse.henvendelseType != HenvendelseDTO.HenvendelseType.SAMTALEREFERAT) {
            henvendelse
        } else if (!henvendelse.erHovedreferat() && indeksForEldsteReferatUtenHovedmelding[henvendelse.kjedeId] != index) {
            null
        } else {
            val id = if (henvendelse.erHovedreferat()) hovedreferatId.getValue(henvendelse) else henvendelse.kjedeId
            val kjede =
                if (henvendelse.erHovedreferat()) {
                    listOf(henvendelse) + underreferater[id].orEmpty()
                } else {
                    underreferater.getValue(id).sortedWith(
                        compareBy<HenvendelseDTO> {
                            it.meldinger!!
                                .single()
                                .sendtDato
                                .toInstant()
                        }.thenBy { it.meldinger!!.single().meldingsId },
                    )
                }
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
            val nyesteReferater =
                referaterMedMelding.filter { (_, melding) -> melding.sendtDato.toInstant() == nyesteSendtDato }
            require(nyesteReferater.map { it.first.gjeldendeTemagruppe }.distinct().size == 1) {
                "Samtalereferater med samme sendetidspunkt har ulike temagrupper"
            }
            val nyesteReferat = nyesteReferater.maxBy { (referat, _) -> referat.opprettetDato.toInstant() }.first

            henvendelse.copy(
                kjedeId = id,
                gjeldendeTemagruppe = nyesteReferat.gjeldendeTemagruppe,
                opprettetDato = nyesteReferat.opprettetDato,
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
