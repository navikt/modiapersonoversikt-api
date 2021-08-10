package no.nav.modiapersonoversikt.legacy.sak.service.saf

import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.*
import no.nav.modiapersonoversikt.legacy.api.domain.saf.generated.HentBrukersDokumenter
import no.nav.modiapersonoversikt.legacy.api.domain.saf.generated.HentBrukersDokumenter.Datotype
import no.nav.modiapersonoversikt.legacy.api.domain.saf.generated.HentBrukersDokumenter.Journalposttype
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.net.URL
import java.time.LocalDateTime
import java.util.*

private val SAF_GRAPHQL_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_GRAPHQL_URL")
private val SAF_HENTDOKUMENT_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_HENTDOKUMENT_URL")

@KtorExperimentalAPI
private val graphQLClient = LoggingGraphqlClient("SAF", URL(SAF_GRAPHQL_BASEURL))

class SafGraphqlServiceImpl : SafService {
    private val LOG = LoggerFactory.getLogger(SafService::class.java)

    private val client: OkHttpClient = RestClient.baseClient().newBuilder()
        .addInterceptor(HeadersInterceptor(this::httpHeaders))
        .addInterceptor(
            LoggingInterceptor("Saf") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .build()

    val JOURNALPOSTTYPE_INN = Journalposttype.I
    val JOURNALPOSTTYPE_UT = Journalposttype.U
    val JOURNALPOSTTYPE_INTERN = Journalposttype.N

    override fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>> {
        val variables = HentBrukersDokumenter.Variables(
            HentBrukersDokumenter.BrukerIdInput(
                id = fnr,
                type = HentBrukersDokumenter.BrukerIdType.FNR
            )
        )

        return runBlocking {
            val response = HentBrukersDokumenter(graphQLClient).execute(variables, userTokenAuthorizationHeaders)
            if (response.errors.isNullOrEmpty()) {
                val data = requireNotNull(response.data)
                    .dokumentoversiktBruker
                    .journalposter
                    .filterNotNull()
                    .map { DokumentMetadata().fraSafGraphqlJournalpost(it) }
                ResultatWrapper(data, emptySet())
            } else {
                ResultatWrapper(emptyList(), setOf(Baksystem.SAF))
            }
        }
    }

    override fun hentDokument(
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: Dokument.Variantformat
    ): TjenesteResultatWrapper {
        val url = "$SAF_HENTDOKUMENT_BASEURL/$journalpostId/$dokumentInfoId/${variantFormat.name}"
        val response = client.newCall(
            Request.Builder().url(url).build()
        ).execute()
        return when (response.code()) {
            200 -> TjenesteResultatWrapper(response.body()?.bytes())
            else -> handterDokumentFeilKoder(response.code())
        }
    }

    private fun httpHeaders(): Map<String, String> {
        val token = SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
            .orElseThrow { IllegalStateException("Fant ikke OIDC-token") }
        val callId = MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
        return mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json",
            "X-Correlation-ID" to callId
        )
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        httpHeaders().forEach { (key, value) -> header(key, value) }
    }

    private fun handterDokumentFeilKoder(statuskode: Int): TjenesteResultatWrapper {
        when (statuskode) {
            400 -> LOG.warn("Feil i SAF hentDokument. Ugyldig input. JournalpostId og dokumentInfoId må være tall og variantFormat må være en gyldig kodeverk-verdi")
            401 -> LOG.warn("Feil i SAF hentDokument. Bruker mangler tilgang for å vise dokumentet. Ugyldig OIDC token.")
            404 -> LOG.warn("Feil i SAF hentDokument. Dokument eller journalpost ble ikke funnet.")
        }
        return TjenesteResultatWrapper(null, statuskode)
    }

    private fun DokumentMetadata.fraSafGraphqlJournalpost(journalpost: HentBrukersDokumenter.Journalpost): DokumentMetadata {
        retning = getRetning(journalpost)
        dato = getDato(journalpost)
        navn = journalpost.avsenderMottaker?.navn ?: "ukjent"
        journalpostId = journalpost.journalpostId
        hoveddokument = Dokument().fraSafDokumentInfo(getHoveddokumentet(journalpost))
        vedlegg = getVedlegg(journalpost)
        avsender = getAvsender(journalpost)
        mottaker = getMottaker(journalpost)
        tilhorendeSakid = journalpost.sak?.arkivsaksnummer // TODO denne er deprecated, men brukes per i dag.
        tilhorendeFagsakId = journalpost.sak?.fagsakId
        baksystem = baksystem.plus(Baksystem.SAF)
        temakode = journalpost.tema?.name
        temakodeVisning = journalpost.temanavn
        kanalNavn = journalpost.kanalnavn
        isDigitalSendt = sjekkDigitalSending(journalpost)
        isSendtViaPost = sjekkPapirSending(journalpost)
        return this
    }

