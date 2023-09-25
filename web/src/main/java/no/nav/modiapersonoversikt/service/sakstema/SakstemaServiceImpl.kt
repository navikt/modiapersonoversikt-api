package no.nav.modiapersonoversikt.service.sakstema

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.FeilendeBaksystemException
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakogbehandling.FilterUtils.fjernGamleDokumenter
import no.nav.modiapersonoversikt.service.sakogbehandling.SakOgBehandlingService
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
import no.nav.modiapersonoversikt.service.soknadsstatus.Soknadsstatus
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusSakstema
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.collections.HashSet

interface SakstemaService {
    fun hentSakstema(saker: List<Sak>, fnr: String): ResultatWrapper<List<Sakstema>>
    fun hentSakstemaSoknadsstatus(saker: List<Sak>, fnr: String): ResultatWrapper<List<SoknadsstatusSakstema>>
    fun opprettSakstemaresultat(
        saker: List<Sak>,
        wrapper: ResultatWrapper<List<DokumentMetadata>>,
        temakoder: Set<String>,
        behandlingskjeder: Map<String, List<Behandlingskjede?>?>
    ): ResultatWrapper<List<Sakstema>>

    fun opprettSakstemaresultatSoknadsstatus(
        saker: List<Sak>,
        wrapper: ResultatWrapper<List<DokumentMetadata>>,
        temakoder: Set<String>,
        soknadsstatuser: Map<String, Soknadsstatus>
    ): ResultatWrapper<List<SoknadsstatusSakstema>>

    fun opprettSakstemaForEnTemagruppe(
        temakoder: Set<String>,
        alleSaker: List<Sak>,
        alleDokumentMetadata: List<DokumentMetadata>,
        behandlingskjeder: Map<String, List<Behandlingskjede?>?>
    ): ResultatWrapper<List<Sakstema>>

    fun opprettSakstemaForEnTemagruppeSoknadsstatus(
        temakoder: Set<String>,
        alleSaker: List<Sak>,
        alleDokumentMetadata: List<DokumentMetadata>,
        soknadsstatuser: Map<String, Soknadsstatus>
    ): ResultatWrapper<List<SoknadsstatusSakstema>>

    fun tilhorendeDokumentMetadata(
        alleDokumentMetadata: List<DokumentMetadata>,
        temakode: String,
        tilhorendeSaker: List<Sak>
    ): List<DokumentMetadata>

    fun sakerITemagruppe(alleSaker: List<Sak>, temakode: String): List<Sak>
    fun getTemanavnForTemakode(temakode: String): ResultatWrapper<String>
    fun convertBehandlingskjederToSoknadsstatuser(
        behandlingskjeder: Map<String, List<Behandlingskjede?>>,
    ): Map<String, Soknadsstatus>
}

