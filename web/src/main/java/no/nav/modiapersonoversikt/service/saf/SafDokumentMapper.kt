package no.nav.modiapersonoversikt.service.saf

import no.nav.modiapersonoversikt.consumer.saf.generated.HentBrukersDokumenter
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*
import java.time.LocalDateTime

object SafDokumentMapper {
    fun fraSafJournalpost(journalpost: HentBrukersDokumenter.Journalpost): DokumentMetadata? {
        val hovedDokument = fraSafDokumentInfo(getHoveddokumentet(journalpost)) ?: return null
        return DokumentMetadata().apply {
            retning = getRetning(journalpost)
            dato = getDato(journalpost)
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

    private fun getAvsender(journalpost: HentBrukersDokumenter.Journalpost): Entitet =
        when (journalpost.journalposttype) {
            SafServiceImpl.JOURNALPOSTTYPE_INTERN -> Entitet.NAV
            SafServiceImpl.JOURNALPOSTTYPE_INN -> if (sluttbrukerErMottakerEllerAvsender(journalpost)) Entitet.SLUTTBRUKER else Entitet.EKSTERN_PART
            SafServiceImpl.JOURNALPOSTTYPE_UT -> Entitet.NAV
            else -> Entitet.UKJENT
        }

    private fun getMottaker(journalpost: HentBrukersDokumenter.Journalpost): Entitet =
        when (journalpost.journalposttype) {
            SafServiceImpl.JOURNALPOSTTYPE_INTERN -> Entitet.NAV
            SafServiceImpl.JOURNALPOSTTYPE_INN -> Entitet.NAV
            SafServiceImpl.JOURNALPOSTTYPE_UT -> if (sluttbrukerErMottakerEllerAvsender(journalpost)) Entitet.SLUTTBRUKER else Entitet.EKSTERN_PART
            else -> Entitet.UKJENT
        }

    private fun sluttbrukerErMottakerEllerAvsender(journalpost: HentBrukersDokumenter.Journalpost): Boolean =
        journalpost.avsenderMottaker?.erLikBruker ?: false

    private fun getRetning(journalpost: HentBrukersDokumenter.Journalpost): Kommunikasjonsretning =
        when (journalpost.journalposttype) {
            HentBrukersDokumenter.Journalposttype.I -> Kommunikasjonsretning.INN
            HentBrukersDokumenter.Journalposttype.U -> Kommunikasjonsretning.UT
            HentBrukersDokumenter.Journalposttype.N -> Kommunikasjonsretning.INTERN
            else -> throw RuntimeException("Ukjent journalposttype: " + journalpost.journalposttype)
        }

    private fun getDato(journalpost: HentBrukersDokumenter.Journalpost): LocalDateTime =
        when (journalpost.journalposttype) {
            HentBrukersDokumenter.Journalposttype.I -> journalpost.getRelevantDatoForType(HentBrukersDokumenter.Datotype.DATO_REGISTRERT)
            HentBrukersDokumenter.Journalposttype.U -> listOfNotNull(
                journalpost.getRelevantDatoForType(HentBrukersDokumenter.Datotype.DATO_EKSPEDERT),
                journalpost.getRelevantDatoForType(HentBrukersDokumenter.Datotype.DATO_SENDT_PRINT),
                journalpost.getRelevantDatoForType(HentBrukersDokumenter.Datotype.DATO_JOURNALFOERT)
            ).firstOrNull()

            HentBrukersDokumenter.Journalposttype.N -> journalpost.getRelevantDatoForType(HentBrukersDokumenter.Datotype.DATO_JOURNALFOERT)
            else -> LocalDateTime.now()
        } ?: LocalDateTime.now()

    private fun HentBrukersDokumenter.Journalpost.getRelevantDatoForType(type: HentBrukersDokumenter.Datotype): LocalDateTime? {
        return this.relevanteDatoer
            .orEmpty()
            .filterNotNull()
            .find { it.datotype == type }
            ?.dato
            ?.value
    }

    private fun getVedlegg(journalpost: HentBrukersDokumenter.Journalpost): List<Dokument> =
        getElektroniskeVedlegg(journalpost).plus(getLogiskeVedlegg(journalpost))

    private fun getElektroniskeVedlegg(journalpost: HentBrukersDokumenter.Journalpost): List<Dokument> =
        journalpost.dokumenter
            .orEmpty()
            .subList(SafServiceImpl.VEDLEGG_START_INDEX, journalpost.dokumenter?.size ?: 0)
            .filterNotNull()
            .mapNotNull { dok -> fraSafDokumentInfo(dok) }

    private fun getHoveddokumentet(journalpost: HentBrukersDokumenter.Journalpost): HentBrukersDokumenter.DokumentInfo =
        journalpost.dokumenter?.get(0) ?: throw RuntimeException("Fant sak uten hoveddokument!")

    private fun getLogiskeVedlegg(journalpost: HentBrukersDokumenter.Journalpost): List<Dokument> =
        getHoveddokumentet(journalpost).logiskeVedlegg
            .filterNotNull()
            .map { logiskVedlegg -> fraSafLogiskVedlegg(logiskVedlegg) }

    private fun fraSafDokumentInfo(dokumentInfo: HentBrukersDokumenter.DokumentInfo): Dokument? {
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

    private fun getDokumentStatus(dokumentInfo: HentBrukersDokumenter.DokumentInfo): Dokument.DokumentStatus =
        when (dokumentInfo.dokumentstatus) {
            HentBrukersDokumenter.Dokumentstatus.UNDER_REDIGERING -> Dokument.DokumentStatus.UNDER_REDIGERING
            HentBrukersDokumenter.Dokumentstatus.FERDIGSTILT -> Dokument.DokumentStatus.FERDIGSTILT
            HentBrukersDokumenter.Dokumentstatus.KASSERT -> Dokument.DokumentStatus.KASSERT
            HentBrukersDokumenter.Dokumentstatus.AVBRUTT -> Dokument.DokumentStatus.AVBRUTT
            else -> Dokument.DokumentStatus.FERDIGSTILT
        }

    private fun getVariantformat(dokumentInfo: HentBrukersDokumenter.DokumentInfo): Dokument.Variantformat? =
        when (getVariant(dokumentInfo)?.variantformat) {
            HentBrukersDokumenter.Variantformat.ARKIV -> Dokument.Variantformat.ARKIV
            HentBrukersDokumenter.Variantformat.SLADDET -> Dokument.Variantformat.SLADDET
            HentBrukersDokumenter.Variantformat.FULLVERSJON -> Dokument.Variantformat.FULLVERSJON
            HentBrukersDokumenter.Variantformat.PRODUKSJON -> Dokument.Variantformat.PRODUKSJON
            HentBrukersDokumenter.Variantformat.PRODUKSJON_DLF -> Dokument.Variantformat.PRODUKSJON_DLF
            null -> null
            else -> throw RuntimeException(
                "Ugyldig tekst for mapping til variantformat. Tekst: ${
                    getVariant(
                        dokumentInfo
                    )?.variantformat
                }"
            )
        }

    private fun getSkjerming(dokumentInfo: HentBrukersDokumenter.DokumentInfo): HentBrukersDokumenter.SkjermingType? =
        getVariant(dokumentInfo)?.skjerming

    private fun getVariant(dokumentInfo: HentBrukersDokumenter.DokumentInfo): HentBrukersDokumenter.Dokumentvariant? =
        dokumentInfo.dokumentvarianter.let {
            it.find { variant -> variant?.variantformat == HentBrukersDokumenter.Variantformat.SLADDET }
                ?: it.find { variant -> variant?.variantformat == HentBrukersDokumenter.Variantformat.ARKIV }
        }

    private fun fraSafLogiskVedlegg(logiskVedlegg: HentBrukersDokumenter.LogiskVedlegg): Dokument {
        return Dokument().apply {
            tittel = logiskVedlegg.tittel
            dokumentreferanse = null
            isKanVises = true
            isLogiskDokument = true
        }
    }
}
