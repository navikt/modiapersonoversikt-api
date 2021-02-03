package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker

import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.SakerUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.*
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.gsak.GsakSaker
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1
import no.nav.tjeneste.virksomhet.sak.v1.SakV1
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate.not
import javax.annotation.PostConstruct
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
private val logger = LoggerFactory.getLogger(SakerServiceImpl::class.java)

@ExperimentalContracts
class SakerServiceImpl : SakerService {
    @Autowired
    private lateinit var sakV1: SakV1

    @Autowired
    private lateinit var behandleSakWS: BehandleSakV1

    @Autowired
    private lateinit var gsakKodeverk: GsakKodeverk

    @Autowired
    private lateinit var standardKodeverk: StandardKodeverk

    @Autowired
    private lateinit var behandleHenvendelsePortType: BehandleHenvendelsePortType

    @Autowired
    private lateinit var arbeidOgAktivitet: ArbeidOgAktivitet

    @Autowired
    private lateinit var psakService: PsakService

    @Autowired
    private lateinit var sakApiGateway: SakApiGateway

    @Autowired
    private lateinit var pdlOppslagService: PdlOppslagService

    @Autowired
    private lateinit var unleashService: UnleashService

    private lateinit var arenaSaker: ArenaSaker
    private lateinit var bidragSaker: BidragSaker
    private lateinit var generelleSaker: GenerelleSaker
    private lateinit var gsakSaker: GsakSaker
    private lateinit var oppfolgingsSaker: OppfolgingsSaker
    private lateinit var pensjonSaker: PensjonSaker

    @PostConstruct
    fun setup() {
        arenaSaker = ArenaSaker(arbeidOgAktivitet)
        bidragSaker = BidragSaker()
        generelleSaker = GenerelleSaker()
        gsakSaker = GsakSaker.createProxy(sakV1, behandleSakWS, sakApiGateway, pdlOppslagService, unleashService)
        oppfolgingsSaker = OppfolgingsSaker()
        pensjonSaker = PensjonSaker(psakService)

    }

    override fun hentSaker(fnr: String): SakerService.Resultat {
        requireFnrNotNullOrBlank(fnr)
        val (gsakSaker, pesysSaker) = inParallel(
            { hentSammensatteSakerResultat(fnr) },
            { hentPensjonSakerResultat(fnr) }
        )

        return slaSammenGsakPesysSaker(gsakSaker, pesysSaker)
    }

    override fun hentSammensatteSaker(fnr: String): List<Sak> {
        requireFnrNotNullOrBlank(fnr)
        return hentSammensatteSakerResultat(fnr).saker
    }

    override fun hentPensjonSaker(fnr: String): List<Sak> {
        requireFnrNotNullOrBlank(fnr)
        return hentPensjonSakerResultat(fnr).saker
    }

    fun hentSammensatteSakerResultat(fnr: String?): SakerService.Resultat {
        requireFnrNotNullOrBlank(fnr)
        val resultat = SakerService.Resultat()
        resultat.leggTilDataFraKilde(fnr, gsakSaker)
        resultat.leggTilDataFraKilde(fnr, arenaSaker)
        resultat.leggTilDataFraKilde(fnr, generelleSaker)
        resultat.leggTilDataFraKilde(fnr, oppfolgingsSaker)

        SakerUtils.leggTilFagsystemnavnOgTemanavn(resultat.saker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk)

        /**
         * Bidragssaken må legges til etter `leggTilFagsystemnavnOgTemanavn` siden vi ikke har
         * fagsystemkode-mapping for bidrag-hack saken
         */
        resultat.leggTilDataFraKilde(fnr, bidragSaker)

        return resultat
            .fjernIkkeGodkjenteSaker()
    }

    private fun SakerService.Resultat.fjernIkkeGodkjenteSaker(): SakerService.Resultat {
        this.saker.removeIf(not(GODKJENT_FAGSAK or GODKJENT_GENERELL))
        return this
    }