class SakstemaServiceImpl @Autowired constructor(
    private val safService: SafService,
    private val sakOgBehandlingService: SakOgBehandlingService,
    private val kodeverk: EnhetligKodeverk.Service,
    private val soknadsstatusService: SoknadsstatusService
) : SakstemaService {

    override fun hentSakstema(saker: List<Sak>, fnr: String): ResultatWrapper<List<Sakstema>> {
        val wrapper = safService.hentJournalposter(fnr)

        return try {
            val behandlingskjeder: Map<String, List<Behandlingskjede?>> =
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

    override fun hentSakstemaSoknadsstatus(
        saker: List<Sak>,
        fnr: String
    ): ResultatWrapper<List<SoknadsstatusSakstema>> {
        val wrapper = safService.hentJournalposter(fnr)

        return try {
            val behandlingskjeder: Map<String, List<Behandlingskjede?>> =
                sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(
                    fnr
                )

            val behandlingIderFraSakOgBehandling =
                behandlingskjeder.values.flatMap { kjede -> kjede.map { it?.behandlingId } }.filterNotNull()

            var soknadsstatuser: Map<String, Soknadsstatus>
            try {
                soknadsstatuser = soknadsstatusService.hentBehandlingerGruppertPaaTema(
                    fnr,
                    behandlingerSomAlleredeErInkludert = behandlingIderFraSakOgBehandling.toSet()
                )
            } catch (e: Exception) {
                soknadsstatuser = emptyMap()
                LOG.error("Klarte ikke å hente ut soknadsstatus")
            }

            val behandlingsKjederSomSoknadsstatuser = convertBehandlingskjederToSoknadsstatuser(behandlingskjeder)
            val alleSoknadsstatuser = soknadsstatuser + behandlingsKjederSomSoknadsstatuser

            val temakoder = hentAlleTemaSoknadsstatus(saker, wrapper.resultat, alleSoknadsstatuser)
            opprettSakstemaresultatSoknadsstatus(saker, wrapper, temakoder, alleSoknadsstatuser)
        } catch (e: FeilendeBaksystemException) {
            val temakoder = hentAlleTemaSoknadsstatus(saker, wrapper.resultat, emptyMap<String, Soknadsstatus>())
            wrapper.feilendeSystemer.add(e.baksystem)
            opprettSakstemaresultatSoknadsstatus(saker, wrapper, temakoder, emptyMap<String, Soknadsstatus>())
        }
    }

    override fun opprettSakstemaresultat(
        saker: List<Sak>,
        wrapper: ResultatWrapper<List<DokumentMetadata>>,
        temakoder: Set<String>,
        behandlingskjeder: Map<String, List<Behandlingskjede?>?>
    ): ResultatWrapper<List<Sakstema>> {
        return opprettSakstemaForEnTemagruppe(temakoder, saker, wrapper.resultat, behandlingskjeder)
            .withEkstraFeilendeBaksystemer(wrapper.feilendeSystemer)
    }

    override fun opprettSakstemaresultatSoknadsstatus(
        saker: List<Sak>,
        wrapper: ResultatWrapper<List<DokumentMetadata>>,
        temakoder: Set<String>,
        soknadsstatuser: Map<String, Soknadsstatus>
    ): ResultatWrapper<List<SoknadsstatusSakstema>> {
        return opprettSakstemaForEnTemagruppeSoknadsstatus(
            temakoder = temakoder,
            alleSaker = saker,
            alleDokumentMetadata = wrapper.resultat,
            soknadsstatuser
        )
    }

    override fun opprettSakstemaForEnTemagruppe(
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

    override fun opprettSakstemaForEnTemagruppeSoknadsstatus(
        temakoder: Set<String>,
        alleSaker: List<Sak>,
        alleDokumentMetadata: List<DokumentMetadata>,
        soknadsstatuser: Map<String, Soknadsstatus>
    ): ResultatWrapper<List<SoknadsstatusSakstema>> {
        val feilendeBaksystem: MutableSet<Baksystem> = mutableSetOf()
        val sakstema = temakoder.map { temakode: String ->
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
        tilhorendeSaker: List<Sak>
    ): List<DokumentMetadata> {
        return alleDokumentMetadata
            .stream()
            .filter(tilhorendeFraJoark(tilhorendeSaker).or(tilhorendeFraHenvendelse(temakode)))
            .collect(Collectors.toList())
    }

    override fun sakerITemagruppe(alleSaker: List<Sak>, temakode: String): List<Sak> {
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

        fun hentAlleTemaSoknadsstatus(
            saker: List<Sak>,
            dokumentMetadata: List<DokumentMetadata>,
            soknadsstatuser: Map<String, Soknadsstatus>
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

    override fun convertBehandlingskjederToSoknadsstatuser(
        behandlingskjeder: Map<String, List<Behandlingskjede?>>,
    ): Map<String, Soknadsstatus> {
        val res = mutableMapOf<String, Soknadsstatus>()
        for (behandlingskjede in behandlingskjeder.entries) {
            val soknadsstatus = Soknadsstatus()

            for (behandling in behandlingskjede.value) {
                if (behandling?.status == null) continue

                when (behandling.status) {
                    BehandlingsStatus.UNDER_BEHANDLING -> soknadsstatus.underBehandling++
                    BehandlingsStatus.AVBRUTT -> soknadsstatus.avbrutt++
                    BehandlingsStatus.FERDIG_BEHANDLET -> soknadsstatus.ferdigBehandlet++
                }

                if (soknadsstatus.sistOppdatert == null || behandling.sistOppdatert.isAfter(soknadsstatus.sistOppdatert!!.toJavaLocalDateTime())) {
                    soknadsstatus.sistOppdatert = behandling.sistOppdatert.toKotlinLocalDateTime()
                }
            }
            res[behandlingskjede.key] = soknadsstatus
        }

        return res
    }
}
