package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave

import com.fasterxml.jackson.core.type.TypeReference
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.infrastructure.Serializer
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.GjelderDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.KodeverkkombinasjonDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.OppgavetypeDTO
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import java.nio.file.Files
import java.nio.file.Path

object OppgaveKodeverk {

    class Provider(
        private val oppgaveKodeverk: KodeverkApi = createKodeverkApi()
    ) : EnhetligKodeverk.KodeverkProvider<String, Tema> {

        override fun hentKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk<String, Tema> {
            val respons = oppgaveKodeverk.hentInterntKodeverk(getCallId())
            return EnhetligKodeverk.Kodeverk(kodeverkNavn, parseTilKodeverk(respons))
        }
    }

    data class Tema(
        val kode: String,
        val tekst: String,
        val oppgavetyper: List<Oppgavetype>,
        val prioriteter: List<Prioritet>,
        val underkategorier: List<Underkategori>
    )

    data class Oppgavetype(
        val kode: String,
        val tekst: String,
        val dagerFrist: Int
    )

    enum class Prioritet(
        val tekst: String
    ) {
        HOY("Høy"),
        NORM("Normal"),
        LAV("Lav")
    }

    data class Underkategori(
        val kode: String,
        val tekst: String,
        val erGyldig: Boolean
    )

    fun createKodeverkApi(): KodeverkApi {
        val url = EnvironmentUtils.getRequiredProperty("OPPGAVE_KODEVERK_URL")
        val client = RestClient.baseClient().newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(
                LoggingInterceptor("OppgaveKodeverk") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                }
            )
            .build()

        return KodeverkApi(url, client)
    }

    internal fun parseTilKodeverk(respons: List<KodeverkkombinasjonDTO>): Map<String, Tema> {
        return respons.map {
            Tema(
                kode = it.tema.tema,
                tekst = it.tema.term,
                oppgavetyper = hentOppgavetyper(it.oppgavetyper, it.tema.tema),
                prioriteter = hentPrioriteter(it),
                underkategorier = hentUnderkategorier(it.gjelderverdier)
            )
        }.associateBy { it.kode }
    }

    internal fun hentPrioriteter(oppgaveKodeverk: KodeverkkombinasjonDTO): List<Prioritet> {
        return overstyrtOppgaveKodeverk.tema[oppgaveKodeverk.tema.tema]?.prioriteter
            ?: overstyrtOppgaveKodeverk.prioriteter
    }

    internal fun hentUnderkategorier(gjelderverdier: List<GjelderDTO>?): List<Underkategori> {
        return gjelderverdier?.map {
            Underkategori(
                kode = listOf(it.behandlingstema, it.behandlingstype).joinToString(":") { it ?: "" },
                tekst = listOfNotNull(it.behandlingstemaTerm, it.behandlingstypeTerm).joinToString(" - "),
                erGyldig = true
            )
        }
            ?: emptyList()
    }

    internal fun hentOppgavetyper(oppgavetyper: List<OppgavetypeDTO>, tema: String): List<Oppgavetype> {
        return oppgavetyper.map {
            Oppgavetype(
                kode = it.oppgavetype,
                tekst = it.term,
                dagerFrist = hentFrist(tema, it.oppgavetype)
            )
        }
    }

    internal fun hentFrist(tema: String, oppgavetype: String): Int {
        return overstyrtOppgaveKodeverk.tema[tema]?.oppgavetyper?.get(oppgavetype)?.frist
            ?: overstyrtOppgaveKodeverk.frist
    }
}

fun main() {
    val fil = Files.readString(Path.of("/Users/eirikdahlen/Documents/code/modiapersonoversikt-api/web/src/main/java/no/nav/modiapersonoversikt/service/enhetligkodeverk/kodeverkproviders/oppgave/oppgave-kodeverk.json"))
    val type = object : TypeReference<List<KodeverkkombinasjonDTO>>() {}
    val parsed = Serializer.jacksonObjectMapper.readValue(fil, type)
    val respons = OppgaveKodeverk.parseTilKodeverk(parsed)
    val serializedJson = Serializer.jacksonObjectMapper.writeValueAsString(respons)
    println(serializedJson)
}
