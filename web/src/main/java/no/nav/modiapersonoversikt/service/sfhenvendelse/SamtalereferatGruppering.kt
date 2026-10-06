package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO

internal fun grupperSamtalereferater(henvendelser: List<HenvendelseDTO>): List<HenvendelseDTO> {
    val referater = henvendelser.filter { it.henvendelseType == HenvendelseDTO.HenvendelseType.SAMTALEREFERAT }
    val heads = referater.filter { it.kjedeId.isBlank() }
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
    val children = referater.filter { it.kjedeId.isNotBlank() }.groupBy { it.kjedeId }
    require(children.keys.all { it in headById }) { "Samtalereferat mangler hovedmelding" }

    return henvendelser.mapNotNull { henvendelse ->
        if (henvendelse.henvendelseType != HenvendelseDTO.HenvendelseType.SAMTALEREFERAT) {
            henvendelse
        } else if (henvendelse.kjedeId.isNotBlank()) {
            null
        } else {
            val id = headId.getValue(henvendelse)
            val kjede = listOf(henvendelse) + children[id].orEmpty()
            require(
                kjede.all {
                    it.fnr == henvendelse.fnr &&
                        it.aktorId == henvendelse.aktorId &&
                        it.gjeldendeTemagruppe == henvendelse.gjeldendeTemagruppe
                },
            ) { "Samtalereferater i samme kjede har ulike eiere eller temagrupper" }
            require(kjede.all { it.meldinger?.size == 1 }) {
                "Samtalereferat har ikke nøyaktig én melding"
            }
            val meldinger = kjede.flatMap { it.meldinger.orEmpty() }
            require(
                meldinger.all { !it.meldingsId.isNullOrBlank() } &&
                    meldinger.map { it.meldingsId }.distinct().size == meldinger.size,
            ) { "Samtalereferat har manglende eller duplikat meldings-ID" }

            henvendelse.copy(
                kjedeId = id,
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
