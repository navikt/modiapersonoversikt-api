package no.nav.modiapersonoversikt.rest.saker

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.saf.domain.Dokument
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
import no.nav.modiapersonoversikt.utils.ConvertionUtils.toJavaDateTime
import no.nav.personoversikt.common.kabac.Decision
import java.util.*

object SakerApiMapper {
    fun createMappingContext(
        tilgangskontroll: Tilgangskontroll,
        enhet: EnhetId,
        sakstemaer: List<Sakstema>,
    ): MappingContext {
        val tematilgang = sakstemaer
            .map { it.temakode }
            .distinct()
            .associateWith { tema ->
                val decision = tilgangskontroll
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
        fun mapTilResultat(sakstemaer: List<Sakstema>) = SakerApi.Resultat(
            sakstemaer.map { sakstema ->
                val harTilgang = tematilgang[sakstema.temakode] ?: false
                val tilhorendeSaker = sakstema.tilhorendeSaker.map(::mapTilTilhorendeSak)
                SakerApi.Sakstema(
                    temakode = sakstema.temakode,
                    temanavn = sakstema.temanavn,
                    erGruppert = sakstema.erGruppert,
                    behandlingskjeder = sakstema.behandlingskjeder.map(::mapTilBehandlingskjede),
                    dokumentMetadata = sakstema.dokumentMetadata.map(::mapTilDokumentMetadata),
                    tilhorendeSaker = tilhorendeSaker,
                    feilkoder = sakstema.feilkoder,
                    harTilgang = harTilgang,
                )
            }
        )

        private fun mapTilBehandlingskjede(behandlingskjede: Behandlingskjede) = SakerApi.Behandlingskjede(
            status = behandlingskjede.status,
            sistOppdatert = behandlingskjede.sistOppdatert,
            sistOppdatertV2 = behandlingskjede.sistOppdatert,
        )

        private fun mapTilDokumentMetadata(behandlingskjede: DokumentMetadata) = SakerApi.Dokumentmetadata(
            id = UUID.randomUUID().toString(),
            retning = behandlingskjede.retning,
            dato = behandlingskjede.dato,
            datoV2 = behandlingskjede.dato,
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
            temakode = behandlingskjede.temakode,
            temakodeVisning = behandlingskjede.temakodeVisning,
            ettersending = false,
            erJournalfort = behandlingskjede.isErJournalfort,
            feil = SakerApi.Feil(
                inneholderFeil = behandlingskjede.feilWrapper?.inneholderFeil ?: false,
                feilmelding = behandlingskjede.feilWrapper?.feilmelding
            ),
        )

        private fun mapTilDokument(dokument: Dokument) = SakerApi.Dokument(
            tittel = dokument.tittel,
            dokumentreferanse = dokument.dokumentreferanse,
            kanVises = dokument.isKanVises,
            logiskDokument = dokument.isLogiskDokument,
            skjerming = dokument.skjerming,
            erKassert = dokument.isKassert,
            dokumentStatus = dokument.dokumentStatus,
        )

        private fun mapTilTilhorendeSak(sak: Sak) = SakerApi.Sak(
            temakode = sak.temakode,
            saksid = sak.saksId,
            fagsaksnummer = sak.fagsaksnummer,
            avsluttet = sak.avsluttet.map { it.toJavaDateTime() }.orElse(null),
            avsluttetV2 = sak.avsluttet.map { it.toJavaDateTime() }.orElse(null),
            fagsystem = sak.fagsystem,
            baksystem = sak.baksystem,
        )
    }
}
