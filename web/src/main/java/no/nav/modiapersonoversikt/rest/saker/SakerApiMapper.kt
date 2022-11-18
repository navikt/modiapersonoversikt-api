package no.nav.modiapersonoversikt.rest.saker

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.saf.domain.Dokument
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
import no.nav.personoversikt.common.kabac.Decision
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.util.*

object SakerApiMapper {
    fun createMappingContext(
        tilgangskontroll: Tilgangskontroll,
        enhet: EnhetId,
        sakstemaer: List<Sakstema>,
        feilendeSystem: Set<Baksystem>,
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
            sakstemaer = sakstemaer,
            feilendeSystem = feilendeSystem,
            tematilgang = tematilgang,
        )
    }

    class MappingContext(
        private val sakstemaer: List<Sakstema>,
        private val feilendeSystem: Set<Baksystem>,
        private val tematilgang: Map<String, Boolean>,
    ) {
        fun mapTilResultat() = SakerApi.Resultat(
            sakstemaer.map { sakstema ->
                val harTilgang = tematilgang[sakstema.temakode] ?: false
                SakerApi.Sakstema(
                    temakode = sakstema.temakode,
                    temanavn = sakstema.temanavn,
                    erGruppert = sakstema.erGruppert,
                    behandlingskjeder = sakstema.behandlingskjeder.map(::mapTilBehandlingskjede),
                    dokumentMetadata = sakstema.dokumentMetadata.map(::mapTilDokumentMetadata),
                    tilhørendeSaker = sakstema.tilhorendeSaker.map(::mapTilTilhorendeSak),
                    feilkoder = sakstema.feilkoder,
                    harTilgang = harTilgang,
                )
            }
        )

        private fun mapTilBehandlingskjede(behandlingskjede: Behandlingskjede) = SakerApi.Behandlingskjede(
            status = behandlingskjede.status,
            sistOppdatert = toLegacyData(behandlingskjede.sistOppdatert)
        )

        private fun mapTilDokumentMetadata(behandlingskjede: DokumentMetadata) = SakerApi.Dokumentmetadata(
            id = UUID.randomUUID().toString(),
            retning = behandlingskjede.retning,
            dato = toLegacyData(behandlingskjede.dato),
            navn = behandlingskjede.navn,
            journalpostId = behandlingskjede.journalpostId,
            hoveddokument = mapTilDokument(behandlingskjede.hoveddokument),
            vedlegg = behandlingskjede.vedlegg.map(::mapTilDokument),
            avsender = behandlingskjede.avsender,
            mottaker = behandlingskjede.mottaker,
            tilhørendeSaksid = behandlingskjede.tilhorendeSakid,
            tilhørendeFagsaksid = behandlingskjede.tilhorendeFagsakId,
            baksystem = behandlingskjede.baksystem,
            temakode = behandlingskjede.temakode,
            temakodeVisning = behandlingskjede.temakodeVisning,
            ettersending = false,
            erJournalført = behandlingskjede.isErJournalfort,
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
            avsluttet = toLegacyData(sak.avsluttet),
            fagsystem = sak.fagsystem,
            baksystem = sak.baksystem,
        )

        private fun toLegacyData(dato: LocalDateTime) = SakerApi.LegacyDato(
            år = dato.year,
            måned = dato.monthValue,
            dag = dato.dayOfMonth,
            time = dato.hour,
            minutt = dato.minute,
            sekund = dato.second,
        )

        private fun toLegacyData(maybeDato: Optional<DateTime>) = maybeDato
            .map { dato ->
                SakerApi.LegacyDato(
                    år = dato.year,
                    måned = dato.monthOfYear,
                    dag = dato.dayOfMonth,
                    time = dato.hourOfDay,
                    minutt = dato.minuteOfHour,
                    sekund = dato.secondOfMinute,
                )
            }.orElse(null)
    }
}
