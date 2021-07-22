package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.legacy.api.domain.Kanal
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.HenvendelseBehandlingApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.HenvendelseInfoApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.JournalApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.NyHenvendelseApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.infrastructure.RequestConfig
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.infrastructure.RequestMethod
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.JournalRequestDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingRequestDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.SamtalereferatRequestDTO
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import org.slf4j.MDC
import java.time.OffsetDateTime
import java.util.*
import kotlin.reflect.KProperty1

sealed class EksternBruker(val ident: String) {
    class AktorId(aktorId: String) : EksternBruker(aktorId)
    class Fnr(fnr: String) : EksternBruker(fnr)
}

interface SfHenvendelseApi {
    fun hentHenvendelser(bruker: EksternBruker): List<HenvendelseDTO>
    fun journalforHenvendelse(enhet: String, henvendelseId: String, saksId: String?, saksTema: String)
    fun sendSamtalereferat(bruker: EksternBruker, enhet: String, temagruppe: Temagruppe, kanal: Kanal, fritekst: String)
    fun sendMeldingPaNyTrad(bruker: EksternBruker, enhet: String, temagruppe: Temagruppe, fritekst: String)
    fun sendMeldingPaEksisterendeTrad(
        bruker: EksternBruker,
        kjedeId: String,
        enhet: String,
        temagruppe: Temagruppe,
        fritekst: String
    )

    fun leggTilKontorsperre(henvendelseId: String, enhet: String)
    fun markerSomFeilsendt(henvendelseId: String)
    fun markerForHastekassering(henvendelseId: String)
    fun markerSomOversendtTilBisys(enhet: String, henvendelseId: String)
}