    private fun sjekkPapirSending(journalpost: HentBrukersDokumenter.Journalpost) =
        listOf(
            "LOKAL_UTSKRIFT",
            "SENTRAL_UTSKRIFT",
            "SKAN_NETS",
            "SKAN_PEN",
            "SKAN_IM"
        ).contains(journalpost.kanal?.name)

    private fun sjekkDigitalSending(journalpost: HentBrukersDokumenter.Journalpost) =
        listOf("NAV_NO", "NAV_NO_UINNLOGGET", "ALTINN", "EESSI", "HELSENETTET", "NAV_NO_UINNLOGGET", "SDP").contains(
            journalpost.kanal?.name
        )

    private fun getAvsender(journalpost: HentBrukersDokumenter.Journalpost): Entitet =
        when (journalpost.journalposttype) {
            JOURNALPOSTTYPE_INTERN -> Entitet.NAV
            JOURNALPOSTTYPE_INN -> if (sluttbrukerErMottakerEllerAvsender(journalpost)) Entitet.SLUTTBRUKER else Entitet.EKSTERN_PART
            JOURNALPOSTTYPE_UT -> Entitet.NAV
            else -> Entitet.UKJENT
        }
    private fun getMottaker(journalpost: HentBrukersDokumenter.Journalpost): Entitet =
        when (journalpost.journalposttype) {
            JOURNALPOSTTYPE_INTERN -> Entitet.NAV
            JOURNALPOSTTYPE_INN -> Entitet.NAV
            JOURNALPOSTTYPE_UT -> if (sluttbrukerErMottakerEllerAvsender(journalpost)) Entitet.SLUTTBRUKER else Entitet.EKSTERN_PART
            else -> Entitet.UKJENT
        }

    private fun sluttbrukerErMottakerEllerAvsender(journalpost: HentBrukersDokumenter.Journalpost): Boolean =
        journalpost.avsenderMottaker?.erLikBruker ?: false

    private fun getRetning(journalpost: HentBrukersDokumenter.Journalpost): Kommunikasjonsretning =
        when (journalpost.journalposttype) {
            Journalposttype.I -> Kommunikasjonsretning.INN
            Journalposttype.U -> Kommunikasjonsretning.UT
            Journalposttype.N -> Kommunikasjonsretning.INTERN
            else -> throw RuntimeException("Ukjent journalposttype: " + journalpost.journalposttype)
        }

    private fun getDato(journalpost: HentBrukersDokumenter.Journalpost): LocalDateTime =
        when (journalpost.journalposttype) {
            Journalposttype.I -> journalpost.getRelevantDatoForType(Datotype.DATO_REGISTRERT)
            Journalposttype.U -> listOfNotNull(
                journalpost.getRelevantDatoForType(Datotype.DATO_EKSPEDERT),
                journalpost.getRelevantDatoForType(Datotype.DATO_SENDT_PRINT),
                journalpost.getRelevantDatoForType(Datotype.DATO_JOURNALFOERT)
            ).firstOrNull()
            Journalposttype.N -> journalpost.getRelevantDatoForType(Datotype.DATO_JOURNALFOERT)
            else -> LocalDateTime.now()
        } ?: LocalDateTime.now()

    private fun HentBrukersDokumenter.Journalpost.getRelevantDatoForType(type: Datotype): LocalDateTime? {
        return this.relevanteDatoer
            .orEmpty()
            .filterNotNull()
            .find { it.datotype == type }
            ?.dato
            ?.value
    }

    private fun getVedlegg(journalpost: HentBrukersDokumenter.Journalpost): List<Dokument> =
        getElektroniskeVedlegg(journalpost).plus(getLogiskeVedlegg(journalpost))

