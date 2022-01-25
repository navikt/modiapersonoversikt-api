package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.GjelderDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.KodeverkkombinasjonDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.OppgavetypeDTO
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk

class OppgaveKodeverkProvider(
    private val oppgaveKodeverk: KodeverkApi
) : EnhetligKodeverk.KodeverkProvider<String, Tema> {

    override fun hentKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk<String, Tema> {
        val respons = oppgaveKodeverk.hentInterntKodeverk(getCallId())
        return EnhetligKodeverk.Kodeverk(kodeverkNavn, parseTilKodeverk(respons))
    }

    private fun parseTilKodeverk(respons: List<KodeverkkombinasjonDTO>): Map<String, Tema> {
        return respons.map {
            Tema(
                kode = it.tema.tema,
                tekst = it.tema.term,
                oppgavetyper = hentOppgavetyper(it.oppgavetyper),
                prioriteter = TODO(),
                underkategorier = hentUnderkategorier(it.gjelderverdier)
            )
        }.associateBy { it.kode }
    }

    private fun hentUnderkategorier(gjelderverdier: List<GjelderDTO>?): List<Underkategori> {
        return gjelderverdier?.map {
            Underkategori(
                kode = listOf(it.behandlingstema, it.behandlingstype).joinToString(":") { it ?: "" },
                tekst = listOfNotNull(it.behandlingstemaTerm, it.behandlingstypeTerm).joinToString(" - "),
                erGyldig = true
            )
        }
            ?: emptyList()
    }

    private fun hentOppgavetyper(oppgavetyper: List<OppgavetypeDTO>): List<Oppgavetype> {
        return oppgavetyper.map {
            Oppgavetype(
                kode = it.oppgavetype,
                tekst = it.term,
                dagerFrist = TODO()
            )
        }
    }

    companion object {
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

data class Prioritet(
    val kode: String,
    val tekst: String
)

data class Underkategori(
    val kode: String,
    val tekst: String,
    val erGyldig: Boolean
)
