package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.saker

import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.widget.ModiaSakstema
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.*
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SaksService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SakstemaService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.SaksoversiktService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType

@Path("/saker/{fnr}")
@Produces(MediaType.APPLICATION_JSON)
class SakerController @Inject constructor(private val saksoversiktService: SaksoversiktService,
                                          private val sakstemaService: SakstemaService,
                                          private val saksService: SaksService,
                                          private val tilgangskontrollService: TilgangskontrollService,
                                          private val unleashService: UnleashService) {
    @GET
    @Path("/")
    fun hent(@Context request: HttpServletRequest, @PathParam("fnr") fødselsnummer: String): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))
        if (!tilgangskontrollService.harGodkjentEnhet(request)) throw NotAuthorizedException("Ikke tilgang.")

        val sakerWrapper = saksService.hentAlleSaker(fødselsnummer)
        val sakstemaWrapper = sakstemaService.hentSakstema(sakerWrapper.resultat, fødselsnummer, false)

        tilgangskontrollService.markerIkkeJournalforte(sakstemaWrapper.resultat)
        saksoversiktService.fjernGamleDokumenter(sakstemaWrapper.resultat)

        val resultat = ResultatWrapper(mapTilModiaSakstema(sakstemaWrapper.resultat, RestUtils.hentValgtEnhet(request)),
                collectFeilendeSystemer(sakerWrapper, sakstemaWrapper))

        return byggResultat(resultat)
    }

    private fun byggResultat(resultat: ResultatWrapper<List<ModiaSakstema>>): Map<String, Any?> {
        return mapOf(
                "resultat" to resultat.resultat.map {
                    mapOf(
                            "temakode" to it.temakode,
                            "temanavn" to it.temanavn,
                            "erGruppert" to it.erGruppert,
                            "behandlingskjeder" to hentBehandlingskjeder(it.behandlingskjeder),
                            "dokumentMetadata" to hentDokumentMetadata(it.dokumentMetadata),
                            "tilhørendeSaker" to hentTilhørendeSaker(it.tilhorendeSaker),
                            "feilkoder" to it.feilkoder,
                            "harTilgang" to it.harTilgang
                    )
                }
        )
    }

    private fun hentBehandlingskjeder(behandlingskjeder: List<Behandlingskjede>): List<Map<String, Any?>> {
        return behandlingskjeder.map {
            mapOf(
                    "status" to it.status,
                    "sistOppdatert" to hentDato(it.sistOppdatert)
            )
        }
    }

    private fun hentDokumentMetadata(dokumenter: List<DokumentMetadata>): List<Map<String, Any?>> {
        return dokumenter.map {
            mapOf(
                    "retning" to it.retning,
                    "dato" to hentDato(it.dato),
                    "navn" to it.navn,
                    "journalpostId" to it.journalpostId,
                    "hoveddokument" to hentDokument(it.hoveddokument),
                    "vedlegg" to it.vedlegg.map { hentDokument(it) },
                    "avsender" to it.avsender,
                    "mottaker" to it.mottaker,
                    "tilhørendeSaksid" to it.tilhorendeSakid,
                    "tilhørendeFagsaksid" to it.tilhorendeFagsakId,
                    "behandlingsid" to it.behandlingsId,
                    "baksystem" to it.baksystem,
                    "temakode" to it.temakode,
                    "temakodeVisning" to it.temakodeVisning,
                    "ettersending" to it.isEttersending,
                    "erJournalført" to it.isErJournalfort,
                    "kategorinotat" to it.kategoriNotat,
                    "feil" to mapOf(
                            "inneholderFeil" to it.feilWrapper?.inneholderFeil,
                            "feilmelding" to it.feilWrapper?.feilmelding
                    )
            )
        }
    }

    private fun hentTilhørendeSaker(saker: List<Sak>): List<Map<String, Any?>> {
        return saker.map {
            mapOf(
                    "temakode" to it.temakode,
                    "saksid" to it.saksId,
                    "fagsaksnummer" to it.fagsaksnummer,
                    "avsluttet" to hentDato(it.avsluttet),
                    "fagsystem" to it.fagsystem,
                    "baksystem" to it.baksystem
            )
        }
    }

    private fun hentDokument(dokument: Dokument): Map<String, Any?> {
        return mapOf(
                "tittel" to dokument.tittel,
                "dokumentreferanse" to dokument.dokumentreferanse,
                "kanVises" to dokument.isKanVises,
                "logiskDokument" to dokument.isLogiskDokument
        )
    }

    private fun hentDato(date: LocalDateTime): Map<String, Any?> {
        return mapOf(
                "år" to date.year,
                "måned" to date.monthValue,
                "dag" to date.dayOfMonth,
                "time" to date.hour,
                "minutt" to date.minute,
                "sekund" to date.second
        )
    }

    private fun hentDato(date: Optional<DateTime>): Map<String, Any?>? {
        if (!date.isPresent) return null
        return mapOf(
                "år" to date.get().year,
                "måned" to date.get().monthOfYear,
                "dag" to date.get().dayOfMonth,
                "time" to date.get().hourOfDay,
                "minutt" to date.get().minuteOfHour,
                "sekund" to date.get().secondOfMinute
        )
    }

    private fun mapTilModiaSakstema(sakstemaList: List<Sakstema>, valgtEnhet: String): List<ModiaSakstema> {
        return sakstemaList.map { sakstema -> createModiaSakstema(sakstema, valgtEnhet) }
    }

    private fun collectFeilendeSystemer(sakerWrapper: ResultatWrapper<List<Sak>>, sakstemaWrapper: ResultatWrapper<List<Sakstema>>): Set<Baksystem> {
        return sakerWrapper.feilendeSystemer.union(sakstemaWrapper.feilendeSystemer)
    }

    private fun createModiaSakstema(sakstema: Sakstema, valgtEnhet: String): ModiaSakstema {
        return ModiaSakstema(sakstema)
                .withTilgang(tilgangskontrollService.harEnhetTilgangTilTema(sakstema.temakode, valgtEnhet))
    }
}