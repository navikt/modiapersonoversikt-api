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
import no.nav.modiapersonoversikt.service.soknadsstatus.Soknadsstatus
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusSakstema
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.function.Predicate
import java.util.stream.Collectors

interface SakstemaService {
    fun hentSakstemaSoknadsstatus(
        saker: List<Sak>,
        fnr: String,
    ): ResultatWrapper<List<SoknadsstatusSakstema>>

    fun opprettSakstemaresultatSoknadsstatus(
        saker: List<Sak>,
        wrapper: ResultatWrapper<List<DokumentMetadata>>,
        temakoder: Set<String>,
        soknadsstatuser: Map<String, Soknadsstatus>,
    ): ResultatWrapper<List<SoknadsstatusSakstema>>

    fun opprettSakstemaForEnTemagruppeSoknadsstatus(
        temakoder: Set<String>,
        alleSaker: List<Sak>,
        alleDokumentMetadata: List<DokumentMetadata>,
        soknadsstatuser: Map<String, Soknadsstatus>,
    ): ResultatWrapper<List<SoknadsstatusSakstema>>

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
        private val soknadsstatusService: SoknadsstatusService,
    ) : SakstemaService {
        override fun hentSakstemaSoknadsstatus(
            saker: List<Sak>,
            fnr: String,
        ): ResultatWrapper<List<SoknadsstatusSakstema>> {
            val wrapper = safService.hentJournalposter(fnr)

            return try {
                var soknadsstatuser: Map<String, Soknadsstatus>
                try {
                    soknadsstatuser =
                        soknadsstatusService.hentBehandlingerGruppertPaaTema(fnr)
                } catch (e: Exception) {
                    soknadsstatuser = emptyMap()
                    LOG.error("Klarte ikke å hente ut soknadsstatus", e)
                }

                val temakoder = hentAlleTemaSoknadsstatus(saker, wrapper.resultat, soknadsstatuser)
                opprettSakstemaresultatSoknadsstatus(saker, wrapper, temakoder, soknadsstatuser)
            } catch (e: FeilendeBaksystemException) {
                val temakoder = hentAlleTemaSoknadsstatus(saker, wrapper.resultat, emptyMap<String, Soknadsstatus>())
                wrapper.feilendeSystemer.add(e.baksystem)
                opprettSakstemaresultatSoknadsstatus(saker, wrapper, temakoder, emptyMap<String, Soknadsstatus>())
            }
        }

        override fun opprettSakstemaresultatSoknadsstatus(
            saker: List<Sak>,
            wrapper: ResultatWrapper<List<DokumentMetadata>>,
            temakoder: Set<String>,
            soknadsstatuser: Map<String, Soknadsstatus>,
        ): ResultatWrapper<List<SoknadsstatusSakstema>> {
            return opprettSakstemaForEnTemagruppeSoknadsstatus(
                temakoder = temakoder,
                alleSaker = saker,
                alleDokumentMetadata = wrapper.resultat,
                soknadsstatuser,
            )
        }

        override fun opprettSakstemaForEnTemagruppeSoknadsstatus(
            temakoder: Set<String>,
            alleSaker: List<Sak>,
            alleDokumentMetadata: List<DokumentMetadata>,
            soknadsstatuser: Map<String, Soknadsstatus>,
        ): ResultatWrapper<List<SoknadsstatusSakstema>> {
            val feilendeBaksystem: MutableSet<Baksystem> = mutableSetOf()
            val sakstema =
                temakoder.map { temakode: String ->
                    val tilhorendeSaker = sakerITemagruppe(alleSaker, temakode)
                    val tilhorendeDokumentMetadata = tilhorendeDokumentMetadata(alleDokumentMetadata, temakode, tilhorendeSaker)
                    val temanavn = getTemanavnForTemakode(temakode)
                    feilendeBaksystem.addAll(temanavn.feilendeSystemer)
                    SoknadsstatusSakstema(
                        temakode = temakode,
                        temanavn = temanavn.resultat,
                        erGruppert = false,
                        soknadsstatus = soknadsstatuser[temakode] ?: Soknadsstatus(),
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
        ): List<DokumentMetadata> {
            return alleDokumentMetadata
                .stream()
                .filter(tilhorendeFraJoark(tilhorendeSaker).or(tilhorendeFraHenvendelse(temakode)))
                .collect(Collectors.toList())
        }

        override fun sakerITemagruppe(
            alleSaker: List<Sak>,
            temakode: String,
        ): List<Sak> {
            return alleSaker.stream()
                .filter { sak: Sak -> temakode == sak.temakode }
                .collect(Collectors.toList())
        }

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

            fun hentAlleTemaSoknadsstatus(
                saker: List<Sak>,
                dokumentMetadata: List<DokumentMetadata>,
                soknadsstatuser: Map<String, Soknadsstatus>,
            ): Set<String> {
                val sakerTema = saker.map { it.temakode }
                val dokumentTema =
                    dokumentMetadata.filter { it.baksystem.contains(Baksystem.HENVENDELSE) }.map { it.temakode }
                val soknadsstatusTema = soknadsstatuser.keys
                return (sakerTema + dokumentTema + soknadsstatusTema).toSet()
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
