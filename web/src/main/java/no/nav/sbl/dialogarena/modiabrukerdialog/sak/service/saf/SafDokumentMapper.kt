package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.*
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument.Variantformat
import java.time.LocalDateTime
import java.time.LocalDateTime.now

const val DATOTYPE_REGISTRERT = "DATO_REGISTRERT"
const val DATOTYPE_JOURNALFOERT = "DATO_JOURNALFOERT"
const val DATOTYPE_EKSPEDERT = "DATO_EKSPEDERT"
const val DATOTYPE_SENDT_PRINT = "DATO_SENDT_PRINT"

const val JOURNALPOSTTYPE_INN = "I"
const val JOURNALPOSTTYPE_UT = "U"
const val JOURNALPOSTTYPE_INTERN = "N"

const val VEDLEGG_START_INDEX = 1

fun DokumentMetadata.fraSafJournalpost(journalpost: Journalpost): DokumentMetadata {
    retning = getRetning(journalpost)
    dato = getDato(journalpost)
    navn = getNavn(journalpost)
    journalpostId = journalpost.journalpostId
    hoveddokument = Dokument().fraSafDokumentInfo(getHoveddokumentet(journalpost))
    vedlegg = getVedlegg(journalpost)
    avsender = getAvsender(journalpost)
    mottaker = getMottaker(journalpost)
    tilhorendeSakid = journalpost.sak?.arkivsaksnummer
    tilhorendeFagsakId = journalpost.sak?.fagsakId
    baksystem = baksystem.plus(Baksystem.SAF)
    temakode = journalpost.tema
    temakodeVisning = journalpost.temanavn

    return this
}

private fun getRetning(journalpost: Journalpost): Kommunikasjonsretning? =
    when (journalpost.journalposttype) {
        JOURNALPOSTTYPE_INN -> Kommunikasjonsretning.INN
        JOURNALPOSTTYPE_UT -> Kommunikasjonsretning.UT
        JOURNALPOSTTYPE_INTERN -> Kommunikasjonsretning.INTERN
        else -> throw RuntimeException("Ukjent journalposttype: " + journalpost.journalposttype)
    }

private fun getDato(journalpost: Journalpost): LocalDateTime? =
    when (journalpost.journalposttype) {
        JOURNALPOSTTYPE_INN -> getRelevantDatoForType(DATOTYPE_REGISTRERT, journalpost)
        JOURNALPOSTTYPE_UT -> getDatoSendt(journalpost)
        /* Her kan man ikke sortere notat på dato fordi dataene finnes ikke */
        JOURNALPOSTTYPE_INTERN -> getRelevantDatoForType(DATOTYPE_JOURNALFOERT, journalpost)
        else -> now()
    } ?: now()

private fun getDatoSendt(journalpost: Journalpost): LocalDateTime? =
    listOfNotNull(
        getRelevantDatoForType(DATOTYPE_EKSPEDERT, journalpost),
        getRelevantDatoForType(DATOTYPE_SENDT_PRINT, journalpost),
        getRelevantDatoForType(DATOTYPE_JOURNALFOERT, journalpost)
    ).firstOrNull()

private fun getRelevantDatoForType(datotype: String, journalpost: Journalpost): LocalDateTime? =
    journalpost.relevanteDatoer.orEmpty()
        .filter { dato -> dato.datotype == datotype }
        .map { relevantDato -> relevantDato.dato }
        .firstOrNull()

private fun getNavn(journalpost: Journalpost) =
    journalpost.avsenderMottaker?.navn ?: "ukjent"

private fun getHoveddokumentet(journalpost: Journalpost): DokumentInfo =
    journalpost.dokumenter?.get(0) ?: throw RuntimeException("Fant sak uten hoveddokument!")

private fun Dokument.fraSafDokumentInfo(dokumentInfo: DokumentInfo): Dokument {
    tittel = dokumentInfo.tittel
    dokumentreferanse = dokumentInfo.dokumentInfoId
    isKanVises = true
    isLogiskDokument = false
    variantformat = getVariantformat(dokumentInfo)
    skjerming = getSkjerming(dokumentInfo)

    return this
}

