package no.nav.modiapersonoversikt.service.sakstema

import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.FeilendeBaksystemException
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakstema.FilterUtils.fjernGamleDokumenter
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.function.Predicate
import java.util.stream.Collectors

data class SakstemaData(
    val temakode: String,
    val temanavn: String,
    val erGruppert: Boolean,
    val dokumentMetadata: List<DokumentMetadata> = emptyList(),
    val tilhorendeSaker: List<Sak> = emptyList(),
    val feilkoder: List<Int> = emptyList(),
)

interface SakstemaService {
    fun hentSakstema(
        saker: List<Sak>,
        fnr: String,
    ): ResultatWrapper<List<SakstemaData>>

    fun opprettSakstemaresultat(
        saker: List<Sak>,
        wrapper: ResultatWrapper<List<DokumentMetadata>>,
        temakoder: Set<String>,
    ): ResultatWrapper<List<SakstemaData>>

    fun opprettSakstemaForEnTemagruppe(
        temakoder: Set<String>,
        alleSaker: List<Sak>,
        alleDokumentMetadata: List<DokumentMetadata>,
    ): ResultatWrapper<List<SakstemaData>>

    fun tilhorendeDokumentMetadata(
        alleDokumentMetadata: List<DokumentMetadata>,
        temakode: String,
        tilhorendeSaker: List<Sak>,
    ): List<DokumentMetadata>

    fun sakerITemagruppe(
        alleSaker: List<Sak>,
        temakode: String,
    ): List<Sak>

    fun getTemanavnForTemakode(temakode: String): ResultatWrapper<String>
}

class SakstemaServiceImpl
    @Autowired
    constructor(
        private val safService: SafService,
        private val kodeverk: EnhetligKodeverk.Service,
    ) : SakstemaService {
        override fun hentSakstema(
            saker: List<Sak>,
            fnr: String,
        ): ResultatWrapper<List<SakstemaData>> {
            val wrapper = safService.hentJournalposter(fnr)

            return try {
                val temakoder = hentAlleTema(saker, wrapper.resultat)
                opprettSakstemaresultat(saker, wrapper, temakoder)
            } catch (e: FeilendeBaksystemException) {
                val temakoder = hentAlleTema(saker, wrapper.resultat)
                wrapper.feilendeSystemer.add(e.baksystem)
                opprettSakstemaresultat(saker, wrapper, temakoder)
            }
        }

        override fun opprettSakstemaresultat(
            saker: List<Sak>,
            wrapper: ResultatWrapper<List<DokumentMetadata>>,
            temakoder: Set<String>,
        ): ResultatWrapper<List<SakstemaData>> =
            opprettSakstemaForEnTemagruppe(
                temakoder = temakoder,
                alleSaker = saker,
                alleDokumentMetadata = wrapper.resultat,
            )

        override fun opprettSakstemaForEnTemagruppe(
            temakoder: Set<String>,
            alleSaker: List<Sak>,
            alleDokumentMetadata: List<DokumentMetadata>,
        ): ResultatWrapper<List<SakstemaData>> {
            val feilendeBaksystem: MutableSet<Baksystem> = mutableSetOf()
            val sakstema =
                temakoder.map { temakode: String ->
                    val tilhorendeSaker = sakerITemagruppe(alleSaker, temakode)
                    val tilhorendeDokumentMetadata = tilhorendeDokumentMetadata(alleDokumentMetadata, temakode, tilhorendeSaker)
                    val temanavn = getTemanavnForTemakode(temakode)
                    feilendeBaksystem.addAll(temanavn.feilendeSystemer)
                    SakstemaData(
                        temakode = temakode,
                        temanavn = temanavn.resultat,
                        erGruppert = false,
                        dokumentMetadata = tilhorendeDokumentMetadata,
                        tilhorendeSaker = tilhorendeSaker,
                    )
                }
            return ResultatWrapper(fjernGamleDokumenter(sakstema), feilendeBaksystem)
        }

        override fun tilhorendeDokumentMetadata(
            alleDokumentMetadata: List<DokumentMetadata>,
            temakode: String,
            tilhorendeSaker: List<Sak>,
        ): List<DokumentMetadata> =
            alleDokumentMetadata
                .stream()
                .filter(tilhorendeFraJoark(tilhorendeSaker).or(tilhorendeFraHenvendelse(temakode)))
                .collect(Collectors.toList())

        override fun sakerITemagruppe(
            alleSaker: List<Sak>,
            temakode: String,
        ): List<Sak> =
            alleSaker
                .stream()
                .filter { sak: Sak -> temakode == sak.temakode }
                .collect(Collectors.toList())

        override fun getTemanavnForTemakode(temakode: String): ResultatWrapper<String> {
            val temanavn = kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA).hentVerdiEllerNull(temakode)
            return if (temanavn == null) {
                LOG.warn("Fant ikke temanavn for temakode $temakode. Bruker temakode som generisk tittel.")
                ResultatWrapper(temakode).withEkstraFeilendeSystem(Baksystem.KODEVERK)
            } else {
                ResultatWrapper(temanavn)
            }
        }

        companion object {
            private val LOG = LoggerFactory.getLogger(SakstemaServiceImpl::class.java)

            fun hentAlleTema(
                saker: List<Sak>,
                dokumentMetadata: List<DokumentMetadata>,
            ): Set<String> {
                val sakerTema = saker.map { it.temakode }
                val dokumentTema =
                    dokumentMetadata.filter { it.baksystem.contains(Baksystem.HENVENDELSE) }.map { it.temakode }
                return (sakerTema + dokumentTema).toSet()
            }

            private fun tilhorendeFraJoark(tilhorendeSaker: List<Sak>): Predicate<DokumentMetadata> =
                Predicate { dm: DokumentMetadata ->
                    tilhorendeSaker
                        .stream()
                        .map { obj: Sak -> obj.saksId }
                        .toList()
                        .contains(dm.tilhorendeSakid)
                }

            private fun tilhorendeFraHenvendelse(temakode: String): Predicate<DokumentMetadata> =
                Predicate { dm: DokumentMetadata -> dm.baksystem.contains(Baksystem.HENVENDELSE) && dm.temakode == temakode }
        }
    }
