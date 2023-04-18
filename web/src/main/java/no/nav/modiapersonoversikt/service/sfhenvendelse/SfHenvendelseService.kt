package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.rest.client.RestClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.apis.*
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.infrastructure.*
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.*
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory.createHenvendelseBehandlingApi
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory.createHenvendelseBehandlingProxyApi
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory.createHenvendelseInfoApi
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory.createHenvendelseInfoProxyApi
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory.createHenvendelseOpprettApi
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory.createHenvendelseOpprettProxyApi
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.BoundedMachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.isNotNullOrEmpty
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import kotlin.reflect.KProperty1
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

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

    fun sendTilSladding(kjedeId: String, arsak: String, meldingId: List<String>?)
    fun hentSladdeArsaker(kjedeId: String): List<String>
    fun lukkTraad(kjedeId: String)

    fun ping()
}

private val logger = LoggerFactory.getLogger(SfHenvendelseServiceImpl::class.java)

class SfHenvendelseServiceImpl(
    private val oboApiTokenClient: BoundedOnBehalfOfTokenClient,
    private val mtmApiTokenClient: BoundedMachineToMachineTokenClient,
    private val oboProxyApiTokenClient: BoundedOnBehalfOfTokenClient,
    private val mtmProxyApiTokenClient: BoundedMachineToMachineTokenClient,
    private val pdlOppslagService: PdlOppslagService,
    private val norgApi: NorgApi,
    private val ansattService: AnsattService,
    private val henvendelseBehandlingApi: HenvendelseBehandlingApi = createHenvendelseBehandlingApi(oboApiTokenClient),
    private val henvendelseInfoApi: HenvendelseInfoApi = createHenvendelseInfoApi(oboApiTokenClient),
    private val henvendelseOpprettApi: NyHenvendelseApi = createHenvendelseOpprettApi(oboApiTokenClient),
    private val henvendelseBehandlingProxyApi: HenvendelseBehandlingApi = createHenvendelseBehandlingProxyApi(oboProxyApiTokenClient),
    private val henvendelseInfoProxyApi: HenvendelseInfoApi = createHenvendelseInfoProxyApi(oboProxyApiTokenClient),
    private val henvendelseOpprettProxyApi: NyHenvendelseApi = createHenvendelseOpprettProxyApi(oboProxyApiTokenClient),
    private val unleashService: UnleashService
) : SfHenvendelseService {
    private val adminKodeverkApiForPing = KodeverkApi(
        SfHenvendelseApiFactory.url(),
        SfHenvendelseApiFactory.createClient {
            mtmApiTokenClient.createMachineToMachineToken()
        }
    )

    private val adminKodeverkProxyApiForPing = KodeverkApi(
        SfHenvendelseApiFactory.proxyUrl(),
        SfHenvendelseApiFactory.createClient {
            mtmProxyApiTokenClient.createMachineToMachineToken()
        }
    )

    override fun hentHenvendelser(bruker: EksternBruker, enhet: String): List<HenvendelseDTO> {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
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

        val aktorId = bruker.aktorId()
        val callId = getCallId()

        return if (newProxyApiEnabled) {
            henvendelseInfoProxyApi
                .henvendelseinfoHenvendelselisteGet(aktorId, callId)
                .let { loggFeilSomErSpesialHandtert(bruker, it) }
                .asSequence()
                .filter(kontorsperreTilgang(enhetOgGTListe))
                .map(kassertInnhold(OffsetDateTime.now()))
                .map(journalfortTemaTilgang(tematilganger))
                .map(::sorterMeldinger)
                .map(::unikeJournalposter)
                .toList()
        } else {
            henvendelseInfoApi
                .henvendelseinfoHenvendelselisteGet(aktorId, callId)
                .let { loggFeilSomErSpesialHandtert(bruker, it) }
                .asSequence()
                .filter(kontorsperreTilgang(enhetOgGTListe))
                .map(kassertInnhold(OffsetDateTime.now()))
                .map(journalfortTemaTilgang(tematilganger))
                .map(::sorterMeldinger)
                .map(::unikeJournalposter)
                .toList()
        }
    }

    override fun hentHenvendelse(kjedeId: String): HenvendelseDTO {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val callId = getCallId()
        val fixKjedeId = kjedeId.fixKjedeId()

        return if (newProxyApiEnabled) {
            henvendelseInfoProxyApi.henvendelseinfoHenvendelseKjedeIdGet(fixKjedeId, callId)
        } else {
            henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(fixKjedeId, callId)
        }
    }

    override fun journalforHenvendelse(
        enhet: String,
        kjedeId: String,
        saksTema: String,
        saksId: String?,
        fagsakSystem: String?
    ) {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val callId = getCallId()
        val fixKjedeId = kjedeId.fixKjedeId()

        val fagsaksystem = if (saksId != null) {
            JournalRequestDTO.Fagsaksystem.valueOf(
                requireNotNull(fagsakSystem) {
                    "Ved journalføring mot $saksId er det påkrevd å sende med fagsakSystem saken kommer fra"
                }
            )
        } else {
            null
        }

        return if (newProxyApiEnabled) {
            henvendelseBehandlingProxyApi
                .henvendelseJournalPost(
                    callId,
                    JournalRequestDTO(
                        journalforendeEnhet = enhet,
                        kjedeId = fixKjedeId,
                        temakode = saksTema,
                        fagsakId = if (saksTema == "BID") null else saksId,
                        fagsaksystem = if (saksTema == "BID") null else fagsaksystem,
                    )
                )
        } else {
            henvendelseBehandlingApi
                .henvendelseJournalPost(
                    callId,
                    JournalRequestDTO(
                        journalforendeEnhet = enhet,
                        kjedeId = fixKjedeId,
                        temakode = saksTema,
                        fagsakId = if (saksTema == "BID") null else saksId,
                        fagsaksystem = if (saksTema == "BID") null else fagsaksystem,
                    )
                )
        }
    }

    override fun sendSamtalereferat(
        kjedeId: String?,
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        kanal: SamtalereferatRequestDTO.Kanal,
        fritekst: String
    ): HenvendelseDTO {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val callId = getCallId()
        val fixKjedeId = kjedeId?.fixKjedeId()
        val aktorId = bruker.aktorId()

        return if (newProxyApiEnabled) {
            henvendelseOpprettProxyApi.henvendelseNySamtalereferatPost(
                xCorrelationID = callId,
                samtalereferatRequestDTO = SamtalereferatRequestDTO(
                    aktorId = aktorId,
                    temagruppe = temagruppe,
                    enhet = enhet,
                    kanal = kanal,
                    fritekst = fritekst
                ),
                kjedeId = fixKjedeId
            )
        } else {
            henvendelseOpprettApi.henvendelseNySamtalereferatPost(
                xCorrelationID = callId,
                samtalereferatRequestDTO = SamtalereferatRequestDTO(
                    aktorId = aktorId,
                    temagruppe = temagruppe,
                    enhet = enhet,
                    kanal = kanal,
                    fritekst = fritekst
                ),
                kjedeId = fixKjedeId
            )
        }
    }

    override fun opprettNyDialogOgSendMelding(
        bruker: EksternBruker,
        enhet: String,
        temagruppe: String,
        tilknyttetAnsatt: Boolean,
        fritekst: String
    ): HenvendelseDTO {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val callId = getCallId()
        val aktorId = bruker.aktorId()

        return if (newProxyApiEnabled) {
            henvendelseOpprettProxyApi.henvendelseNyMeldingPost(
                callId,
                kjedeId = null,
                meldingRequestDTO = MeldingRequestDTO(
                    aktorId = aktorId,
                    temagruppe = temagruppe,
                    enhet = enhet,
                    tildelMeg = tilknyttetAnsatt,
                    fritekst = fritekst
                )
            )
        } else {
            henvendelseOpprettApi.henvendelseNyMeldingPost(
                callId,
                kjedeId = null,
                meldingRequestDTO = MeldingRequestDTO(
                    aktorId = aktorId,
                    temagruppe = temagruppe,
                    enhet = enhet,
                    tildelMeg = tilknyttetAnsatt,
                    fritekst = fritekst
                )
            )
        }
    }

    override fun sendMeldingPaEksisterendeDialog(
        bruker: EksternBruker,
        kjedeId: String,
        enhet: String,
        tilknyttetAnsatt: Boolean,
        fritekst: String
    ): HenvendelseDTO {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val fixKjedeId = kjedeId.fixKjedeId()
        val aktorId = bruker.aktorId()
        val callId = getCallId()
        val henvendelse = if (newProxyApiEnabled) {
            henvendelseInfoProxyApi.henvendelseinfoHenvendelseKjedeIdGet(fixKjedeId, callId)
        } else {
            henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(fixKjedeId, callId)
        }

        val kjedeTilhorerBruker = sjekkEierskap(bruker, henvendelse)
        if (!kjedeTilhorerBruker) {
            throw IllegalStateException("Henvendelse $kjedeId tilhørte ikke bruker")
        }

        return if (newProxyApiEnabled) {
            henvendelseOpprettProxyApi
                .henvendelseNyMeldingPost(
                    callId,
                    kjedeId = kjedeId.fixKjedeId(),
                    meldingRequestDTO = MeldingRequestDTO(
                        aktorId = aktorId,
                        temagruppe = henvendelse.gjeldendeTemagruppe!!, // TODO må fikses av SF-api. Temagruppe kan ikke være null
                        enhet = enhet,
                        fritekst = fritekst,
                        tildelMeg = tilknyttetAnsatt
                    )
                )
        } else {
            henvendelseOpprettApi
                .henvendelseNyMeldingPost(
                    callId,
                    kjedeId = kjedeId.fixKjedeId(),
                    meldingRequestDTO = MeldingRequestDTO(
                        aktorId = aktorId,
                        temagruppe = henvendelse.gjeldendeTemagruppe!!, // TODO må fikses av SF-api. Temagruppe kan ikke være null
                        enhet = enhet,
                        fritekst = fritekst,
                        tildelMeg = tilknyttetAnsatt
                    )
                )
        }
    }

    override fun henvendelseTilhorerBruker(bruker: EksternBruker, kjedeId: String): Boolean {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val fixKjedeId = kjedeId.fixKjedeId()
        val callId = getCallId()

        val henvendelse = if (newProxyApiEnabled) {
            henvendelseInfoProxyApi.henvendelseinfoHenvendelseKjedeIdGet(fixKjedeId, callId)
        } else {
            henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(fixKjedeId, callId)
        }

        return sjekkEierskap(bruker, henvendelse)
    }

    override fun sjekkEierskap(bruker: EksternBruker, henvendelse: HenvendelseDTO): Boolean {
        return when (bruker) {
            is EksternBruker.Fnr -> bruker.ident == henvendelse.fnr
            is EksternBruker.AktorId -> bruker.ident == henvendelse.aktorId
        }
    }

    override fun merkSomFeilsendt(kjedeId: String) {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val fixKjedeId = kjedeId.fixKjedeId()
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            fixKjedeId,
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::feilsendt).to(true)
        )

        if (newProxyApiEnabled) {
            henvendelseBehandlingProxyApi.client.request<Map<String, Any?>, Unit>(request).throwIfError()
        } else {
            henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request).throwIfError()
        }
    }

    override fun sendTilSladding(kjedeId: String, arsak: String, meldingId: List<String>?) {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val callId = getCallId()

        if (newProxyApiEnabled) {
            henvendelseBehandlingProxyApi.henvendelseSladdingPost(
                xCorrelationID = callId,
                sladdeRequestDTO = SladdeRequestDTO(
                    kjedeId = kjedeId,
                    aarsak = arsak,
                    meldingsIder = meldingId
                )
            )
        } else {
            henvendelseBehandlingApi.henvendelseSladdingPost(
                xCorrelationID = callId,
                sladdeRequestDTO = SladdeRequestDTO(
                    kjedeId = kjedeId,
                    aarsak = arsak,
                    meldingsIder = meldingId
                )
            )
        }
    }

    override fun hentSladdeArsaker(kjedeId: String): List<String> {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val callId = getCallId()

        return if (newProxyApiEnabled) {
            henvendelseBehandlingProxyApi.henvendelseSladdingAarsakerKjedeIdGet(
                xCorrelationID = callId,
                kjedeId = kjedeId
            )
        } else {
            henvendelseBehandlingApi.henvendelseSladdingAarsakerKjedeIdGet(
                xCorrelationID = callId,
                kjedeId = kjedeId
            )
        }
    }

    override fun lukkTraad(kjedeId: String) {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val fixKjedeId = kjedeId.fixKjedeId()
        val callId = getCallId()

        if (newProxyApiEnabled) {
            henvendelseBehandlingProxyApi.henvendelseMeldingskjedeLukkPost(
                fixKjedeId,
                callId
            )
        } else {
            henvendelseBehandlingApi.henvendelseMeldingskjedeLukkPost(
                fixKjedeId,
                callId
            )
        }
    }

    override fun ping() {
        val newProxyApiEnabled = unleashService.isEnabled(Feature.USE_NEW_HENVENDELSE_PROXY_API)
        val callId = getCallId()

        if (newProxyApiEnabled) {
            adminKodeverkProxyApiForPing.henvendelseKodeverkTemagrupperGet(callId)
        } else {
            adminKodeverkApiForPing.henvendelseKodeverkTemagrupperGet(callId)
        }
    }

    enum class ApiFeilType {
        IDENT, TEMAGRUPPE, JOURNALFORENDE_IDENT, MARKERT_DATO, MARKERT_AV, FRITEKST, TOM_TRAD, DUPLIKAT_JOURNALPOST, MELDING_ID
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
            val unikeJournalposter = journalposter.distinctBy { Pair(it.journalfortTema, it.journalpostId) }
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

            if (henvendelse.meldinger?.any { it.meldingsId == null } == true) {
                feil.add(ApiFeil(ApiFeilType.MELDING_ID, henvendelse.kjedeId))
            }
        }
        val kanJobbesMedIModia = henvendelser
            .filter { it.gjeldendeTemagruppe != null }
            .filter { it.meldinger.isNotNullOrEmpty() }

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
            val harTilgangTilMinstEttAvJournalforteTema: Boolean = journalforteTemaer
                .any { tema -> tematilganger.contains(tema) }

            if (journalforteTemaer.isEmpty() || harTilgangTilMinstEttAvJournalforteTema) {
                henvendelseDTO
            } else {
                val ident = AuthContextUtils.getIdent().orElse("-")
                logger.info(
                    """
                    Ikke tilgang til noen av temaene tema. 
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

    private fun unikeJournalposter(henvendelse: HenvendelseDTO): HenvendelseDTO {
        return henvendelse.copy(
            journalposter = henvendelse.journalposter?.distinctBy { Pair(it.journalfortTema, it.fagsakId) }
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
    fun downstreamApi(): DownstreamApi = DownstreamApi.parse(getRequiredProperty("SF_HENVENDELSE_SCOPE"))

    fun proxyUrl(): String = getRequiredProperty("SF_HENVENDELSE_PROXY_URL")
    fun downstreamProxyApi(): DownstreamApi = DownstreamApi.parse(getRequiredProperty("SF_HENVENDELSE_PROXY_SCOPE"))

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
        .readTimeout(15.seconds.toJavaDuration())
        .build()

    fun createHenvendelseBehandlingApi(oboClient: BoundedOnBehalfOfTokenClient) =
        HenvendelseBehandlingApi(url(), createClient(oboClient.asTokenProvider()))

    fun createHenvendelseInfoApi(oboClient: BoundedOnBehalfOfTokenClient) =
        HenvendelseInfoApi(url(), createClient(oboClient.asTokenProvider()))

    fun createHenvendelseOpprettApi(oboClient: BoundedOnBehalfOfTokenClient) =
        NyHenvendelseApi(url(), createClient(oboClient.asTokenProvider()))

    fun createHenvendelseBehandlingProxyApi(oboClient: BoundedOnBehalfOfTokenClient) =
        HenvendelseBehandlingApi(proxyUrl(), createClient(oboClient.asTokenProvider()))

    fun createHenvendelseInfoProxyApi(oboClient: BoundedOnBehalfOfTokenClient) =
        HenvendelseInfoApi(proxyUrl(), createClient(oboClient.asTokenProvider()))

    fun createHenvendelseOpprettProxyApi(oboClient: BoundedOnBehalfOfTokenClient) =
        NyHenvendelseApi(proxyUrl(), createClient(oboClient.asTokenProvider()))

    fun BoundedOnBehalfOfTokenClient.asTokenProvider(): () -> String = {
        AuthContextUtils.requireBoundedClientOboToken(this)
    }
}
