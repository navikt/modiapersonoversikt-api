package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.types.identer.EnhetId
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.*
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.infrastructure.*
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.*
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.utils.isNotNullOrEmpty
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import kotlin.reflect.KProperty1

sealed class EksternBruker(val ident: String) {
    data class AktorId(val aktorId: String) : EksternBruker(aktorId)
    data class Fnr(val fnr: String) : EksternBruker(fnr)
}

interface SfHenvendelseService {
    fun hentHenvendelser(bruker: EksternBruker, enhet: String): List<HenvendelseDTO>
    fun hentHenvendelse(kjedeId: String): HenvendelseDTO
    fun journalforHenvendelse(enhet: String, kjedeId: String, saksTema: String, saksId: String?, fagsakSystem: String?)
    fun sendSamtalereferat(
        kjedeId: String?,
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        kanal: SamtalereferatRequestDTO.Kanal,
        fritekst: String
    ): HenvendelseDTO

    fun opprettNyDialogOgSendMelding(
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        tilknyttetAnsatt: Boolean,
        fritekst: String
    ): HenvendelseDTO
    fun sendMeldingPaEksisterendeDialog(
        bruker: EksternBruker,
        kjedeId: String,
        enhet: String,
        tilknyttetAnsatt: Boolean,
        fritekst: String
    ): HenvendelseDTO

    fun henvendelseTilhorerBruker(bruker: EksternBruker, kjedeId: String): Boolean
    fun sjekkEierskap(bruker: EksternBruker, henvendelse: HenvendelseDTO): Boolean
    fun merkSomFeilsendt(kjedeId: String)
    fun sendTilSladding(kjedeId: String)
    fun lukkTraad(kjedeId: String)

    fun ping()
}

