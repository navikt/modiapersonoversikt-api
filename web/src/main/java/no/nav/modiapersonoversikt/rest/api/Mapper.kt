package no.nav.modiapersonoversikt.rest.api

import no.nav.modiapersonoversikt.legacy.api.domain.Person
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Status
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.JournalpostDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MarkeringDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingFraDTO
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.domain.Traad
import no.nav.modiapersonoversikt.rest.DATO_TID_FORMAT
import no.nav.modiapersonoversikt.rest.dialog.MeldingDTO
import no.nav.modiapersonoversikt.rest.dialog.TraadDTO
import org.joda.time.format.DateTimeFormat
import java.time.format.DateTimeFormatter
import kotlin.collections.HashMap
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.HenvendelseDTO as SfHenvendelseDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingDTO as SfMeldingDTO

typealias Function<S, T> = (s: S) -> T
interface DTO

inline fun <reified TO> Any.toDTO(): TO where TO : DTO {
    return Mapper.map(this, TO::class.java)
}

inline fun <reified TO : Any> DTO.fromDTO(): TO {
    return Mapper.map(this, TO::class.java)
}

inline fun <reified TO> Iterable<Any>.toDTO(): List<TO> where TO : DTO {
    return Mapper.mapList(this, TO::class.java)
}

inline fun <reified TO : Any> Iterable<DTO>.fromDTO(): List<TO> {
    return Mapper.mapList(this, TO::class.java)
}

class Mapper {
    companion object {
        val mapping: MutableMap<Class<*>, MutableMap<Class<*>, Function<*, *>>> = HashMap()
        inline fun <reified FROM, reified TO> registerMapping(noinline fn: (FROM) -> TO) {
            val fromMap = mapping.computeIfAbsent(FROM::class.java as Class<*>) { HashMap() }
            fromMap[TO::class.java] = fn as Function<*, *>
        }

        fun <FROM : Iterable<Any>, TO> mapList(from: FROM, toCls: Class<TO>): List<TO> {
            return from.map { map(it, toCls) }
        }

        fun <FROM : Any, TO> map(from: FROM, toCls: Class<TO>): TO {
            val fromCls = from.javaClass
            return mapping
                .get(fromCls)
                ?.get(toCls)
                ?.let { it as Function<FROM, TO> }
                ?.invoke(from)
                ?: throw IllegalStateException()
        }

        init {
            registerMapping(::traadMapping)
            registerMapping(::meldingMapping)
            registerMapping(::henvendelseDTOMapping)
        }
    }
}

private fun traadMapping(it: Traad): TraadDTO = TraadDTO(it.traadId, it.meldinger.toDTO())

private fun meldingMapping(melding: Melding): MeldingDTO =
    MeldingDTO(
        mapOf(
            "id" to melding.id,
            "oppgaveId" to melding.oppgaveId,
            "meldingstype" to melding.meldingstype?.name,
            "temagruppe" to melding.gjeldendeTemagruppe?.name,
            "skrevetAvTekst" to melding.getSkrevetAv(),
            "journalfortAv" to melding.journalfortAv?.let(::hentPerson),
            "journalfortDato" to melding.journalfortDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
            "journalfortTema" to melding.journalfortTema,
            "journalfortTemanavn" to melding.journalfortTemanavn,
            "journalfortSaksid" to melding.journalfortSaksId,
            "fritekst" to melding.fritekst,
            "lestDato" to melding.lestDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
            "status" to melding.status?.name,
            "statusTekst" to melding.statusTekst,
            "opprettetDato" to melding.opprettetDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
            "ferdigstiltDato" to melding.ferdigstiltDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
            "erFerdigstiltUtenSvar" to melding.erFerdigstiltUtenSvar,
            "ferdigstiltUtenSvarDato" to melding.ferdigstiltUtenSvarDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
            "ferdigstiltUtenSvarAv" to melding.ferdigstiltUtenSvarAv?.let(::hentSaksbehandler),
            "kontorsperretEnhet" to melding.kontorsperretEnhet,
            "kontorsperretAv" to melding.kontorsperretAv?.let(::hentSaksbehandler),
            "markertSomFeilsendtAv" to melding.markertSomFeilsendtAv?.let(::hentSaksbehandler),
            "erDokumentMelding" to melding.erDokumentMelding
        )
    )

