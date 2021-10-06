package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.*
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.infrastructure.RequestConfig
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.infrastructure.RequestMethod
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.JournalRequestDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingRequestDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.SamtalereferatRequestDTO
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import okhttp3.OkHttpClient
import java.time.OffsetDateTime
import kotlin.reflect.KProperty1

sealed class EksternBruker(val ident: String) {
    class AktorId(aktorId: String) : EksternBruker(aktorId)
    class Fnr(fnr: String) : EksternBruker(fnr)
}

interface SfHenvendelseService {
    fun hentHenvendelser(bruker: EksternBruker, enhet: String): List<HenvendelseDTO>
    fun hentHenvendelse(kjedeId: String): HenvendelseDTO
    fun journalforHenvendelse(enhet: String, kjedeId: String, saksId: String?, saksTema: String)
    fun sendSamtalereferat(bruker: EksternBruker, enhet: String, temagruppe: String, kanal: SamtalereferatRequestDTO.Kanal, fritekst: String)
    fun opprettNyDialogOgSendMelding(
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        fritekst: String
    ): HenvendelseDTO
    fun sendMeldingPaEksisterendeDialog(
        bruker: EksternBruker,
        kjedeId: String,
        enhet: String,
        fritekst: String
    ): HenvendelseDTO

    fun henvendelseTilhorerBruker(bruker: EksternBruker, kjedeId: String): Boolean
    fun sjekkEierskap(bruker: EksternBruker, henvendelse: HenvendelseDTO): Boolean
    fun merkSomKontorsperret(kjedeId: String, enhet: String)
    fun merkSomFeilsendt(kjedeId: String)
    fun merkForHastekassering(kjedeId: String)
    fun lukkTraad(kjedeId: String)

    fun ping()
}