private val logger = LoggerFactory.getLogger(SfHenvendelseServiceImpl::class.java)
class SfHenvendelseServiceImpl(
    private val henvendelseBehandlingApi: HenvendelseBehandlingApi = SfHenvendelseApiFactory.createHenvendelseBehandlingApi(),
    private val henvendelseInfoApi: HenvendelseInfoApi = SfHenvendelseApiFactory.createHenvendelseInfoApi(),
    private val henvendelseJournalApi: JournalApi = SfHenvendelseApiFactory.createHenvendelseJournalApi(),
    private val henvendelseOpprettApi: NyHenvendelseApi = SfHenvendelseApiFactory.createHenvendelseOpprettApi(),
    private val pdlOppslagService: PdlOppslagService,
    private val norgApi: NorgApi,
    private val ansattService: AnsattService,
    private val stsService: SystemUserTokenProvider
) : SfHenvendelseService {
    private val adminKodeverkApiForPing = KodeverkApi(
        SfHenvendelseApiFactory.url(),
        SfHenvendelseApiFactory.createClient {
            stsService.systemUserToken
        }
    )

    constructor(
        pdlOppslagService: PdlOppslagService,
        norgApi: NorgApi,
        ansattService: AnsattService,
        stsService: SystemUserTokenProvider
    ) : this(
        SfHenvendelseApiFactory.createHenvendelseBehandlingApi(),
        SfHenvendelseApiFactory.createHenvendelseInfoApi(),
        SfHenvendelseApiFactory.createHenvendelseJournalApi(),
        SfHenvendelseApiFactory.createHenvendelseOpprettApi(),
        pdlOppslagService,
        norgApi,
        ansattService,
        stsService
    )

    override fun hentHenvendelser(bruker: EksternBruker, enhet: String): List<HenvendelseDTO> {
        val enhetOgGTListe = norgApi
            .runCatching {
                hentGeografiskTilknyttning(EnhetId(enhet))
            }
            .onFailure { logger.error("Kunne ikke hente geografisk tilknyttning", it) }
            .getOrDefault(emptyList())
            .mapNotNull { it.geografiskOmraade }
            .plus(enhet)
        val tematilganger = ansattService.hentAnsattFagomrader(
            AuthContextUtils.requireIdent(),
            enhet
        )

        return henvendelseInfoApi
            .henvendelseinfoHenvendelselisteGet(bruker.aktorId(), getCallId())
            .let { loggFeilSomErSpesialHandtert(bruker, it) }
            .filter(kontorsperreTilgang(enhetOgGTListe))
            .map(kassertInnhold(OffsetDateTime.now()))
            .map(journalfortTemaTilgang(tematilganger))
            .map(::sorterMeldinger)
    }

    override fun hentHenvendelse(kjedeId: String): HenvendelseDTO {
        return henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(kjedeId.fixKjedeId(), getCallId())
    }

    override fun journalforHenvendelse(
        enhet: String,
        kjedeId: String,
        saksTema: String,
        saksId: String?,
        fagsakSystem: String?
    ) {
        val fagsaksystem = if (saksId != null) {
            JournalRequestDTO.Fagsaksystem.valueOf(
                requireNotNull(fagsakSystem) {
                    "Ved journalføring mot $saksId er det påkrevd å sende med fagsakSystem saken kommer fra"
                }
            )
        } else {
            null
        }
        henvendelseJournalApi
            .henvendelseJournalPost(
                getCallId(),
                JournalRequestDTO(
                    journalforendeEnhet = enhet,
                    kjedeId = kjedeId.fixKjedeId(),
                    temakode = saksTema,
                    fagsakId = if (saksTema == "BID") null else saksId,
                    fagsaksystem = if (saksTema == "BID") null else fagsaksystem,
                )
            )
    }

    override fun sendSamtalereferat(
        kjedeId: String?,
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        kanal: SamtalereferatRequestDTO.Kanal,
        fritekst: String
    ): HenvendelseDTO {
        return henvendelseOpprettApi
            .henvendelseNySamtalereferatPost(
                xCorrelationID = getCallId(),
                samtalereferatRequestDTO = SamtalereferatRequestDTO(
                    aktorId = bruker.aktorId(),
                    temagruppe = temagruppe,
                    enhet = enhet,
                    kanal = kanal,
                    fritekst = fritekst
                ),
                kjedeId = kjedeId?.fixKjedeId()
            )
    }

    override fun opprettNyDialogOgSendMelding(
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        tilknyttetAnsatt: Boolean,
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
                    tildelMeg = tilknyttetAnsatt,
                    fritekst = fritekst
                )
            )
    }

    override fun sendMeldingPaEksisterendeDialog(
        bruker: EksternBruker,
        kjedeId: String,
        enhet: String,
        tilknyttetAnsatt: Boolean,
        fritekst: String
    ): HenvendelseDTO {
        val callId = getCallId()
        val henvendelse = henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(kjedeId.fixKjedeId(), callId)
        val kjedeTilhorerBruker = sjekkEierskap(bruker, henvendelse)
        if (!kjedeTilhorerBruker) {
            throw IllegalStateException("Henvendelse $kjedeId tilhørte ikke bruker")
        }
        return henvendelseOpprettApi
            .henvendelseNyMeldingPost(
                callId,
                kjedeId = kjedeId.fixKjedeId(),
                meldingRequestDTO = MeldingRequestDTO(
                    aktorId = bruker.aktorId(),
                    temagruppe = henvendelse.gjeldendeTemagruppe!!, // TODO må fikses av SF-api. Temagruppe kan ikke være null
                    enhet = enhet,
                    fritekst = fritekst,
                    tildelMeg = tilknyttetAnsatt
                )
            )
    }

    override fun henvendelseTilhorerBruker(bruker: EksternBruker, kjedeId: String): Boolean {
        val henvendelse = henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(kjedeId.fixKjedeId(), getCallId())
        return sjekkEierskap(bruker, henvendelse)
    }

    override fun sjekkEierskap(bruker: EksternBruker, henvendelse: HenvendelseDTO): Boolean {
        return when (bruker) {
            is EksternBruker.Fnr -> bruker.ident == henvendelse.fnr
            is EksternBruker.AktorId -> bruker.ident == henvendelse.aktorId
        }
    }

    override fun merkSomFeilsendt(kjedeId: String) {
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            kjedeId.fixKjedeId(),
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::feilsendt).to(true)
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request).throwIfError()
    }

    override fun sendTilSladding(kjedeId: String) {
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            kjedeId.fixKjedeId(),
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::sladding).to(true)
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request).throwIfError()
    }

    override fun lukkTraad(kjedeId: String) {
        henvendelseBehandlingApi.henvendelseMeldingskjedeLukkPost(
            kjedeId.fixKjedeId(),
            getCallId()
        )
    }

    override fun ping() {
        adminKodeverkApiForPing.henvendelseKodeverkTemagrupperGet(getCallId())
    }

    enum class ApiFeilType {
        IDENT, TEMAGRUPPE, JOURNALFORENDE_IDENT, MARKERT_DATO, MARKERT_AV, FRITEKST, TOM_TRAD, CHAT, DUPLIKAT_JOURNALPOST
    }

    data class ApiFeil(val type: ApiFeilType, val kjedeId: String)

    private fun loggFeilSomErSpesialHandtert(
        bruker: EksternBruker,
        henvendelser: List<HenvendelseDTO>
    ): List<HenvendelseDTO> {
        val feil = mutableListOf<ApiFeil>()
        val now = OffsetDateTime.now()
        for (henvendelse in henvendelser) {
            val meldinger = henvendelse.meldinger ?: emptyList()
            if (meldinger.any { it.fra.identType != MeldingFraDTO.IdentType.SYSTEM && it.fra.ident == null }) {
                feil.add(ApiFeil(ApiFeilType.IDENT, henvendelse.kjedeId))
            }
            if (henvendelse.gjeldendeTemagruppe == null) {
                feil.add(ApiFeil(ApiFeilType.TEMAGRUPPE, henvendelse.kjedeId))
            }
            if (henvendelse.meldinger.isNullOrEmpty()) {
                feil.add(ApiFeil(ApiFeilType.TOM_TRAD, henvendelse.kjedeId))
            }
            val journalposter = henvendelse.journalposter ?: emptyList()
            if (journalposter.any { it.journalforerNavIdent == null }) {
                feil.add(ApiFeil(ApiFeilType.JOURNALFORENDE_IDENT, henvendelse.kjedeId))
            }
            val unikeJournalposter = journalposter.distinctBy { Pair(it.journalfortTema, it.fagsakId) }
            if (unikeJournalposter.size != journalposter.size) {
                feil.add(ApiFeil(ApiFeilType.DUPLIKAT_JOURNALPOST, henvendelse.kjedeId))
            }

            val markeringer = henvendelse.markeringer ?: emptyList()
            if (markeringer.any { it.markertDato == null }) {
                feil.add(ApiFeil(ApiFeilType.MARKERT_DATO, henvendelse.kjedeId))
            }
            if (markeringer.any { it.markertAv == null }) {
                feil.add(ApiFeil(ApiFeilType.MARKERT_AV, henvendelse.kjedeId))
            }

            if (henvendelse.kasseringsDato?.isAfter(now) == true && meldinger.any { it.fritekst == null }) {
                feil.add(ApiFeil(ApiFeilType.FRITEKST, henvendelse.kjedeId))
            }
            if (henvendelse.henvendelseType == HenvendelseDTO.HenvendelseType.CHAT) {
                feil.add(ApiFeil(ApiFeilType.CHAT, henvendelse.kjedeId))
            }
        }
        val kanJobbesMedIModia = henvendelser
            .filter { it.gjeldendeTemagruppe != null }
            .filter { it.meldinger.isNotNullOrEmpty() }
            .filter { it.henvendelseType != HenvendelseDTO.HenvendelseType.CHAT }

        if (feil.isNotEmpty()) {
            val grupperteFeil = feil
                .groupBy { it.type }
                .mapValues { apifeil -> apifeil.value.map { it.kjedeId } }

            val sb = StringBuilder()
            sb.appendLine("[SF-HENVENDELSE]")
            sb.appendLine("Fant ${feil.size} feil i dataformat til henvendelser fra $bruker")
            for ((feiltype, kjedeIder) in grupperteFeil) {
                sb.appendLine("Type: $feiltype KjedeIder: ${kjedeIder.joinToString(", ")}")
            }

            logger.warn(sb.toString())
        }

        return kanJobbesMedIModia
    }

    private fun kontorsperreTilgang(enhetOgGTListe: List<String>): (HenvendelseDTO) -> Boolean {
        return { henvendelseDTO ->
            if (!henvendelseDTO.kontorsperre) {
                true
            } else {
                val sperre =
                    henvendelseDTO.markeringer?.find { it.markeringstype == MarkeringDTO.Markeringstype.KONTORSPERRE }
                val enhetEllerGT: String? = sperre?.kontorsperreGT ?: sperre?.kontorsperreEnhet
                enhetEllerGT == null || enhetOgGTListe.any { it == enhetEllerGT }
            }
        }
    }

    private fun kassertInnhold(now: OffsetDateTime): (HenvendelseDTO) -> HenvendelseDTO {
        return { henvendelseDTO ->
            if (henvendelseDTO.kasseringsDato != null && henvendelseDTO.kasseringsDato!!.isBefore(now)) {
                henvendelseDTO.copy(
                    meldinger = henvendelseDTO.meldinger?.map { melding ->
                        melding.copy(
                            fritekst = "Innholdet i denne henvendelsen er slettet av NAV."
                        )
                    }
                )
            } else {
                henvendelseDTO
            }
        }
    }

    private fun journalfortTemaTilgang(tematilganger: Set<String>): (HenvendelseDTO) -> HenvendelseDTO {
        return { henvendelseDTO ->
            val journalforteTemaer = (henvendelseDTO.journalposter ?: emptyList())
                .map { it.journalfortTema }
            val harTilgangTilAlleJournalforteTema: Boolean = journalforteTemaer
                .all { tema -> tematilganger.contains(tema) }

            if (harTilgangTilAlleJournalforteTema) {
                henvendelseDTO
            } else {
                val ident = AuthContextUtils.getIdent().orElse("-")
                logger.info(
                    """
                    Ikke tilgang til tema. 
                    Ident: $ident
                    Henvendelse: ${henvendelseDTO.kjedeId}
                    Journalførte: $journalforteTemaer
                    Tilganger: $tematilganger
                    """.trimIndent()
                )
                henvendelseDTO.copy(
                    meldinger = henvendelseDTO.meldinger?.map { melding ->
                        melding.copy(
                            fritekst = "Du kan ikke se innholdet i denne henvendelsen fordi tråden er journalført på et tema du ikke har tilgang til."
                        )
                    }
                )
            }
        }
    }

    private fun sorterMeldinger(henvendelse: HenvendelseDTO): HenvendelseDTO {
        return henvendelse.copy(
            meldinger = henvendelse.meldinger?.sortedBy { it.sendtDato }
        )
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
            /**
             * Var original PATCH og følger semantikken til PATCH, men endret til PUT pga støtte i sf-henvendelse proxy
             */
            method = RequestMethod.PUT,
            path = "/henvendelse/behandling/$kjedeId",
            query = mutableMapOf(),
            headers = localVariableHeaders,
            body = patchnote.patches.mapKeys { it.key.name }
        )
    }

    internal class PatchNote<CLS> {
        val patches: MutableMap<KProperty1<CLS, Any?>, Any?> = mutableMapOf()
        fun <TYPE> set(field: KProperty1<CLS, TYPE>) = PatchNoteEntry(this, field)

        internal class PatchNoteEntry<CLS, TYPE>(
            private val collector: PatchNote<CLS>,
            val field: KProperty1<CLS, TYPE>
        ) {
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

    private fun <T> ApiResponse<T>.throwIfError() {
        when (this.responseType) {
            ResponseType.ClientError -> {
                val localVarError = this as ClientError<*>
                throw ClientException(
                    "Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}",
                    localVarError.statusCode,
                    this
                )
            }
            ResponseType.ServerError -> {
                val localVarError = this as ServerError<*>
                throw ServerException(
                    "Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}",
                    localVarError.statusCode,
                    this
                )
            }
            ResponseType.Informational -> {
                throw UnsupportedOperationException("Client does not support Informational responses.")
            }
            ResponseType.Redirection -> {
                throw UnsupportedOperationException("Client does not support Redirection responses.")
            }
            ResponseType.Success -> {}
        }
    }
}

fun String.fixKjedeId(): String {
    val fragments = this.split("---")
    if (fragments.size > 1) {
        logger.warn("Mottok kjedeId med meldings-id-hack $this")
    }
    return fragments.first()
}

object SfHenvendelseApiFactory {
    fun url(): String = getRequiredProperty("SF_HENVENDELSE_URL")
    private val client = createClient {
        AuthContextUtils.requireToken()
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

    fun createHenvendelseBehandlingApi() = HenvendelseBehandlingApi(url(), client)
    fun createHenvendelseInfoApi() = HenvendelseInfoApi(url(), client)
    fun createHenvendelseJournalApi() = JournalApi(url(), client)
    fun createHenvendelseOpprettApi() = NyHenvendelseApi(url(), client)
}
