package no.nav.modiapersonoversikt.service.saf

import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.Entitet
import no.nav.modiapersonoversikt.consumer.saf.generated.enums.*
import no.nav.modiapersonoversikt.consumer.saf.generated.hentbrukersdokumenter.DokumentInfo
import no.nav.modiapersonoversikt.consumer.saf.generated.hentbrukersdokumenter.Dokumentvariant
import no.nav.modiapersonoversikt.consumer.saf.generated.hentbrukersdokumenter.Journalpost
import no.nav.modiapersonoversikt.consumer.saf.generated.hentbrukersdokumenter.LogiskVedlegg
import no.nav.modiapersonoversikt.service.saf.domain.Dokument
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.saf.domain.Kommunikasjonsretning
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object SafDokumentMapper {
    private val log = LoggerFactory.getLogger(SafService::class.java)

    fun fraSafJournalpost(journalpost: Journalpost): DokumentMetadata? {
        val hovedDokument = fraSafDokumentInfo(getHoveddokumentet(journalpost)) ?: return null
        return DokumentMetadata().apply {
            retning = getRetning(journalpost)
            dato = getDato(journalpost)
            lestDato = getLestDato(journalpost)
            navn = journalpost.avsenderMottaker?.navn ?: "ukjent"
            journalpostId = journalpost.journalpostId
            hoveddokument = hovedDokument
            vedlegg = getVedlegg(journalpost)
            avsender = getAvsender(journalpost)
            mottaker = getMottaker(journalpost)
            tilhorendeSakid = journalpost.sak?.arkivsaksnummer // TODO denne er deprecated, men brukes per i dag.
            tilhorendeFagsakId = journalpost.sak?.fagsakId
            baksystem = baksystem.plus(Baksystem.SAF)
            temakode = journalpost.tema?.name
            temakodeVisning = journalpost.temanavn
        }
    }

    private fun getAvsender(journalpost: Journalpost): Entitet =
        when (journalpost.journalposttype) {
            SafServiceImpl.JOURNALPOSTTYPE_INTERN -> Entitet.NAV
            SafServiceImpl.JOURNALPOSTTYPE_INN ->
                if (sluttbrukerErMottakerEllerAvsender(
                        journalpost,
                    )
                ) {
                    Entitet.SLUTTBRUKER
                } else {
                    Entitet.EKSTERN_PART
                }
            SafServiceImpl.JOURNALPOSTTYPE_UT -> Entitet.NAV
            else -> Entitet.UKJENT
        }

    private fun getMottaker(journalpost: Journalpost): Entitet =
        when (journalpost.journalposttype) {
            SafServiceImpl.JOURNALPOSTTYPE_INTERN -> Entitet.NAV
            SafServiceImpl.JOURNALPOSTTYPE_INN -> Entitet.NAV
            SafServiceImpl.JOURNALPOSTTYPE_UT ->
                if (sluttbrukerErMottakerEllerAvsender(
                        journalpost,
                    )
                ) {
                    Entitet.SLUTTBRUKER
                } else {
                    Entitet.EKSTERN_PART
                }
            else -> Entitet.UKJENT
        }

    private fun sluttbrukerErMottakerEllerAvsender(journalpost: Journalpost): Boolean = journalpost.avsenderMottaker?.erLikBruker ?: false

    private fun getRetning(journalpost: Journalpost): Kommunikasjonsretning =
        when (journalpost.journalposttype) {
            Journalposttype.I -> Kommunikasjonsretning.INN
            Journalposttype.U -> Kommunikasjonsretning.UT
            Journalposttype.N -> Kommunikasjonsretning.INTERN
            else -> throw RuntimeException("Ukjent journalposttype: " + journalpost.journalposttype)
        }

    private fun getDato(journalpost: Journalpost): LocalDateTime =
        when (journalpost.journalposttype) {
            Journalposttype.I -> journalpost.getRelevantDatoForType(Datotype.DATO_REGISTRERT)
            Journalposttype.U ->
                listOfNotNull(
                    journalpost.getRelevantDatoForType(Datotype.DATO_EKSPEDERT),
                    journalpost.getRelevantDatoForType(Datotype.DATO_SENDT_PRINT),
                    journalpost.getRelevantDatoForType(Datotype.DATO_JOURNALFOERT),
                ).firstOrNull()

            Journalposttype.N -> journalpost.getRelevantDatoForType(Datotype.DATO_JOURNALFOERT)
            else -> LocalDateTime.now()
        } ?: LocalDateTime.now()

    private fun getLestDato(journalpost: Journalpost): LocalDateTime? =
        if (journalpost.journalposttype == Journalposttype.U) {
            journalpost.getRelevantDatoForType(Datotype.DATO_LEST)
        } else {
            null
        }

    private fun Journalpost.getRelevantDatoForType(type: Datotype): LocalDateTime? =
        this.relevanteDatoer
            .orEmpty()
            .filterNotNull()
            .find { it.datotype == type }
            ?.dato

    private fun getVedlegg(journalpost: Journalpost): List<Dokument> =
        getElektroniskeVedlegg(journalpost).plus(getLogiskeVedlegg(journalpost))

    private fun getElektroniskeVedlegg(journalpost: Journalpost): List<Dokument> =
        journalpost.dokumenter
            .orEmpty()
            .subList(SafServiceImpl.VEDLEGG_START_INDEX, journalpost.dokumenter?.size ?: 0)
            .filterNotNull()
            .mapNotNull { dok -> fraSafDokumentInfo(dok) }

    private fun getHoveddokumentet(journalpost: Journalpost): DokumentInfo =
        journalpost.dokumenter?.get(0) ?: throw RuntimeException("Fant sak uten hoveddokument!")

    private fun getLogiskeVedlegg(journalpost: Journalpost): List<Dokument> =
        getHoveddokumentet(journalpost)
            .logiskeVedlegg
            .filterNotNull()
            .map { logiskVedlegg -> fraSafLogiskVedlegg(logiskVedlegg) }

    private fun fraSafDokumentInfo(dokumentInfo: DokumentInfo): Dokument? {
        val variantFormat = getVariantformat(dokumentInfo) ?: return null

        return Dokument().apply {
            tittel = dokumentInfo.tittel
            dokumentreferanse = dokumentInfo.dokumentInfoId
            isKanVises = true
            isLogiskDokument = false
            variantformat = variantFormat
            skjerming = getSkjerming(dokumentInfo)?.toString()
            dokumentStatus = getDokumentStatus(dokumentInfo)
        }
    }

    private fun getDokumentStatus(dokumentInfo: DokumentInfo): Dokument.DokumentStatus =
        when (dokumentInfo.dokumentstatus) {
            Dokumentstatus.UNDER_REDIGERING -> Dokument.DokumentStatus.UNDER_REDIGERING
            Dokumentstatus.FERDIGSTILT -> Dokument.DokumentStatus.FERDIGSTILT
            Dokumentstatus.KASSERT -> Dokument.DokumentStatus.KASSERT
            Dokumentstatus.AVBRUTT -> Dokument.DokumentStatus.AVBRUTT
            else -> Dokument.DokumentStatus.FERDIGSTILT
        }

    private fun getVariantformat(dokumentInfo: DokumentInfo): Dokument.Variantformat? =
        when (getVariant(dokumentInfo)?.variantformat) {
            Variantformat.ARKIV -> Dokument.Variantformat.ARKIV
            Variantformat.SLADDET -> Dokument.Variantformat.SLADDET
            Variantformat.FULLVERSJON -> Dokument.Variantformat.FULLVERSJON
            Variantformat.PRODUKSJON -> Dokument.Variantformat.PRODUKSJON
            Variantformat.PRODUKSJON_DLF -> Dokument.Variantformat.PRODUKSJON_DLF
            null -> {
                log.warn("SAF dokument tilfredstiller ikke krav til variantFormat (SLADDET,ARKIV)")
                null
            }
            else -> throw RuntimeException(
                "Ugyldig tekst for mapping til variantformat. Tekst: ${
                    getVariant(
                        dokumentInfo,
                    )?.variantformat
                }",
            )
        }

    private fun getSkjerming(dokumentInfo: DokumentInfo): SkjermingType? = getVariant(dokumentInfo)?.skjerming

    private fun getVariant(dokumentInfo: DokumentInfo): Dokumentvariant? =
        dokumentInfo.dokumentvarianter.let {
            it.find { variant -> variant?.variantformat == Variantformat.SLADDET }
                ?: it.find { variant -> variant?.variantformat == Variantformat.ARKIV }
        }

    private fun fraSafLogiskVedlegg(logiskVedlegg: LogiskVedlegg): Dokument =
        Dokument().apply {
            tittel = logiskVedlegg.tittel
            dokumentreferanse = null
            isKanVises = true
            isLogiskDokument = true
        }
}