    fun hentPensjonSakerResultat(fnr: String?): SakerService.Resultat {
        requireFnrNotNullOrBlank(fnr)
        val resultat = SakerService.Resultat().leggTilDataFraKilde(fnr, pensjonSaker)
        SakerUtils.leggTilFagsystemnavnOgTemanavn(resultat.saker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk)
        return resultat
    }

    override fun knyttBehandlingskjedeTilSak(fnr: String?, behandlingskjede: String?, sak: Sak, enhet: String?) {
        requireKnyttTilSakParametereNotNullOrBlank(sak, behandlingskjede, fnr, enhet)

        if (sak.syntetisk && Sak.BIDRAG_MARKOR == sak.fagsystemKode) {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(behandlingskjede, "BID")
            return
        }

        if (sakFinnesIkkeIPsakOgGsak(sak)) {
            sak.saksId = gsakSaker.opprettSak(fnr, sak)
        }

        requireNotNullOrBlank(sak.saksId) {
            "SaksId-parameter må være tilstede for å kunne knytte behandlingskjede $behandlingskjede til sak."
        }

        try {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                behandlingskjede,
                sak.saksId,
                sak.temaKode,
                enhet
            )
        } catch (e: Exception) {
            throw JournalforingFeilet(e)
        }
    }

    companion object {
        private fun SakerService.Resultat.leggTilDataFraKilde(fnr: String, kilde: SakerKilde): SakerService.Resultat {
            try {
                kilde.leggTilSaker(fnr, this.saker)
            } catch (e: Exception) {
                logger.error("Kunne ikke hente saker fra ${kilde.kildeNavn}", e)
                this.feiledeSystemer.add(kilde.kildeNavn)
            }
            return this
        }

        private fun slaSammenGsakPesysSaker(
            gsak: SakerService.Resultat,
            pesys: SakerService.Resultat
        ): SakerService.Resultat {
            val pesysIder = pesys.saker.map { it.fagsystemSaksId }
            return SakerService.Resultat(
                (pesys.saker + gsak.saker.filter { !pesysIder.contains(it.fagsystemSaksId) }).toMutableList(),
                (pesys.feiledeSystemer + gsak.feiledeSystemer).toMutableList()
            )
        }

        private fun <S, T> inParallel(first: () -> S, second: () -> T): Pair<S, T> {
            val firstTask = CompletableFuture.supplyAsync(copyAuthAndMDC(first))
            val secondTask = CompletableFuture.supplyAsync(copyAuthAndMDC(second))

            CompletableFuture.allOf(firstTask, secondTask).get()

            return Pair(firstTask.get(), secondTask.get())
        }

        private fun sakFinnesIkkeIPsakOgGsak(sak: Sak): Boolean {
            return !(sak.finnesIPsak || sak.finnesIGsak)
        }

        private val GODKJENT_FAGSAK: (Sak) -> Boolean = { sak ->
            !sak.isSakstypeForVisningGenerell &&
                    Sak.GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode) &&
                    Sak.TEMAKODE_KLAGE_ANKE != sak.temaKode
        }

        private val GODKJENT_GENERELL: (Sak) -> Boolean = { sak ->
            sak.isSakstypeForVisningGenerell &&
                    Sak.GYLDIGE_FAGSYSTEM_FOR_GENERELLE_SAKER.contains(sak.fagsystemKode) &&
                    Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK.contains(sak.temaKode)
        }
    }
}

private infix fun <T> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean = { this(it) || other(it) }
internal fun <T> copyAuthAndMDC(fn: () -> T): () -> T {
    val callId = MDC.get(MDCConstants.MDC_CALL_ID)
    val subject = SubjectHandler.getSubject()
    return {
        withCallId(callId) {
            SubjectHandler.withSubject(subject.get(), fn)
        }
    }
}

fun <T> withCallId(callId: String, fn: () -> T): T {
    val originalCallId = MDC.get(MDCConstants.MDC_CALL_ID)
    MDC.put(MDCConstants.MDC_CALL_ID, callId)
    val result = fn()
    MDC.put(MDCConstants.MDC_CALL_ID, originalCallId)
    return result
}
