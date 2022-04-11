@file:OptIn(ExperimentalContracts::class)

package no.nav.modiapersonoversikt.service.saker

import no.nav.modiapersonoversikt.consumer.sak.SakApi
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.pensjonsak.PsakService
import no.nav.modiapersonoversikt.service.saker.kilder.*
import no.nav.modiapersonoversikt.utils.ConcurrencyUtils.inParallel
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.function.Predicate.not
import javax.annotation.PostConstruct
import kotlin.contracts.ExperimentalContracts

private val logger = LoggerFactory.getLogger(SakerServiceImpl::class.java)

class SakerServiceImpl : SakerService {
    @Autowired
    private lateinit var kodeverk: EnhetligKodeverk.Service

    @Autowired
    private lateinit var arbeidOgAktivitet: ArbeidOgAktivitet

    @Autowired
    private lateinit var psakService: PsakService

    @Autowired
    private lateinit var sakApi: SakApi

    @Autowired
    private lateinit var pdlOppslagService: PdlOppslagService

    private lateinit var arenaSaker: ArenaSaker
    private lateinit var bidragSaker: BidragSaker
    private lateinit var generelleSaker: GenerelleSaker
    private lateinit var restSakSaker: RestSakSaker
    private lateinit var oppfolgingsSaker: OppfolgingsSaker
    private lateinit var pensjonSaker: PensjonSaker

    @PostConstruct
    fun setup() {
        arenaSaker = ArenaSaker(arbeidOgAktivitet)
        bidragSaker = BidragSaker()
        generelleSaker = GenerelleSaker()
        restSakSaker = RestSakSaker(sakApi, pdlOppslagService)
        oppfolgingsSaker = OppfolgingsSaker()
        pensjonSaker = PensjonSaker(psakService)
    }

    override fun hentSakSaker(fnr: String): SakerService.Resultat {
        requireFnrNotNullOrBlank(fnr)
        val resultat = SakerService.Resultat()
        return resultat.leggTilDataFraKilde(fnr, restSakSaker)
    }

    override fun hentSaker(fnr: String): SakerService.Resultat {
        requireFnrNotNullOrBlank(fnr)
        val (restSakSaker, pesysSaker) = inParallel(
            { hentSammensatteSakerResultat(fnr) },
            { hentPensjonSakerResultat(fnr) }
        )
        return slaSammenGsakPesysSaker(restSakSaker, pesysSaker)
    }

    fun hentSammensatteSakerResultat(fnr: String?): SakerService.Resultat {
        requireFnrNotNullOrBlank(fnr)
        val resultat = SakerService.Resultat()
        resultat.leggTilDataFraKilde(fnr, restSakSaker)
        resultat.leggTilDataFraKilde(fnr, arenaSaker)
        resultat.leggTilDataFraKilde(fnr, generelleSaker)
        resultat.leggTilDataFraKilde(fnr, oppfolgingsSaker)

        leggTilFagsystemnavnOgTemanavn(
            resultat.saker,
            kodeverk.hentKodeverk(KodeverkConfig.FAGSYSTEM),
            kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA)
        )

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
        leggTilFagsystemnavnOgTemanavn(
            resultat.saker,
            kodeverk.hentKodeverk(KodeverkConfig.FAGSYSTEM),
            kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA)
        )
        return resultat
    }

    companion object {
        @JvmStatic
        fun leggTilFagsystemnavnOgTemanavn(
            saker: List<Sak>,
            fagsystemKodeverk: EnhetligKodeverk.Kodeverk<String, String>,
            arkivtemaKodeverk: EnhetligKodeverk.Kodeverk<String, String>,
        ) {
            saker.forEach {
                it.fagsystemNavn = fagsystemKodeverk.hentVerdi(it.fagsystemKode ?: "", it.fagsystemKode ?: "")
                it.temaNavn = arkivtemaKodeverk.hentVerdi(it.temaKode ?: "", it.temaKode ?: "")
            }
        }

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
            restSak: SakerService.Resultat,
            pesys: SakerService.Resultat
        ): SakerService.Resultat {
            val pesysIder = pesys.saker.map { it.fagsystemSaksId }
            return SakerService.Resultat(
                (pesys.saker + restSak.saker.filter { !pesysIder.contains(it.fagsystemSaksId) }).toMutableList(),
                (pesys.feiledeSystemer + restSak.feiledeSystemer).toMutableList()
            )
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