class SfHenvendelseServiceImpl(
    private val henvendelseBehandlingApi: HenvendelseBehandlingApi = SfHenvendelseApiFactory.createHenvendelseBehandlingApi(),
    private val henvendelseInfoApi: HenvendelseInfoApi = SfHenvendelseApiFactory.createHenvendelseInfoApi(),
    private val henvendelseJournalApi: JournalApi = SfHenvendelseApiFactory.createHenvendelseJournalApi(),
    private val henvendelseOpprettApi: NyHenvendelseApi = SfHenvendelseApiFactory.createHenvendelseOpprettApi(),
    private val pdlOppslagService: PdlOppslagService,
    private val stsService: SystemUserTokenProvider
) : SfHenvendelseService {
    private val adminKodeverkApiForPing = KodeverkApi(
        SfHenvendelseApiFactory.url,
        SfHenvendelseApiFactory.createClient {
            stsService.systemUserToken
        }
    )

    constructor(pdlOppslagService: PdlOppslagService, stsService: SystemUserTokenProvider) : this(
        SfHenvendelseApiFactory.createHenvendelseBehandlingApi(),
        SfHenvendelseApiFactory.createHenvendelseInfoApi(),
        SfHenvendelseApiFactory.createHenvendelseJournalApi(),
        SfHenvendelseApiFactory.createHenvendelseOpprettApi(),
        pdlOppslagService,
        stsService
    )

    override fun hentHenvendelser(bruker: EksternBruker, enhet: String): List<HenvendelseDTO> {
        return henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(bruker.aktorId(), getCallId())
    }

    override fun hentHenvendelse(kjedeId: String): HenvendelseDTO {
        return henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(kjedeId, getCallId())
    }

    override fun journalforHenvendelse(enhet: String, kjedeId: String, saksId: String?, saksTema: String) {
        henvendelseJournalApi
            .henvendelseJournalPost(
                getCallId(),
                JournalRequestDTO(
                    kjedeId = kjedeId,
                    saksId = saksId,
                    temakode = saksTema,
                    journalforendeEnhet = enhet
                )
            )
    }

    override fun sendSamtalereferat(
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        kanal: SamtalereferatRequestDTO.Kanal,
        fritekst: String
    ) {
        henvendelseOpprettApi
            .henvendelseNySamtalereferatPost(
                getCallId(),
                SamtalereferatRequestDTO(
                    aktorId = bruker.aktorId(),
                    temagruppe = temagruppe,
                    enhet = enhet,
                    kanal = kanal,
                    fritekst = fritekst
                ),
                null // Skal alltid være enkeltstående samtalereferat
            )
    }

    override fun opprettNyDialogOgSendMelding(
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        fritekst: String
    ): HenvendelseDTO {
        return henvendelseOpprettApi
            .henvendelseNyMeldingPost(
                getCallId(),
                kjedeId = null,
                meldingRequestDTO = MeldingRequestDTO(
                    aktorId = bruker.aktorId(),
                    temagruppe = temagruppe,
                    enhet = enhet,
                    fritekst = fritekst
                )
            )
    }

    override fun sendMeldingPaEksisterendeDialog(
        bruker: EksternBruker,
        kjedeId: String,
        enhet: String,
        fritekst: String
    ): HenvendelseDTO {
        val callId = getCallId()
        val henvendelse = henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(kjedeId, callId)
        val kjedeTilhorerBruker = sjekkEierskap(bruker, henvendelse)
        if (!kjedeTilhorerBruker) {
            throw IllegalStateException("Henvendelse $kjedeId tilhørte ikke bruker")
        }
        return henvendelseOpprettApi
            .henvendelseNyMeldingPost(
                callId,
                kjedeId = kjedeId,
                meldingRequestDTO = MeldingRequestDTO(
                    aktorId = bruker.aktorId(),
                    temagruppe = henvendelse.gjeldendeTemagruppe,
                    enhet = enhet,
                    fritekst = fritekst
                )
            )
    }

    override fun henvendelseTilhorerBruker(bruker: EksternBruker, kjedeId: String): Boolean {
        val henvendelse = henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(kjedeId, getCallId())
        return sjekkEierskap(bruker, henvendelse)
    }

    override fun sjekkEierskap(bruker: EksternBruker, henvendelse: HenvendelseDTO): Boolean {
        return when (bruker) {
            is EksternBruker.Fnr -> bruker.ident == henvendelse.fnr
            is EksternBruker.AktorId -> bruker.ident == henvendelse.aktorId
        }
    }

    override fun merkSomKontorsperret(kjedeId: String, enhet: String) {
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            kjedeId,
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::kontorsperre).to(true)
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request)
    }

    override fun merkSomFeilsendt(kjedeId: String) {
        val callId = getCallId()
        val henvendelse = henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(kjedeId, callId)
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            kjedeId,
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::kasseringsDato).to(henvendelse.opprettetDato.plusYears(2))
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request)
    }

    // TODO mulig denne forsvinner fra modia, da brukerstøtte skal kunne gjøre sletting fra SF
    override fun merkForHastekassering(kjedeId: String) {
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            kjedeId,
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::kasseringsDato).to(OffsetDateTime.now())
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request)
    }

    override fun lukkTraad(kjedeId: String) {
        henvendelseBehandlingApi.henvendelseMeldingskjedeLukkPatch(
            kjedeId,
            getCallId()
        )
    }

    override fun ping() {
        adminKodeverkApiForPing.henvendelseKodeverkTemagrupperGet(getCallId())
    }

    private fun createPatchRequest(
        kjedeId: String,
        patchnote: PatchNote<HenvendelseDTO>
    ): RequestConfig<Map<String, Any?>> {
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf(
            "X-Correlation-ID" to getCallId()
        )

        patchnote.patches.mapKeys { it.key.name }
        return RequestConfig(
            method = RequestMethod.PATCH,
            path = "/henvendelse/behandling/$kjedeId",
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

    private fun EksternBruker.aktorId(): String {
        return when (this) {
            is EksternBruker.AktorId -> this.ident
            is EksternBruker.Fnr -> requireNotNull(pdlOppslagService.hentAktorId(this.ident)) {
                "Fant ikke aktørid for ${this.ident}"
            }
        }
    }
}

object SfHenvendelseApiFactory {
    internal val url = getRequiredProperty("SF_HENVENDELSE_URL")
    private val client = createClient {
        SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
            .orElseThrow { IllegalStateException("Fant ikke OIDC-token") }
    }

    fun createClient(tokenProvider: () -> String): OkHttpClient = RestClient.baseClient().newBuilder()
        .addInterceptor(
            LoggingInterceptor("SF-Henvendelse") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .addInterceptor(
            AuthorizationInterceptor(tokenProvider)
        )
        .build()
    fun createHenvendelseBehandlingApi() = HenvendelseBehandlingApi(url, client)
    fun createHenvendelseInfoApi() = HenvendelseInfoApi(url, client)
    fun createHenvendelseJournalApi() = JournalApi(url, client)
    fun createHenvendelseOpprettApi() = NyHenvendelseApi(url, client)
}