class SfHenvendelseApiImpl(
    private val henvendelseBehandlingApi: HenvendelseBehandlingApi = SfHenvendelseApiFactory.createHenvendelseBehandlingApi(),
    private val henvendelseInfoApi: HenvendelseInfoApi = SfHenvendelseApiFactory.createHenvendelseInfoApi(),
    private val henvendelseJournalApi: JournalApi = SfHenvendelseApiFactory.createHenvendelseJournalApi(),
    private val henvendelseOpprettApi: NyHenvendelseApi = SfHenvendelseApiFactory.createHenvendelseOpprettApi(),
    private val pdlOppslagService: PdlOppslagService
) : SfHenvendelseApi {

    override fun hentHenvendelser(bruker: EksternBruker): List<HenvendelseDTO> {
        return henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(bruker.aktorId(), callId())
    }

    override fun journalforHenvendelse(enhet: String, henvendelseId: String, saksId: String?, saksTema: String) {
        henvendelseJournalApi
            .henvendelseJournalPost(
                callId(),
                JournalRequestDTO(
                    henvendelseId = henvendelseId,
                    saksId = saksId,
                    temakode = saksTema,
                    journalforendeEnhet = enhet
                )
            )
    }

    override fun sendSamtalereferat(
        bruker: EksternBruker,
        enhet: String,
        temagruppe: Temagruppe,
        kanal: Kanal,
        fritekst: String
    ) {
        henvendelseOpprettApi
            .henvendelseNySamtalereferatPost(
                callId(),
                SamtalereferatRequestDTO(
                    aktorId = bruker.aktorId(),
                    temagruppe = temagruppe.name,
                    enhet = enhet,
                    kanal = SamtalereferatRequestDTO.Kanal.valueOf(kanal.name),
                    fritekst = fritekst
                )
            )
    }

    override fun sendMeldingPaNyTrad(
        bruker: EksternBruker,
        enhet: String,
        temagruppe: Temagruppe,
        fritekst: String
    ) {
        henvendelseOpprettApi
            .henvendelseNyMeldingPost(
                callId(),
                kjedeId = null,
                meldingRequestDTO = MeldingRequestDTO(
                    aktorId = bruker.aktorId(),
                    temagruppe = temagruppe.name,
                    enhet = enhet,
                    fritekst = fritekst
                )
            )
    }

    override fun sendMeldingPaEksisterendeTrad(
        bruker: EksternBruker,
        kjedeId: String,
        enhet: String,
        temagruppe: Temagruppe,
        fritekst: String
    ) {
        henvendelseOpprettApi
            .henvendelseNyMeldingPost(
                callId(),
                kjedeId = kjedeId,
                meldingRequestDTO = MeldingRequestDTO(
                    aktorId = bruker.aktorId(),
                    temagruppe = temagruppe.name,
                    enhet = enhet,
                    fritekst = fritekst
                )
            )
    }

    override fun leggTilKontorsperre(henvendelseId: String, enhet: String) {
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            henvendelseId,
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::kasseringsDato).to(null)
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request)
    }

    override fun markerSomFeilsendt(henvendelseId: String) {
        val callId = callId()
        val henvendelse = henvendelseInfoApi.henvendelseinfoHenvendelseGet(henvendelseId, callId)
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            henvendelseId,
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::kasseringsDato).to(henvendelse.opprettetDato.plusYears(2))
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request)
    }

    override fun markerForHastekassering(henvendelseId: String) {
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            henvendelseId,
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::kasseringsDato).to(OffsetDateTime.now())
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request)
    }

    override fun markerSomOversendtTilBisys(enhet: String, henvendelseId: String) {
        // TODO vi trenger sannsynligvis ikke spesialhåndtering fra frontend siden apiet ser ok ut for oss nå
        henvendelseJournalApi
            .henvendelseJournalPost(
                callId(),
                JournalRequestDTO(
                    henvendelseId = henvendelseId,
                    saksId = null,
                    temakode = "BID",
                    journalforendeEnhet = enhet
                )
            )
    }

    private fun createPatchRequest(
        henvendelseId: String,
        patchnote: PatchNote<HenvendelseDTO>
    ): RequestConfig<Map<String, Any?>> {
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf(
            "X-Correlation-ID" to callId()
        )

        patchnote.patches.mapKeys { it.key.name }
        return RequestConfig(
            method = RequestMethod.PATCH,
            path = "/henvendelse/behandling/$henvendelseId",
            query = mutableMapOf(),
            headers = localVariableHeaders,
            body = patchnote.patches.mapKeys { it.key.name }
        )
    }

    internal class PatchNote<CLS> {
        val patches: MutableMap<KProperty1<CLS, Any?>, Any?> = mutableMapOf()
        fun <TYPE> set(field: KProperty1<CLS, TYPE>) = PatchNoteEntry(this, field)

        internal class PatchNoteEntry<CLS, TYPE>(private val collector: PatchNote<CLS>, val field: KProperty1<CLS, TYPE>) {
            fun to(value: TYPE): PatchNote<CLS> {
                collector.patches[field] = value
                return collector
            }
        }
    }

    fun EksternBruker.aktorId(): String {
        return when (this) {
            is EksternBruker.AktorId -> this.ident
            is EksternBruker.Fnr -> requireNotNull(pdlOppslagService.hentAktorId(this.ident)) {
                "Fant ikke aktørid for ${this.ident}"
            }
        }
    }

    fun callId(): String = MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
}

object SfHenvendelseApiFactory {
    private val url = getRequiredProperty("SF_HENVENDELSE_URL")
    private val client = RestClient.baseClient().newBuilder()
        .addInterceptor(
            LoggingInterceptor("SF-Henvendelse") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .addInterceptor(
            AuthorizationInterceptor {
                SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
                    .orElseThrow { IllegalStateException("Fant ikke OIDC-token") }
            }
        )
        .build()

    fun createHenvendelseBehandlingApi() = HenvendelseBehandlingApi(url, client)
    fun createHenvendelseInfoApi() = HenvendelseInfoApi(url, client)
    fun createHenvendelseJournalApi() = JournalApi(url, client)
    fun createHenvendelseOpprettApi() = NyHenvendelseApi(url, client)
}
