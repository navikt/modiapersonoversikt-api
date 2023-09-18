package no.nav.modiapersonoversikt.service.sakstema

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json.Default.encodeToJsonElement
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
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
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusService
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
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

    @Autowired
    lateinit var soknadsstatusService: SoknadsstatusService

    fun hentSakstema(saker: List<Sak>, fnr: String): ResultatWrapper<List<Sakstema>> {
        val wrapper = safService.hentJournalposter(fnr)

        return try {
            val behandlingskjeder: Map<String, List<Behandlingskjede?>> =
                sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(
                    fnr
                )

            try {
                val soknadsstatus = soknadsstatusService.hentBehandlingerMedHendelser(fnr, false)
                val filtrerteBehandlinger = soknadsstatusService.grupperBehandlingerPaaTema(soknadsstatus, true)
                val ufiltrerteBehandlinger = soknadsstatusService.grupperBehandlingerPaaTema(soknadsstatus, false)
                Experiment.compareSakogbehandlingAndSoknadsstatus(
                    behandlingskjeder,
                    filtrerteBehandlinger,
                    ufiltrerteBehandlinger
                )
            } catch (e: Exception) {
                Experiment.experimentLog(
                    "Failed to fetch experiment",
                    Level.ERROR,
                    behandlingskjeder,
                    null,
                    null,
                    false,
                    e
                )
            }

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

@Serializable
private data class BehandlingskjedeDTO(
    val sistOppdatert: LocalDateTime,
    val status: BehandlingsStatus
)

internal object Experiment {
    fun compareSakogbehandlingAndSoknadsstatus(
        control: Map<String, List<Behandlingskjede?>>,
        experiment: Map<String, Soknadsstatus>?,
        experimentNoFilter: Map<String, Soknadsstatus>?
    ): Boolean {
        if (experiment == null) {
            experimentLog("Eksperiment var null", Level.ERROR, control, null, experimentNoFilter, false)
            return false
        }
        try {
            if (!checkTheme(control, experiment!!)) {
                experimentLog(
                    "Eksperiment og kontroll hadde ikke samme tema",
                    Level.ERROR,
                    control,
                    experiment,
                    experimentNoFilter,
                    false
                )
                return false
            }
            if (!checkBehandlinger(control, experiment)) {
                experimentLog(
                    "Eksperiment og kontroll hadde ikke samme behandlingsstatus",
                    Level.ERROR,
                    control,
                    experiment,
                    experimentNoFilter
                )
                return false
            }

            experimentLog("Eksperiment og kontroll var like", Level.INFO, control, experiment, experimentNoFilter, true)
            return true
        } catch (e: Exception) {
            experimentLog(
                "Klarte ikke å håndtere eksperiment",
                Level.ERROR,
                control,
                experiment,
                experimentNoFilter,
                false,
                e
            )
            return false
        }
    }

    private fun checkBehandlinger(
        control: Map<String, List<Behandlingskjede?>>,
        experiment: Map<String, Soknadsstatus>
    ): Boolean {
        for (controlEntry in control.entries) {
            val controlSoknadsstatus = Soknadsstatus()

            for (behandling in controlEntry.value) {
                if (behandling?.status == null) continue

                when (behandling.status) {
                    BehandlingsStatus.UNDER_BEHANDLING -> controlSoknadsstatus.underBehandling++
                    BehandlingsStatus.AVBRUTT -> controlSoknadsstatus.avbrutt++
                    BehandlingsStatus.FERDIG_BEHANDLET -> controlSoknadsstatus.ferdigBehandlet++
                }
            }

            val experimentStatus = experiment[controlEntry.key] ?: return false

            if (controlSoknadsstatus.avbrutt != experimentStatus.avbrutt) return false
            if (controlSoknadsstatus.ferdigBehandlet != experimentStatus.ferdigBehandlet) return false
            if (controlSoknadsstatus.underBehandling != experimentStatus.underBehandling) return false
        }

        return true
    }

    private fun checkTheme(
        control: Map<String, List<Behandlingskjede?>>,
        experiment: Map<String, Soknadsstatus>
    ): Boolean {
        val controlThemes = control.keys.toMutableSet()
        val experimentThemes = experiment.keys.toMutableSet()

        for (key in control.keys) {
            if (!experimentThemes.contains(key)) {
                return false
            }
            controlThemes.remove(key)
            experimentThemes.remove(key)
        }

        return experimentThemes.isEmpty()
    }

    fun experimentLog(
        message: String,
        level: Level,
        control: Map<String, List<Behandlingskjede?>>?,
        experiment: Map<String, Soknadsstatus>?,
        experimentNoFilter: Map<String, Soknadsstatus>?,
        success: Boolean = false,
        exception: Throwable? = null,
    ) {
        val header = "[Experiment]: Soknadsstatus"

        var controlJson: JsonObject? = null
        if (control != null) {
            try {
                controlJson = encodeControl(control)
            } catch (e: Exception) {
                TjenestekallLogg.error(
                    header,
                    fields = mapOf("message" to "Failed to encode control", "success" to false),
                    throwable = e
                )
            }
        }
        var experimentJson: JsonObject? = null
        if (experiment != null) {
            try {
                experimentJson = encodeExperiment(experiment)
            } catch (e: Exception) {
                TjenestekallLogg.error(
                    header,
                    fields = mapOf("message" to "Failed to encode experiment", "success" to false),
                    throwable = e
                )
            }
        }

        var experimentNoFilterJson: JsonObject? = null

        if (experimentNoFilter != null) {
            try {
                experimentNoFilterJson = encodeExperiment(experimentNoFilter)
            } catch (e: Exception) {
                TjenestekallLogg.error(
                    header,
                    fields = mapOf("message" to "Failed to encode experiment without filter", "success" to false)
                )
            }
        }

        val fields =
            mapOf(
                message to message,
                "control" to controlJson,
                "experiment" to experimentJson,
                "experimentNoFilter" to experimentNoFilterJson,
                "success" to success
            )

        if (level == Level.ERROR) {
            TjenestekallLogg.error(header, fields = fields, throwable = exception)
        }

        if (level == Level.INFO) {
            TjenestekallLogg.info(header, fields)
        }
    }

    private fun encodeControl(control: Map<String, List<Behandlingskjede?>>): JsonObject {
        return control.toJsonObject {
            val behandlingskjeder = it as List<Behandlingskjede?>?
            val behandlingsKjedeJson = behandlingskjeder?.filterNotNull()?.map { kjede ->
                val behandlingsKjedeDTO = BehandlingskjedeDTO(
                    sistOppdatert = kjede.sistOppdatert.toKotlinLocalDateTime(),
                    status = kjede.status
                )
                encodeToJsonElement(BehandlingskjedeDTO.serializer(), behandlingsKjedeDTO)
            } ?: listOf()
            JsonArray(behandlingsKjedeJson)
        }
    }

    private fun encodeExperiment(experiment: Map<String, Soknadsstatus>): JsonObject {
        return experiment.toJsonObject {
            val soknadsstatus = it as Soknadsstatus
            encodeToJsonElement(Soknadsstatus.serializer(), soknadsstatus)
        }
    }

    fun Map<*, *>.toJsonObject(mapValue: (Any?) -> JsonElement) =
        JsonObject(mapKeys { it.key.toString() }.mapValues { mapValue(it.value) })
}
