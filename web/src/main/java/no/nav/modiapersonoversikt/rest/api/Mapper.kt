package no.nav.modiapersonoversikt.rest.api

import no.nav.modiapersonoversikt.legacy.api.domain.Person
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.domain.Traad
import no.nav.modiapersonoversikt.rest.DATO_TID_FORMAT
import no.nav.modiapersonoversikt.rest.dialog.apis.MeldingDTO
import no.nav.modiapersonoversikt.rest.dialog.apis.TraadDTO
import org.joda.time.format.DateTimeFormat
import kotlin.collections.HashMap

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