private fun henvendelseDTOMapping(henvendelse: SfHenvendelseDTO): TraadDTO {
    val journalpost: JournalpostDTO? = henvendelse.journalposter?.firstOrNull()
    val meldinger: List<MeldingDTO> = (henvendelse.meldinger ?: emptyList()).map { melding ->
        MeldingDTO(
            mapOf(
                "id" to "${henvendelse.kjedeId}-${(henvendelse.hashCode())}",
                "oppgaveId" to null,
                "meldingstype" to meldingstypeFraSfTyper(henvendelse, melding),
                "temagruppe" to henvendelse.gjeldendeTemagruppe,
                "skrevetAvTekst" to melding.fra.ident, // TODO trenger remapping for hente ut navn
                "journalfortAv" to journalpost?.journalforerNavIdent, // TODO trenger remapping for hente ut navn
                "journalfortDato" to journalpost?.journalfortDato?.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                "journalfortTema" to journalpost?.journalfortTema,
                "journalfortTemanavn" to journalpost?.journalfortTema, // TODO trenger remapping vha kodeverk
                "journalfortSaksid" to journalpost?.journalpostId,
                "fritekst" to melding.fritekst,
                "lestDato" to melding.lestDato?.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                "status" to when {
                    melding.erFraBruker() -> Status.IKKE_BESVART
                    melding.lestDato != null -> Status.LEST_AV_BRUKER
                    else -> Status.IKKE_LEST_AV_BRUKER
                },
                "statusTekst" to null, // Blir ikke brukt av frontend uansett
                "opprettetDato" to melding.sendtDato.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                "ferdigstiltDato" to melding.sendtDato.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                "erFerdigstiltUtenSvar" to false, // TODO Informasjon finnes ikke i SF
                "ferdigstiltUtenSvarDato" to null, // TODO Informasjon finnes ikke i SF
                "ferdigstiltUtenSvarAv" to null, // TODO Informasjon finnes ikke i SF
                "kontorsperretEnhet" to if (henvendelse.kontorsperre) henvendelse.opprinneligGT else null,
                "kontorsperretAv" to henvendelse.markeringer?.find { it.markeringstype == MarkeringDTO.Markeringstype.KONTORSPERRE }?.markertAv, // TODO trenger remapping for ansatt navn
                "markertSomFeilsendtAv" to henvendelse.markeringer?.find { it.markeringstype == MarkeringDTO.Markeringstype.FEILSENDT }?.markertAv, // TODO trenger remapping for ansatt navn
                "erDokumentMelding" to false // Brukes ikke
            )
        )
    }
    return TraadDTO(requireNotNull(henvendelse.kjedeId), meldinger)
}

private fun SfMeldingDTO.erFraBruker() = this.fra.identType == MeldingFraDTO.IdentType.AKTORID

private fun meldingstypeFraSfTyper(henvendelse: SfHenvendelseDTO, melding: SfMeldingDTO): Meldingstype {
    val erForsteMelding = henvendelse.meldinger?.firstOrNull() == melding
    return when (henvendelse.henvendelseType) {
        SfHenvendelseDTO.HenvendelseType.SAMTALEREFERAT -> Meldingstype.SAMTALEREFERAT_TELEFON // TODO trenger kanal fra SF her
        SfHenvendelseDTO.HenvendelseType.MELDINGSKJEDE -> {
            when (melding.fra.identType) {
                MeldingFraDTO.IdentType.AKTORID -> if (erForsteMelding) Meldingstype.SPORSMAL_SKRIFTLIG else Meldingstype.SVAR_SBL_INNGAAENDE
                MeldingFraDTO.IdentType.NAVIDENT -> if (erForsteMelding) Meldingstype.SPORSMAL_MODIA_UTGAAENDE else Meldingstype.SVAR_SKRIFTLIG
            }
        }
    }
}

private fun hentPerson(person: Person): Map<String, String> =
    mapOf(
        "fornavn" to person.fornavn,
        "etternavn" to person.etternavn
    )

private fun hentSaksbehandler(saksbehandler: Saksbehandler): Map<String, String> =
    mapOf(
        "fornavn" to saksbehandler.fornavn,
        "etternavn" to saksbehandler.etternavn,
        "ident" to saksbehandler.ident
    )
