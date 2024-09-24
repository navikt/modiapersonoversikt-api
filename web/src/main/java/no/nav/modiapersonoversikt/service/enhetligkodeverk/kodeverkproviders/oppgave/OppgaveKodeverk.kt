package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave

import com.fasterxml.jackson.annotation.JsonFormat
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.oppgave.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.GjelderDTO
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.KodeverkkombinasjonDTO
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.OppgavetypeDTO
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveApiFactory
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

object OppgaveKodeverk {
    class Provider(
        private val machineToMachineTokenClient: MachineToMachineTokenClient,
        private val tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
        val oppgaveKodeverk: KodeverkApi =
            createKodeverkApi(
                machineToMachineTokenClient,
                tjenestekallLoggingInterceptorFactory,
            ),
    ) : EnhetligKodeverk.KodeverkProvider<String, Tema> {
        override fun hentKodeverk(kodeverkNavn: String): EnhetligKodeverk.Kodeverk<String, Tema> {
            val respons =
                oppgaveKodeverk.hentInterntKodeverk(getCallId()) ?: throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feil ved henting av kodverk.",
                )

            return EnhetligKodeverk.Kodeverk(kodeverkNavn, parseTilKodeverk(respons))
        }
    }

    data class Tema(
        val kode: String,
        val tekst: String,
        val oppgavetyper: List<Oppgavetype>,
        val prioriteter: List<Prioritet>,
        val underkategorier: List<Underkategori>,
    )

    data class Oppgavetype(
        val kode: String,
        val tekst: String,
        val dagerFrist: Int,
    )

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    enum class Prioritet(
        val kode: String,
        val tekst: String,
    ) {
        HOY("HOY", "Høy"),
        NORM("NORM", "Normal"),
        LAV("LAV", "Lav"),
    }

    data class Underkategori(
        val kode: String,
        val tekst: String,
        val erGyldig: Boolean,
    )

    fun createKodeverkApi(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): KodeverkApi {
        val client =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("OppgaveKodeverk") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        machineToMachineTokenClient.createMachineToMachineToken(OppgaveApiFactory.downstreamApi)
                    },
                ).build()

        return KodeverkApi(OppgaveApiFactory.url, client)
    }

    internal fun parseTilKodeverk(respons: List<KodeverkkombinasjonDTO>): Map<String, Tema> =
        respons
            .filter { !OppgaveOverstyring.underkjenteTemaer.contains(it.tema.tema) }
            .map {
                Tema(
                    kode = it.tema.tema,
                    tekst = it.tema.term,
                    oppgavetyper = hentOppgavetyper(it.oppgavetyper, it.tema.tema),
                    prioriteter = hentPrioriteter(it),
                    underkategorier = hentUnderkategorier(it.gjelderverdier),
                )
            }.sortedBy { it.tekst }
            .associateBy { it.kode }

    private fun hentPrioriteter(oppgaveKodeverk: KodeverkkombinasjonDTO): List<Prioritet> =
        OppgaveOverstyring.overstyrtKodeverk.tema[oppgaveKodeverk.tema.tema]?.prioriteter
            ?: OppgaveOverstyring.overstyrtKodeverk.prioriteter

    private fun hentUnderkategorier(gjelderverdier: List<GjelderDTO>?): List<Underkategori> =
        gjelderverdier
            ?.map { gjelder ->
                Underkategori(
                    kode = listOf(gjelder.behandlingstema, gjelder.behandlingstype).joinToString(":") { it ?: "" },
                    tekst = listOfNotNull(gjelder.behandlingstemaTerm, gjelder.behandlingstypeTerm).joinToString(" - "),
                    erGyldig = true,
                )
            }?.sortedBy { it.tekst }
            ?: emptyList()

    private fun hentOppgavetyper(
        oppgavetyper: List<OppgavetypeDTO>,
        tema: String,
    ): List<Oppgavetype> =
        oppgavetyper
            .filter { OppgaveOverstyring.godkjenteOppgavetyper.contains(it.oppgavetype) }
            .map {
                Oppgavetype(
                    kode = it.oppgavetype,
                    tekst = it.term,
                    dagerFrist = hentFrist(tema, it.oppgavetype),
                )
            }.sortedBy { it.tekst }

    private fun hentFrist(
        tema: String,
        oppgavetype: String,
    ): Int =
        OppgaveOverstyring.overstyrtKodeverk.tema[tema]
            ?.oppgavetyper
            ?.get(oppgavetype)
            ?.frist
            ?: OppgaveOverstyring.overstyrtKodeverk.frist
}
