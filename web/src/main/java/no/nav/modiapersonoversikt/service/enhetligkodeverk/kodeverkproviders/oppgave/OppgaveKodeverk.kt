package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave

import com.fasterxml.jackson.annotation.JsonFormat
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.oppgave.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.GjelderDTO
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.KodeverkkombinasjonDTO
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.OppgavetypeDTO
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk

object OppgaveKodeverk {

    class Provider(
        private val systemUserTokenProvider: SystemUserTokenProvider,
        val oppgaveKodeverk: KodeverkApi = createKodeverkApi(systemUserTokenProvider)
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

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    enum class Prioritet(
        val kode: String,
        val tekst: String
    ) {
        HOY("HOY", "Høy"),
        NORM("NORM", "Normal"),
        LAV("LAV", "Lav")
    }

    data class Underkategori(
        val kode: String,
        val tekst: String,
        val erGyldig: Boolean
    )

    fun createKodeverkApi(systemUserTokenProvider: SystemUserTokenProvider): KodeverkApi {
        val url = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
        val client = RestClient.baseClient().newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(
                LoggingInterceptor("OppgaveKodeverk") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                }
            )
            .addInterceptor(
                AuthorizationInterceptor {
                    systemUserTokenProvider.systemUserToken
                }
            )
            .build()

        return KodeverkApi(url, client)
    }

    internal fun parseTilKodeverk(respons: List<KodeverkkombinasjonDTO>): Map<String, Tema> {
        return respons.filter { !OppgaveOverstyring.underkjenteTemaer.contains(it.tema.tema) }.map {
            Tema(
                kode = it.tema.tema,
                tekst = it.tema.term,
                oppgavetyper = hentOppgavetyper(it.oppgavetyper, it.tema.tema),
                prioriteter = hentPrioriteter(it),
                underkategorier = hentUnderkategorier(it.gjelderverdier)
            )
        }.sortedBy { it.tekst }.associateBy { it.kode }
    }

    internal fun hentPrioriteter(oppgaveKodeverk: KodeverkkombinasjonDTO): List<Prioritet> {
        return OppgaveOverstyring.overstyrtKodeverk.tema[oppgaveKodeverk.tema.tema]?.prioriteter
            ?: OppgaveOverstyring.overstyrtKodeverk.prioriteter
    }

    internal fun hentUnderkategorier(gjelderverdier: List<GjelderDTO>?): List<Underkategori> {
        return gjelderverdier?.map {
            Underkategori(
                kode = listOf(it.behandlingstema, it.behandlingstype).joinToString(":") { it ?: "" },
                tekst = listOfNotNull(it.behandlingstemaTerm, it.behandlingstypeTerm).joinToString(" - "),
                erGyldig = true
            )
        }?.sortedBy { it.tekst }
            ?: emptyList()
    }

    internal fun hentOppgavetyper(oppgavetyper: List<OppgavetypeDTO>, tema: String): List<Oppgavetype> {
        return oppgavetyper.filter { OppgaveOverstyring.godkjenteOppgavetyper.contains(it.oppgavetype) }.map {
            Oppgavetype(
                kode = it.oppgavetype,
                tekst = it.term,
                dagerFrist = hentFrist(tema, it.oppgavetype)
            )
        }.sortedBy { it.tekst }
    }

    internal fun hentFrist(tema: String, oppgavetype: String): Int {
        return OppgaveOverstyring.overstyrtKodeverk.tema[tema]?.oppgavetyper?.get(oppgavetype)?.frist
            ?: OppgaveOverstyring.overstyrtKodeverk.frist
    }
}
