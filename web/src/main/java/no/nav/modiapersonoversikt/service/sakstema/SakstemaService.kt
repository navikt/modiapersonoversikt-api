package no.nav.modiapersonoversikt.service.sakstema

import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakogbehandling.SakOgBehandlingService
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime

class SakstemaService @Autowired constructor(
    val safService: SafService,
    val sakOgBehandlingService: SakOgBehandlingService,
    val kodeverk: EnhetligKodeverk.Service,
) {
    private val log = LoggerFactory.getLogger(SakstemaService::class.java)
    fun hentSakstema(saker: List<Sak>, fnr: String): ResultatWrapper<List<Sakstema>> {
        val dokumenter = safService.hentJournalposter(fnr)
        val behandlingskjeder = sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(fnr)
        val temakoder = hentAlleTema(saker, dokumenter.resultat, behandlingskjeder)

        return opprettSakstemaresultat(saker, dokumenter, temakoder, behandlingskjeder)
    }

    private fun opprettSakstemaresultat(
        saker: List<Sak>,
        dokumenter: ResultatWrapper<List<DokumentMetadata>>,
        temakoder: Set<String>,
        behandlingskjeder: Map<String, List<Behandlingskjede>>
    ): ResultatWrapper<List<Sakstema>> {
        return opprettSakstemaForEnTemagruppe(temakoder, saker, dokumenter.resultat, behandlingskjeder)
            .withEkstraFeilendeBaksystemer(dokumenter.feilendeSystemer)
    }

    fun opprettSakstemaForEnTemagruppe(
        temakoder: Set<String>,
        saker: List<Sak>,
        dokumenter: List<DokumentMetadata>,
        behandlingskjeder: Map<String, List<Behandlingskjede>>
    ): ResultatWrapper<List<Sakstema>> {
        val feilendeBaksystemer = mutableSetOf<Baksystem>()
        val sakerGruppert = saker.groupBy(Sak::getTemakode)
        val dokumenterFraHenvendelseGruppert = dokumenter
            .filter { it.baksystem.contains(Baksystem.HENVENDELSE) }
            .groupBy(DokumentMetadata::getTemakode)

        val dokumentIdLUT = dokumenter.associateBy(DokumentMetadata::getTilhorendeSakid)
        val sakstema = temakoder.map { temakode ->
            val tilhorendeSaker = sakerGruppert[temakode] ?: emptyList()
            val dokumenterFraHenvendelse = dokumenterFraHenvendelseGruppert[temakode] ?: emptyList()
            val dokumenterFraJoark = tilhorendeSaker
                .mapNotNull { dokumentIdLUT[it.saksId] }
            val tilhorendeDokumenter = dokumenterFraHenvendelse + dokumenterFraJoark

            var temanavn = kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA).hentVerdiEllerNull(temakode)
            if (temanavn == null) {
                feilendeBaksystemer.add(Baksystem.KODEVERK)
                temanavn = temakode
                log.warn("Fant ikke temanavn for temakode $temakode. Bruker temakode som generisk tittel.")
            }
            Sakstema()
                .withTemakode(temakode)
                .withTemanavn(temanavn)
                .withBehandlingskjeder(behandlingskjeder[temakode] ?: emptyList())
                .withTilhorendeSaker(tilhorendeSaker)
                .withDokumentMetadata(tilhorendeDokumenter)
                .withErGruppert(false)
        }
        return ResultatWrapper(fjernGamleDokumenter(sakstema), feilendeBaksystemer)
    }

    companion object {
        private val prodsettingsdato: LocalDateTime by lazy {
            LocalDate
                .parse(EnvironmentUtils.getRequiredProperty("SAKSOVERSIKT_PRODSETTNINGSDATO"))
                .atStartOfDay()
        }

        @JvmStatic
        fun hentAlleTema(
            saker: List<Sak>,
            dokumenter: List<DokumentMetadata>,
            behandlingskjeder: Map<String, List<Behandlingskjede>>
        ): Set<String> {
            return setOf(
                *saker.map(Sak::getTemakode).toTypedArray(),
                *dokumenter
                    .filter { it.baksystem.contains(Baksystem.HENVENDELSE) }
                    .map(DokumentMetadata::getTemakode)
                    .toTypedArray(),
                *behandlingskjeder.keys.toTypedArray()
            )
        }

        @JvmStatic
        fun fjernGamleDokumenter(saker: List<Sakstema>): List<Sakstema> {
            return saker
                .map { sak ->
                    val filtrerteDokumenter = sak
                        .dokumentMetadata
                        .filter { dokument ->
                            val erFraSAF = dokument.baksystem.size == 1 && dokument.baksystem.contains(Baksystem.SAF)
                            val erFraForProdsetting = dokument.dato?.isBefore(prodsettingsdato) ?: false
                            (erFraSAF && erFraForProdsetting).not()
                        }

                    sak.withDokumentMetadata(filtrerteDokumenter)
                }
        }
    }
}
