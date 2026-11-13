package no.nav.modiapersonoversikt.service.persondata

import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.Tilgangsavslag
import java.time.LocalDate
import java.util.UUID

data class Representasjonsoppdrag(
    val fullmaktsgivere: List<Fullmaktsoppdrag>,
    val vergehavere: List<Vergeoppdrag>,
    val feilendeSystemer: Set<RepresentasjonsoppdragFeil>,
)

enum class RepresentasjonsoppdragFeil {
    FULLMAKTER,
    VERGEMAL,
    PERSONOPPLYSNINGER,
    TILGANGSMASKINEN,
}

data class RepresentertPerson(
    val ident: String?,
    val navn: Persondata.Navn?,
    val skjulingsaarsak: Persondata.Skjulingsaarsak?,
    val tilgangsavslag: Tilgangsavslag?,
)

data class Fullmaktsoppdrag(
    val id: UUID,
    val person: RepresentertPerson,
    val gyldigFraOgMed: LocalDate,
    val gyldigTilOgMed: LocalDate?,
    val leserettigheter: List<Persondata.KodeBeskrivelse<String>>,
    val skriverettigheter: List<Persondata.KodeBeskrivelse<String>>,
)

data class Vergeoppdrag(
    val id: Int,
    val person: RepresentertPerson,
    val gyldigFraOgMed: LocalDate,
    val aktivt: Boolean,
    val vergemaalstype: String,
    val leserettigheter: List<Persondata.KodeBeskrivelse<String>>,
    val skriverettigheter: List<Persondata.KodeBeskrivelse<String>>,
)
