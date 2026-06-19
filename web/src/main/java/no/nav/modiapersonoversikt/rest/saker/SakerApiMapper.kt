package no.nav.modiapersonoversikt.rest.saker

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.saf.domain.Dokument
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.soknadsstatus.SaksData
import no.nav.modiapersonoversikt.service.soknadsstatus.Sakstema
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusSakstema
import no.nav.modiapersonoversikt.utils.ConvertionUtils.toJavaDateTime
import no.nav.personoversikt.common.kabac.Decision
import java.util.*

object SakerApiMapper {
    @JvmName("createMappingContextSoknadsstatus")
    fun createMappingContext(
        tilgangskontroll: Tilgangskontroll,
        enhet: EnhetId,
        sakstemaer: List<SoknadsstatusSakstema>,
    ): MappingContext {
        val tematilgang =
            sakstemaer
                .map { it.temakode }
                .distinct()
                .associateWith { tema ->
                    val decision =
                        tilgangskontroll
                            .check(Policies.tilgangTilTema(enhet, tema))
                            .getDecision()

                    decision.type == Decision.Type.PERMIT
                }

        return MappingContext(
            tematilgang = tematilgang,
        )
    }

    @JvmName("createMappingContext")
    fun createMappingContextSaksDokumenter(
        tilgangskontroll: Tilgangskontroll,
        enhet: EnhetId,
        sakstemaer: List<Sakstema>,
    ): MappingContext {
        val tematilgang =
            sakstemaer
                .map { it.temakode }
                .distinct()
                .associateWith { tema ->
                    val decision =
                        tilgangskontroll
                            .check(Policies.tilgangTilTema(enhet, tema))
                            .getDecision()

                    decision.type == Decision.Type.PERMIT
                }

        return MappingContext(
            tematilgang = tematilgang,
        )
    }

    class MappingContext(
        private val tematilgang: Map<String, Boolean>,
    ) {
        fun mapTilResultat(sakstemaer: List<SoknadsstatusSakstema>) =
            SakerApi.ResultatSoknadsstatus(
                sakstemaer.map { sakstema ->
                    val harTilgang = tematilgang[sakstema.temakode] == true
                    val tilhorendeSaker = sakstema.tilhorendeSaker.map(::mapTilTilhorendeSak)
                    SakerApi.SoknadsstatusSakstema(
                        temakode = sakstema.temakode,
                        temanavn = sakstema.temanavn,
                        erGruppert = sakstema.erGruppert,
                        soknadsstatus = sakstema.soknadsstatus,
                        dokumentMetadata = sakstema.dokumentMetadata.map(::mapTilDokumentMetadata),
                        tilhorendeSaker = tilhorendeSaker,
                        feilkoder = sakstema.feilkoder,
                        harTilgang = harTilgang,
                    )
                },
            )

        fun mapTilResultatSaksDokumenter(resultatWrapper: ResultatWrapper<SaksData>) =
            SakerApi.ResultatSaksDokumenter(
                temaer = hentTemaerMedTilgangOgDato(resultatWrapper.resultat.temaer, resultatWrapper.resultat.dokumenter),
                dokumenter = resultatWrapper.resultat.dokumenter.map(::mapTilDokumentMetadata),
                feilendeSystemer = resultatWrapper.feilendeSystemer.toList(),
            )

        private fun hentTemaerMedTilgangOgDato(
            temaer: List<Sakstema>,
            dokumenter: List<DokumentMetadata>,
        ): List<SakerApi.Sakstema> {
            val dokumenterGruppertEtterTema = dokumenter.groupBy { it.temakode }
            return temaer.map { tema ->
                SakerApi.Sakstema(
                    temakode = tema.temakode,
                    temanavn = tema.temanavn,
                    harTilgang = tematilgang[tema.temakode] == true,
                    nyesteDokumentDato = dokumenterGruppertEtterTema[tema.temakode]?.maxByOrNull { it.dato }?.dato,
                )
            }
        }

        private fun mapTilDokumentMetadata(behandlingskjede: DokumentMetadata) =
            SakerApi.Dokumentmetadata(
                id = UUID.randomUUID().toString(),
                retning = behandlingskjede.retning,
                dato = behandlingskjede.dato,
                lestDato = behandlingskjede.lestDato,
                navn = behandlingskjede.navn,
                journalpostId = behandlingskjede.journalpostId,
                hoveddokument = mapTilDokument(behandlingskjede.hoveddokument),
                vedlegg = behandlingskjede.vedlegg.map(::mapTilDokument),
                avsender = behandlingskjede.avsender,
                mottaker = behandlingskjede.mottaker,
                tilhorendeSaksid = behandlingskjede.tilhorendeSakid,
                tilhorendeFagsaksid = behandlingskjede.tilhorendeFagsakId,
                baksystem = behandlingskjede.baksystem,
                temakode = behandlingskjede.temakode ?: "",
                temakodeVisning = behandlingskjede.temakodeVisning,
                ettersending = false,
                erJournalfort = behandlingskjede.isErJournalfort,
                feil =
                    SakerApi.Feil(
                        inneholderFeil = behandlingskjede.feilWrapper?.inneholderFeil == true,
                        feilmelding = behandlingskjede.feilWrapper?.feilmelding,
                    ),
            )

        private fun mapTilDokument(dokument: Dokument) =
            SakerApi.Dokument(
                tittel = dokument.tittel,
                dokumentreferanse = dokument.dokumentreferanse,
                logiskDokument = dokument.isLogiskDokument,
                skjerming = dokument.skjerming,
                dokumentStatus = dokument.dokumentStatus,
                saksbehandlerHarTilgang = dokument.saksbehandlerHarTilgang,
            )

        private fun mapTilTilhorendeSak(sak: Sak) =
            SakerApi.Sak(
                temakode = sak.temakode,
                fagsaksnummer = sak.fagsaksnummer,
                avsluttet = sak.avsluttet.map { it.toJavaDateTime() }.orElse(null),
                fagsystem = sak.fagsystem,
                fagsystemNavn = sak.fagsystemNavn.orElse(null),
                baksystem = sak.baksystem,
            )
    }
}
