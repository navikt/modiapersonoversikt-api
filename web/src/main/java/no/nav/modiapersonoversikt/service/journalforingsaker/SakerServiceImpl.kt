@file:OptIn(ExperimentalContracts::class)

package no.nav.modiapersonoversikt.service.journalforingsaker

import jakarta.annotation.PostConstruct
import no.nav.arena.services.sakvedtakservice.SakVedtakPortType
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.journalforingsaker.kilder.*
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.utils.Contracts
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.contracts.ExperimentalContracts

private val logger = LoggerFactory.getLogger(SakerServiceImpl::class.java)

class SakerServiceImpl : SakerService {
    @Autowired
    private lateinit var kodeverk: EnhetligKodeverk.Service

    @Autowired
    private lateinit var safService: SafService

    @Autowired
    private lateinit var arenaSakVedtakService: SakVedtakPortType

    private lateinit var safSaker: SafSaker
    private lateinit var arenaSaker: SakerKilde
    private lateinit var bidragSaker: BidragSaker
    private lateinit var generelleSaker: GenerelleSaker
    private lateinit var oppfolgingsSaker: OppfolgingsSaker

    @jakarta.annotation.PostConstruct
    fun setup() {
        safSaker = SafSaker(safService)
        arenaSaker = ArenaSakerV2(arenaSakVedtakService)
        bidragSaker = BidragSaker()
        generelleSaker = GenerelleSaker()
        oppfolgingsSaker = OppfolgingsSaker()
    }

    override fun hentSafSaker(fnr: String): SakerService.Resultat {
        Contracts.requireNotNullOrBlank(fnr) { "Fnr-parameter må være tilstede." }
        return SakerService.Resultat()
            .leggTilDataFraKilde(fnr, safSaker)
    }

    override fun hentSaker(fnr: String): SakerService.Resultat {
        Contracts.requireNotNullOrBlank(fnr) { "Fnr-parameter må være tilstede." }

        return SakerService.Resultat()
            .leggTilDataFraKilde(fnr, safSaker)
            .leggTilDataFraKilde(fnr, arenaSaker)
            .leggTilDataFraKilde(fnr, generelleSaker)
            .leggTilDataFraKilde(fnr, oppfolgingsSaker)
            .leggTilDataFraKilde(fnr, bidragSaker)
            .leggTilTemaNavn(kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA))
            .leggTilFagsystemNavn(kodeverk.hentKodeverk(KodeverkConfig.FAGSYSTEM))
            .fjernIkkeGodkjenteSaker()
            .fjernDuplikater()
    }

    companion object {
        private fun SakerService.Resultat.fjernIkkeGodkjenteSaker(): SakerService.Resultat {
            this.saker.removeIf {
                !godkjentFagSak(it) && !godkjentGenerellSak(it)
            }
            return this
        }

        private fun SakerService.Resultat.fjernDuplikater(): SakerService.Resultat {
            return this.copy(
                saker = this.saker.distinctBy { Pair(it.temaKode, it.fagsystemSaksId) }.toMutableList(),
                feiledeSystemer = this.feiledeSystemer.distinct().toMutableList(),
            )
        }

        private fun SakerService.Resultat.leggTilDataFraKilde(
            fnr: String,
            kilde: SakerKilde,
        ): SakerService.Resultat {
            try {
                kilde.leggTilSaker(fnr, this.saker)
            } catch (e: Exception) {
                logger.error("Kunne ikke hente saker fra ${kilde.kildeNavn}", e)
                this.feiledeSystemer.add(kilde.kildeNavn)
            }
            return this
        }

        fun SakerService.Resultat.leggTilTemaNavn(kodeverk: EnhetligKodeverk.Kodeverk<String, String>): SakerService.Resultat {
            saker.forEach {
                it.temaNavn = kodeverk.hentVerdi(it.temaKode ?: "", it.temaKode ?: "")
            }
            return this
        }

        fun SakerService.Resultat.leggTilFagsystemNavn(kodeverk: EnhetligKodeverk.Kodeverk<String, String>): SakerService.Resultat {
            saker.forEach {
                it.fagsystemNavn = kodeverk.hentVerdi(it.fagsystemKode ?: "", it.fagsystemKode ?: "")
            }
            return this
        }

        private fun godkjentFagSak(sak: JournalforingSak): Boolean {
            val godkjentType = !sak.isSakstypeForVisningGenerell
            val godkjentSystem = JournalforingSak.GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode)
            val godkjentTema = JournalforingSak.TEMAKODE_KLAGE_ANKE != sak.temaKode

            return godkjentType && godkjentSystem && godkjentTema
        }

        private fun godkjentGenerellSak(sak: JournalforingSak): Boolean {
            val godkjentType = sak.isSakstypeForVisningGenerell
            val godkjentSystem = JournalforingSak.GYLDIGE_FAGSYSTEM_FOR_GENERELLE_SAKER.contains(sak.fagsystemKode)
            val godkjentTema = JournalforingSak.GODKJENTE_TEMA_FOR_GENERELL_SAK.contains(sak.temaKode)

            return godkjentType && godkjentSystem && godkjentTema
        }
    }
}
