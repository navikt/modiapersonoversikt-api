package no.nav.modiapersonoversikt.service.sakstema

import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.FeilendeBaksystemException
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakogbehandling.FilterUtils.fjernGamleDokumenter
import no.nav.modiapersonoversikt.service.sakogbehandling.SakOgBehandlingService
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

class SakstemaService {
    @Autowired
    lateinit var safService: SafService

    @Autowired
    lateinit var sakOgBehandlingService: SakOgBehandlingService

    @Autowired
    lateinit var kodeverk: EnhetligKodeverk.Service

    fun hentSakstema(saker: List<Sak>, fnr: String): ResultatWrapper<List<Sakstema>> {
        val wrapper = safService.hentJournalposter(fnr)

        return try {
            val behandlingskjeder: Map<String, List<Behandlingskjede?>?> =
                sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(
                    fnr
                )
            val temakoder = hentAlleTema(saker, wrapper.resultat, behandlingskjeder)
            opprettSakstemaresultat(saker, wrapper, temakoder, behandlingskjeder)
        } catch (e: FeilendeBaksystemException) {
            val temakoder = hentAlleTema(saker, wrapper.resultat, emptyMap<String, List<Behandlingskjede?>>())
            wrapper.feilendeSystemer.add(e.baksystem)
            opprettSakstemaresultat(saker, wrapper, temakoder, emptyMap<String, List<Behandlingskjede?>>())
        }
    }

    private fun opprettSakstemaresultat(
        saker: List<Sak>,
        wrapper: ResultatWrapper<List<DokumentMetadata>>,
        temakoder: Set<String>,
        behandlingskjeder: Map<String, List<Behandlingskjede?>?>
    ): ResultatWrapper<List<Sakstema>> {
        return opprettSakstemaForEnTemagruppe(temakoder, saker, wrapper.resultat, behandlingskjeder)
            .withEkstraFeilendeBaksystemer(wrapper.feilendeSystemer)
    }

    fun opprettSakstemaForEnTemagruppe(
        temakoder: Set<String>,
        alleSaker: List<Sak>,
        alleDokumentMetadata: List<DokumentMetadata>,
        behandlingskjeder: Map<String, List<Behandlingskjede?>?>
    ): ResultatWrapper<List<Sakstema>> {
        val feilendeBaksystemer: MutableSet<Baksystem> = HashSet()
        val sakstema = temakoder.stream()
            .map<Sakstema> { temakode: String ->
                val tilhorendeSaker = sakerITemagruppe(alleSaker, temakode)
                val tilhorendeDokumentMetadata =
                    tilhorendeDokumentMetadata(alleDokumentMetadata, temakode, tilhorendeSaker)
                val temanavn = getTemanavnForTemakode(temakode)
                feilendeBaksystemer.addAll(temanavn.feilendeSystemer)
                Sakstema()
                    .withTemakode(temakode)
                    .withBehandlingskjeder(
                        Optional.ofNullable<List<Behandlingskjede?>?>(
                            behandlingskjeder[temakode]
                        ).orElse(emptyList<Behandlingskjede>())
                    )
                    .withTilhorendeSaker(tilhorendeSaker)
                    .withTemanavn(temanavn.resultat)
                    .withDokumentMetadata(tilhorendeDokumentMetadata)
                    .withErGruppert(false)
            }
            .collect(Collectors.toList<Sakstema>())
        return ResultatWrapper(fjernGamleDokumenter(sakstema), feilendeBaksystemer)
    }

    private fun tilhorendeDokumentMetadata(
        alleDokumentMetadata: List<DokumentMetadata>,
        temakode: String,
        tilhorendeSaker: List<Sak>
    ): List<DokumentMetadata> {
        return alleDokumentMetadata
            .stream()
            .filter(tilhorendeFraJoark(tilhorendeSaker).or(tilhorendeFraHenvendelse(temakode)))
            .collect(Collectors.toList())
    }

    private fun sakerITemagruppe(alleSaker: List<Sak>, temakode: String): List<Sak> {
        return alleSaker.stream()
            .filter { sak: Sak -> temakode == sak.temakode }
            .collect(Collectors.toList())
    }

    private fun getTemanavnForTemakode(temakode: String): ResultatWrapper<String> {
        val temanavn = kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA).hentVerdiEllerNull(temakode)
        return if (temanavn == null) {
            LOG.warn("Fant ikke temanavn for temakode $temakode. Bruker temakode som generisk tittel.")
            ResultatWrapper(temakode).withEkstraFeilendeSystem(Baksystem.KODEVERK)
        } else {
            ResultatWrapper(temanavn)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SakstemaService::class.java)
        fun hentAlleTema(
            saker: List<Sak>,
            dokumentMetadata: List<DokumentMetadata>,
            behandlingskjeder: Map<String, List<Behandlingskjede?>?>
        ): Set<String> {
            val sakerTema = saker.stream().map { obj: Sak -> obj.temakode }
            val dokumentTema = dokumentMetadata
                .stream()
                .filter { metadata: DokumentMetadata -> metadata.baksystem.contains(Baksystem.HENVENDELSE) }
                .map { obj: DokumentMetadata -> obj.temakode }
            val behandlingskjedeTema = behandlingskjeder.keys.stream()
            return Stream.of(sakerTema, dokumentTema, behandlingskjedeTema)
                .flatMap(Function.identity())
                .collect(Collectors.toSet())
        }

        private fun tilhorendeFraJoark(tilhorendeSaker: List<Sak>): Predicate<DokumentMetadata> {
            return Predicate { dm: DokumentMetadata ->
                tilhorendeSaker.stream().map { obj: Sak -> obj.saksId }
                    .toList().contains(dm.tilhorendeSakid)
            }
        }

        private fun tilhorendeFraHenvendelse(temakode: String): Predicate<DokumentMetadata> {
            return Predicate { dm: DokumentMetadata -> dm.baksystem.contains(Baksystem.HENVENDELSE) && dm.temakode == temakode }
        }
    }
}
