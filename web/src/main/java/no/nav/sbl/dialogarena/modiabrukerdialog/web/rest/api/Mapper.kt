package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATO_TID_FORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog.MeldingDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog.TraadDTO
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Traad
import org.joda.time.format.DateTimeFormat
import java.lang.IllegalStateException


typealias Function<S, T> = (s: S) -> T
interface DTO

public inline fun <reified TO> Any.toDTO(): TO where TO : DTO {
    return Mapper.map(this, TO::class.java)
}

public inline fun <reified TO> Iterable<Any>.toDTO(): List<TO> where TO : DTO {
    return Mapper.mapList(this, TO::class.java)
}

class Mapper {
    companion object {
        val mapping: MutableMap<Class<*>, MutableMap<Class<*>, Function<*, *>>> = HashMap()
        public inline fun <reified FROM, reified TO> registerMapping(noinline fn: (FROM) -> TO) where TO : DTO {
            val fromMap = mapping.computeIfAbsent(FROM::class.java as Class<*>) { HashMap() }
            fromMap[TO::class.java] = fn as Function<*, *>
        }

        public fun <FROM : Iterable<Any>, TO> mapList(from: FROM, toCls: Class<TO>): List<TO> where TO : DTO {
            return from.map { map(it, toCls) }
        }

        public fun <FROM : Any, TO> map(from: FROM, toCls: Class<TO>): TO where TO : DTO {
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
                        "skrevetAv" to melding.skrevetAv?.let(::hentPerson),
                        "journalfortAv" to melding.journalfortAv?.let(::hentPerson),
                        "journalfortDato" to melding.journalfortDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
                        "journalfortTema" to melding.journalfortTema,
                        "journalfortTemanavn" to melding.journalfortTemanavn,
                        "journalfortSaksid" to melding.journalfortSaksId,
                        "fritekst" to melding.fritekst,
                        "lestDato" to melding.lestDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
                        "status" to melding.status?.name,
                        "opprettetDato" to melding.opprettetDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
                        "ferdigstiltDato" to melding.ferdigstiltDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
                        "erFerdigstiltUtenSvar" to melding.erFerdigstiltUtenSvar,
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