    private fun getElektroniskeVedlegg(journalpost: HentBrukersDokumenter.Journalpost): List<Dokument> =
        journalpost.dokumenter
            .orEmpty()
            .subList(VEDLEGG_START_INDEX, journalpost.dokumenter?.size ?: 0)
            .filterNotNull()
            .map { dok -> Dokument().fraSafDokumentInfo(dok) }

    private fun getHoveddokumentet(journalpost: HentBrukersDokumenter.Journalpost): HentBrukersDokumenter.DokumentInfo =
        journalpost.dokumenter?.get(0) ?: throw RuntimeException("Fant sak uten hoveddokument!")

    private fun getLogiskeVedlegg(journalpost: HentBrukersDokumenter.Journalpost): List<Dokument> =
        getHoveddokumentet(journalpost).logiskeVedlegg
            .filterNotNull()
            .map { logiskVedlegg -> Dokument().fraSafLogiskVedlegg(logiskVedlegg) }

    private fun Dokument.fraSafDokumentInfo(dokumentInfo: HentBrukersDokumenter.DokumentInfo): Dokument {
        tittel = dokumentInfo.tittel
        dokumentreferanse = dokumentInfo.dokumentInfoId
        isKanVises = true
        isLogiskDokument = false
        variantformat = getVariantformat(dokumentInfo)
        skjerming = getSkjerming(dokumentInfo)?.toString()
        dokumentStatus = getDokumentStatus(dokumentInfo)
        return this
    }

    private fun getDokumentStatus(dokumentInfo: HentBrukersDokumenter.DokumentInfo): Dokument.DokumentStatus =
        when (dokumentInfo.dokumentstatus) {
            HentBrukersDokumenter.Dokumentstatus.UNDER_REDIGERING -> Dokument.DokumentStatus.UNDER_REDIGERING
            HentBrukersDokumenter.Dokumentstatus.FERDIGSTILT -> Dokument.DokumentStatus.FERDIGSTILT
            HentBrukersDokumenter.Dokumentstatus.KASSERT -> Dokument.DokumentStatus.KASSERT
            HentBrukersDokumenter.Dokumentstatus.AVBRUTT -> Dokument.DokumentStatus.AVBRUTT
            else -> throw RuntimeException("Ugyldig tekst for mapping til dokumentstatus. Tekst: ${dokumentInfo.dokumentstatus}")
        }

    private fun getVariantformat(dokumentInfo: HentBrukersDokumenter.DokumentInfo): Dokument.Variantformat =
        when (getVariant(dokumentInfo).variantformat) {
            HentBrukersDokumenter.Variantformat.ARKIV -> Dokument.Variantformat.ARKIV
            HentBrukersDokumenter.Variantformat.SLADDET -> Dokument.Variantformat.SLADDET
            HentBrukersDokumenter.Variantformat.FULLVERSJON -> Dokument.Variantformat.FULLVERSJON
            HentBrukersDokumenter.Variantformat.PRODUKSJON -> Dokument.Variantformat.PRODUKSJON
            HentBrukersDokumenter.Variantformat.PRODUKSJON_DLF -> Dokument.Variantformat.PRODUKSJON_DLF
            else -> throw RuntimeException("Ugyldig tekst for mapping til variantformat. Tekst: ${getVariant(dokumentInfo).variantformat}")
        }

    private fun getSkjerming(dokumentInfo: HentBrukersDokumenter.DokumentInfo): HentBrukersDokumenter.SkjermingType? =
        getVariant(dokumentInfo).skjerming

    private fun getVariant(dokumentInfo: HentBrukersDokumenter.DokumentInfo): HentBrukersDokumenter.Dokumentvariant =
        dokumentInfo.dokumentvarianter.let {
            it.find { variant -> variant?.variantformat == HentBrukersDokumenter.Variantformat.SLADDET }
                ?: it.find { variant -> variant?.variantformat == HentBrukersDokumenter.Variantformat.ARKIV }
                ?: throw RuntimeException("Dokument med id ${dokumentInfo.dokumentInfoId} mangler både ARKIV og SLADDET variantformat")
        }

    private fun Dokument.fraSafLogiskVedlegg(logiskVedlegg: HentBrukersDokumenter.LogiskVedlegg): Dokument {
        tittel = logiskVedlegg.tittel
        dokumentreferanse = null
        isKanVises = true
        isLogiskDokument = true

        return this
    }
}
