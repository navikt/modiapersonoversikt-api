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
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.*
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
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
    fun sendSamtalereferat(kjedeId: String?, bruker: EksternBruker, enhet: String, temagruppe: String, kanal: SamtalereferatRequestDTO.Kanal, fritekst: String): HenvendelseDTO
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
    fun lukkTraad(kjedeId: String)

    fun ping()
}

class SfHenvendelseServiceImpl(
    private val henvendelseBehandlingApi: HenvendelseBehandlingApi = SfHenvendelseApiFactory.createHenvendelseBehandlingApi(),
    private val henvendelseInfoApi: HenvendelseInfoApi = SfHenvendelseApiFactory.createHenvendelseInfoApi(),
    private val henvendelseJournalApi: JournalApi = SfHenvendelseApiFactory.createHenvendelseJournalApi(),
    private val henvendelseOpprettApi: NyHenvendelseApi = SfHenvendelseApiFactory.createHenvendelseOpprettApi(),
    private val pdlOppslagService: PdlOppslagService,
    private val arbeidsfordeling: ArbeidsfordelingV1Service,
    private val ansattService: AnsattService,
    private val stsService: SystemUserTokenProvider
) : SfHenvendelseService {
    private val logger = LoggerFactory.getLogger(SfHenvendelseServiceImpl::class.java)
    private val adminKodeverkApiForPing = KodeverkApi(
        SfHenvendelseApiFactory.url(),
        SfHenvendelseApiFactory.createClient {
            stsService.systemUserToken
        }
    )

    constructor(
        pdlOppslagService: PdlOppslagService,
        arbeidsfordeling: ArbeidsfordelingV1Service,
        ansattService: AnsattService,
        stsService: SystemUserTokenProvider
    ) : this(
        SfHenvendelseApiFactory.createHenvendelseBehandlingApi(),
        SfHenvendelseApiFactory.createHenvendelseInfoApi(),
        SfHenvendelseApiFactory.createHenvendelseJournalApi(),
        SfHenvendelseApiFactory.createHenvendelseOpprettApi(),
        pdlOppslagService,
        arbeidsfordeling,
        ansattService,
        stsService
    )

    override fun hentHenvendelser(bruker: EksternBruker, enhet: String): List<HenvendelseDTO> {
        val enhetOgGTListe = arbeidsfordeling.hentGTnummerForEnhet(enhet)
            .map { it.geografiskOmraade }
            .plus(enhet)
        val tematilganger = ansattService.hentAnsattFagomrader(
            SubjectHandler.getIdent().orElseThrow(),
            enhet
        )

        return henvendelseInfoApi
            .henvendelseinfoHenvendelselisteGet(bruker.aktorId(), getCallId())
            .also { loggFeilSomErSpesialHandtert(bruker, it) }
            .filter(kontorsperreTilgang(enhetOgGTListe))
            .map(kassertInnhold(OffsetDateTime.now()))
            .map(journalfortTemaTilgang(tematilganger))
    }

    override fun hentHenvendelse(kjedeId: String): HenvendelseDTO {
        return henvendelseInfoApi.henvendelseinfoHenvendelseKjedeIdGet(kjedeId, getCallId())
    }

    override fun journalforHenvendelse(enhet: String, kjedeId: String, saksTema: String, saksId: String?, fagsakSystem: String?) {
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
                    kjedeId = kjedeId,
                    temakode = saksTema,
                    saksId = saksId,
                    fagsaksystem = fagsaksystem
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
                kjedeId = kjedeId
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
        val request: RequestConfig<Map<String, Any?>> = createPatchRequest(
            kjedeId,
            PatchNote<HenvendelseDTO>()
                .set(HenvendelseDTO::feilsendt).to(true)
        )
        henvendelseBehandlingApi.client.request<Map<String, Any?>, Unit>(request)
    }

    override fun lukkTraad(kjedeId: String) {
        henvendelseBehandlingApi.henvendelseMeldingskjedeLukkPost(
            kjedeId,
            getCallId()
        )
    }

    override fun ping() {
        adminKodeverkApiForPing.henvendelseKodeverkTemagrupperGet(getCallId())
    }

    private fun loggFeilSomErSpesialHandtert(bruker: EksternBruker, henvendelser: List<HenvendelseDTO>) {
        val manglendeOpprinneligGt = mutableListOf<String>()
        val manglendeIdentIMelding = mutableListOf<String>()
        for (henvendelse in henvendelser) {
            if (henvendelse.opprinneligGT == null) {
                manglendeOpprinneligGt.add(henvendelse.kjedeId)
            }
            val meldinger = henvendelse.meldinger ?: emptyList()
            if (meldinger.any { it.fra.ident == null }) {
                manglendeIdentIMelding.add(henvendelse.kjedeId)
            }
        }
        if (manglendeOpprinneligGt.isEmpty() && manglendeIdentIMelding.isEmpty()) {
            return
        }
        val sb = StringBuilder()
        sb.appendln("[SF-HENVENDELSE]")
        sb.appendln("Fant feil i dataformat til henvendelser fra $bruker")
        sb.appendln("Kjeder med manglende GT: ${manglendeOpprinneligGt.joinToString(", ")}")
        sb.appendln("Kjeder med meldinger uten ident: ${manglendeIdentIMelding.joinToString(", ")}")

        logger.warn(sb.toString())
    }

    private fun kontorsperreTilgang(enhetOgGTListe: List<String>): (HenvendelseDTO) -> Boolean {
        return { henvendelseDTO ->
            if (!henvendelseDTO.kontorsperre) {
                true
            } else {
                val sperre = henvendelseDTO.markeringer?.find { it.markeringstype == MarkeringDTO.Markeringstype.KONTORSPERRE }
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
                val ident = SubjectHandler.getIdent().orElse("-")
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
    fun url(): String = getRequiredProperty("SF_HENVENDELSE_URL")
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
    fun createHenvendelseBehandlingApi() = HenvendelseBehandlingApi(url(), client)
    fun createHenvendelseInfoApi() = HenvendelseInfoApi(url(), client)
    fun createHenvendelseJournalApi() = JournalApi(url(), client)
    fun createHenvendelseOpprettApi() = NyHenvendelseApi(url(), client)
}