private fun getVariantformat(dokumentInfo: DokumentInfo): Variantformat =
    when (getVariant(dokumentInfo).variantformat) {
        Variantformat.ARKIV.name -> Variantformat.ARKIV
        Variantformat.SLADDET.name -> Variantformat.SLADDET
        Variantformat.FULLVERSJON.name -> Variantformat.FULLVERSJON
        Variantformat.PRODUKSJON.name -> Variantformat.PRODUKSJON
        Variantformat.PRODUKSJON_DLF.name -> Variantformat.PRODUKSJON_DLF
        else -> throw RuntimeException("Ugyldig tekst for mapping til variantformat. Tekst: ${getVariant(dokumentInfo).variantformat}")
    }

private fun getSkjerming(dokumentInfo: DokumentInfo): String? =
    getVariant(dokumentInfo).skjerming

private fun getVariant(dokumentInfo: DokumentInfo): Dokumentvariant =
    dokumentInfo.dokumentvarianter.let {
        it.find { variant -> variant.variantformat == Variantformat.SLADDET.name }
            ?: it.find { variant -> variant.variantformat == Variantformat.ARKIV.name }
            ?: throw RuntimeException("Dokument med id ${dokumentInfo.dokumentInfoId} mangler både ARKIV og SLADDET variantformat")
    }

private fun getVedlegg(journalpost: Journalpost): List<Dokument> =
    getElektroniskeVedlegg(journalpost).plus(getLogiskeVedlegg(journalpost))

private fun getElektroniskeVedlegg(journalpost: Journalpost): List<Dokument> =
    journalpost.dokumenter
        .orEmpty()
        .subList(VEDLEGG_START_INDEX, journalpost.dokumenter?.size ?: 0)
        .map { dok -> Dokument().fraSafDokumentInfo(dok) }

private fun getLogiskeVedlegg(journalpost: Journalpost): List<Dokument> =
    getHoveddokumentet(journalpost).logiskeVedlegg.map { logiskVedlegg -> Dokument().fraSafLogiskVedlegg(logiskVedlegg) }

private fun getAvsender(journalpost: Journalpost): Entitet = getAvsenderMottaker(journalpost).first
private fun getMottaker(journalpost: Journalpost): Entitet = getAvsenderMottaker(journalpost).second
private fun getAvsenderMottaker(journalpost: Journalpost): Pair<Entitet, Entitet> {
    if (journalpost.journalposttype == JOURNALPOSTTYPE_INTERN) {
        return Pair(Entitet.NAV, Entitet.NAV)
    } else if (sluttbrukerErMottakerEllerAvsender(journalpost)) {
        if (journalpost.journalposttype == JOURNALPOSTTYPE_INN) return Pair(Entitet.SLUTTBRUKER, Entitet.NAV)
        if (journalpost.journalposttype == JOURNALPOSTTYPE_UT) return Pair(Entitet.NAV, Entitet.SLUTTBRUKER)
    } else {
        if (journalpost.journalposttype == JOURNALPOSTTYPE_INN) return Pair(Entitet.EKSTERN_PART, Entitet.NAV)
        if (journalpost.journalposttype == JOURNALPOSTTYPE_UT) return Pair(Entitet.NAV, Entitet.EKSTERN_PART)
    }

    return Pair(Entitet.UKJENT, Entitet.UKJENT)
}

private fun sluttbrukerErMottakerEllerAvsender(journalpost: Journalpost): Boolean =
    journalpost.avsenderMottaker?.erLikBruker ?: false

private fun Dokument.fraSafLogiskVedlegg(logiskVedlegg: LogiskVedlegg): Dokument {
    tittel = logiskVedlegg.tittel
    dokumentreferanse = null
    isKanVises = true
    isLogiskDokument = true

    return this
}
