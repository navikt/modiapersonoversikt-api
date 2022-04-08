@file:OptIn(ExperimentalContracts::class)

package no.nav.modiapersonoversikt.service.saker

import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.saker.kilder.*
import no.nav.modiapersonoversikt.utils.or
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
    private lateinit var safService: SafService

    @Autowired
    private lateinit var arbeidOgAktivitet: ArbeidOgAktivitet

    private lateinit var safSaker: SafSaker
    private lateinit var arenaSaker: ArenaSaker
    private lateinit var bidragSaker: BidragSaker
    private lateinit var generelleSaker: GenerelleSaker
    private lateinit var oppfolgingsSaker: OppfolgingsSaker

    @PostConstruct
    fun setup() {
        safSaker = SafSaker(safService)
        arenaSaker = ArenaSaker(arbeidOgAktivitet)
        bidragSaker = BidragSaker()
        generelleSaker = GenerelleSaker()
        oppfolgingsSaker = OppfolgingsSaker()
    }

    override fun hentSafSaker(fnr: String): SakerService.Resultat {
        requireFnrNotNullOrBlank(fnr)
        return SakerService.Resultat()
            .leggTilDataFraKilde(fnr, safSaker)
    }

    override fun hentSaker(fnr: String): SakerService.Resultat {
        requireFnrNotNullOrBlank(fnr)

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
            this.saker.removeIf(not(::godkjentFagSak or ::godkjentGenerellSak))
            return this
        }

        private fun SakerService.Resultat.fjernDuplikater(): SakerService.Resultat {
            return this.copy(
                saker = this.saker.distinctBy { Pair(it.temaKode, it.fagsystemSaksId) }.toMutableList(),
                feiledeSystemer = this.feiledeSystemer.distinct().toMutableList()
            )
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

        private fun godkjentFagSak(sak: Sak): Boolean {
            val godkjentType = !sak.isSakstypeForVisningGenerell
            val godkjentSystem = Sak.GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode)
            val godkjentTema = Sak.TEMAKODE_KLAGE_ANKE != sak.temaKode

            return godkjentType && godkjentSystem && godkjentTema
        }
        private fun godkjentGenerellSak(sak: Sak): Boolean {
            val godkjentType = sak.isSakstypeForVisningGenerell
            val godkjentSystem = Sak.GYLDIGE_FAGSYSTEM_FOR_GENERELLE_SAKER.contains(sak.fagsystemKode)
            val godkjentTema = Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK.contains(sak.temaKode)

            return godkjentType && godkjentSystem && godkjentTema
        }
    }
}
