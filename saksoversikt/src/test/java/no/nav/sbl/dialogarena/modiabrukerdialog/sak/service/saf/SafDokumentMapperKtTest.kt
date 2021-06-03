package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf

import io.mockk.every
import io.mockk.mockkStatic
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument.Variantformat.ARKIV
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument.Variantformat.SLADDET
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Entitet.*
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Kommunikasjonsretning
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val datoOpprettet = LocalDateTime.parse("2019-02-04T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
private const val temanavn = "Arbeidsavklaringspenger"
private const val tema = "AAP"
private const val journalStatus = "JOURNALFOERT"
private const val journalpostType = JOURNALPOSTTYPE_INN
private const val journalpostTittel = "Journalpost Tittel"
private const val journalpostId = "456"
private const val avsenderMottakerNavn = "Aremark"
private const val fagsakSystem = "FS22"
private const val fagsakId = "987"
private const val arkivsaksystem = "GSAK"
private const val arkivsaknummer = "789"
private const val datotype = DATOTYPE_REGISTRERT
private val relevantdato = LocalDateTime.parse("2011-05-07T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
private const val logiskVedleggtittel = "Logisk Vedlegg tittel"
private const val variantformat = "ARKIV"
private const val POL = "POL"
private const val dokumentinfoid = "123"
private const val hovedDokumentTittel = "Dokument Tittel"
private const val vedleggTittel = "Vedleggtittel"
private const val aremarkFNR = "10108000398"

internal class SafDokumentMapperKtTest {

    @Test
    fun `dokumentMetadata mapper hele objektet`() {
        val journalpost = lagJournalpost()

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(Kommunikasjonsretning.INN, dokumentMetadata.retning)
        assertEquals(relevantdato, dokumentMetadata.dato)
        assertEquals(avsenderMottakerNavn, dokumentMetadata.navn)
        assertEquals(journalpostId, dokumentMetadata.journalpostId)

        assertEquals(hovedDokumentTittel, dokumentMetadata.hoveddokument.tittel)
        assertEquals(dokumentinfoid, dokumentMetadata.hoveddokument.dokumentreferanse)
        assertEquals(true, dokumentMetadata.hoveddokument.isKanVises)
        assertEquals(false, dokumentMetadata.hoveddokument.isLogiskDokument)
        assertEquals(ARKIV, dokumentMetadata.hoveddokument.variantformat)

        assertEquals(2, dokumentMetadata.vedlegg.size)
        assertEquals(vedleggTittel, dokumentMetadata.vedlegg[0].tittel)
        assertEquals(dokumentinfoid, dokumentMetadata.vedlegg[0].dokumentreferanse)
        assertEquals(true, dokumentMetadata.vedlegg[0].isKanVises)
        assertEquals(false, dokumentMetadata.vedlegg[0].isLogiskDokument)
        assertEquals(ARKIV, dokumentMetadata.vedlegg[0].variantformat)

        assertEquals(NAV, dokumentMetadata.mottaker)
        assertEquals(SLUTTBRUKER, dokumentMetadata.avsender)
        assertEquals(arkivsaknummer, dokumentMetadata.tilhorendeSakid)
        assertEquals(fagsakId, dokumentMetadata.tilhorendeFagsakId)
        assert(dokumentMetadata.baksystem.contains(Baksystem.SAF))
        assertEquals(tema, dokumentMetadata.temakode)
        assertEquals(temanavn, dokumentMetadata.temakodeVisning)
    }

    @Test
    fun `Sak uten hoveddokument kaster runtimeException`() {
        val journalpost = lagJournalpost().copy(
            dokumenter = emptyList()
        )

        assertThrows(RuntimeException::class.java) {
            DokumentMetadata().fraSafJournalpost(journalpost)
        }
    }

    @Test
    fun `Retning I mappes korrekt`() {
        val journalpost = lagJournalpost().copy(journalposttype = JOURNALPOSTTYPE_INN)

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(Kommunikasjonsretning.INN, dokumentMetadata.retning)
    }

    @Test
    fun `Retning U mappes korrekt`() {
        val journalpost = lagJournalpost().copy(journalposttype = JOURNALPOSTTYPE_UT)

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(Kommunikasjonsretning.UT, dokumentMetadata.retning)
    }

    @Test
    fun `Retning N mappes korrekt`() {
        val journalpost = lagJournalpost().copy(journalposttype = JOURNALPOSTTYPE_INTERN)

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(Kommunikasjonsretning.INTERN, dokumentMetadata.retning)
    }

    @Test
    fun `Sett variantformat til SLADDET om SLADDET eksisterer`() {
        val journalpost = lagJournalpost().copy(
            dokumenter = listOf(
                lagHoveddokument().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, ARKIV.name, null),
                        Dokumentvariant(true, SLADDET.name, null),
                        Dokumentvariant(true, "ANNET_FORMAT", null)
                    )
                ),
                lagVedlegg().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, ARKIV.name, null),
                        Dokumentvariant(true, SLADDET.name, null),
                        Dokumentvariant(true, "ANNET_FORMAT", null)
                    )
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(SLADDET, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(SLADDET, dokumentMetadata.vedlegg[0].variantformat)
        assertEquals(2, dokumentMetadata.vedlegg.size)
    }

    @Test
    fun `Sett variantformat til ARKIV om ingen SLADDET eksisterer`() {
        val journalpost = lagJournalpost().copy(
            dokumenter = listOf(
                lagHoveddokument().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, "ANNET_FORMAT", null),
                        Dokumentvariant(true, ARKIV.name, null),
                        Dokumentvariant(true, "ANNET_FORMAT", null)
                    )
                ),
                lagVedlegg().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, "ANNET_FORMAT", null),
                        Dokumentvariant(true, ARKIV.name, null),
                        Dokumentvariant(true, "ANNET_FORMAT", null)
                    )
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(ARKIV, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(ARKIV, dokumentMetadata.vedlegg[0].variantformat)
        assertEquals(2, dokumentMetadata.vedlegg.size)
    }

    @Test
    fun `Kast feil om vi mangler både ARKIV og SLADDET i variantformater`() {
        val journalpost = lagJournalpost().copy(
            dokumenter = listOf(
                lagHoveddokument().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, "ANNET_FORMAT", null),
                        Dokumentvariant(true, "ANNET_FORMAT", null)
                    )
                ),
                lagVedlegg().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, "ANNET_FORMAT", null),
                        Dokumentvariant(true, "ANNET_FORMAT", null)
                    )
                )
            )
        )

        assertThrows(RuntimeException::class.java) {
            DokumentMetadata().fraSafJournalpost(journalpost)
        }
    }

    @Test
    fun `Hvis dokumentvarient er kun arkiv skjermet vis arkiv skjermet`() {
        val journalpost = lagJournalpost().copy(
            dokumenter = listOf(
                lagHoveddokument().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, ARKIV.name, POL)
                    )
                ),
                lagVedlegg().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, ARKIV.name, POL)
                    )
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(ARKIV, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(POL, dokumentMetadata.hoveddokument.skjerming)
        assertEquals(ARKIV, dokumentMetadata.vedlegg[0].variantformat)
        assertEquals(POL, dokumentMetadata.vedlegg[0].skjerming)
    }

    @Test
    fun `Hvis arkiv skjermet men eksiterer sladdet ikke skjermet, vis sladdet ikke skjermet`() {
        val journalpost = lagJournalpost().copy(
            dokumenter = listOf(
                lagHoveddokument().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, ARKIV.name, POL),
                        Dokumentvariant(true, SLADDET.name, null)
                    )
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(SLADDET, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(null, dokumentMetadata.hoveddokument.skjerming)
    }

    @Test
    fun `Hvis arkiv skjermet og eksisterer sladdet som er skjermet vis sladdet skjermet`() {
        val journalpost = lagJournalpost().copy(
            dokumenter = listOf(
                lagHoveddokument().copy(
                    dokumentvarianter = listOf(
                        Dokumentvariant(true, SLADDET.name, POL),
                        Dokumentvariant(true, ARKIV.name, POL)
                    )
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(SLADDET, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(POL, dokumentMetadata.hoveddokument.skjerming)
    }

    @Test
    fun `Bruker som avsender mappes korrekt`() {
        val navn = "Aremark"
        val journalpost = lagJournalpost().copy(
            avsenderMottaker = AvsenderMottaker(true, navn),
            journalposttype = JOURNALPOSTTYPE_INN
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(SLUTTBRUKER, dokumentMetadata.avsender)
        assertEquals(NAV, dokumentMetadata.mottaker)
        assertEquals(navn, dokumentMetadata.navn)
    }

    @Test
    fun `Bruker som mottaker mappes korrekt`() {
        val navn = "Aremark"
        val journalpost = lagJournalpost().copy(
            avsenderMottaker = AvsenderMottaker(true, navn),
            journalposttype = JOURNALPOSTTYPE_UT
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(NAV, dokumentMetadata.avsender)
        assertEquals(SLUTTBRUKER, dokumentMetadata.mottaker)
        assertEquals(navn, dokumentMetadata.navn)
    }

    @Test
    fun `Intern retning mappes korrekt`() {
        val journalpost = lagJournalpost().copy(
            avsenderMottaker = AvsenderMottaker(false, "Aremark"),
            journalposttype = JOURNALPOSTTYPE_INTERN
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(NAV, dokumentMetadata.avsender)
        assertEquals(NAV, dokumentMetadata.mottaker)
    }

    @Test
    fun `Ekstern som mottaker mappes korrekt`() {
        val eksternNavn = "Aremark sin lege"
        val journalpost = lagJournalpost().copy(
            bruker = lagJournalpost().bruker?.copy(id = "10108000398"),
            avsenderMottaker = AvsenderMottaker(false, eksternNavn),
            journalposttype = JOURNALPOSTTYPE_UT
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(NAV, dokumentMetadata.avsender)
        assertEquals(EKSTERN_PART, dokumentMetadata.mottaker)
        assertEquals(eksternNavn, dokumentMetadata.navn)
    }

    @Test
    fun `Ekstern som avsender mappes korrekt`() {
        val eksternNavn = "Aremark sin lege"
        val journalpost = lagJournalpost().copy(
            bruker = lagJournalpost().bruker?.copy(id = "10108000398"),
            avsenderMottaker = AvsenderMottaker(false, eksternNavn),
            journalposttype = JOURNALPOSTTYPE_INN
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(NAV, dokumentMetadata.mottaker)
        assertEquals(EKSTERN_PART, dokumentMetadata.avsender)
        assertEquals(eksternNavn, dokumentMetadata.navn)
    }

    @Test
    fun `Navn blir ukjent når avsenderMottaker er null`() {
        val journalpost = lagJournalpost().copy(
            avsenderMottaker = null
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals("ukjent", dokumentMetadata.navn)
    }

    @Test
    fun `Kaster feil ved ukjent journalposttype`() {
        val journalpost = lagJournalpost().copy(journalposttype = "UGYLDIGTYPE")

        assertThrows(RuntimeException::class.java) {
            DokumentMetadata().fraSafJournalpost(journalpost)
        }
    }

    @Test
    fun `Setter nådato om relevant dato for type ikke eksisterer`() {
        val nowDate = mockLocalDateTimeNow()

        val journalpost = lagJournalpost().copy(
            journalposttype = JOURNALPOSTTYPE_INN,
            relevanteDatoer = emptyList()
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(nowDate, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker registrert dato for Inngående`() {
        val registrertDato = LocalDateTime.parse("2018-11-11T13:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost = lagJournalpost().copy(
            relevanteDatoer = listOf(
                RelevantDato(
                    datotype = DATOTYPE_REGISTRERT,
                    dato = registrertDato
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(registrertDato, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker ekspedert dato primært om ikke null for Utgående`() {
        val ekspedertDato = LocalDateTime.parse("2018-11-11T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
        val sendtPrintDato = LocalDateTime.parse("2017-10-10T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
        val journalFoertDato = LocalDateTime.parse("2016-01-01T13:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost = lagJournalpost().copy(
            journalposttype = JOURNALPOSTTYPE_UT,
            relevanteDatoer = listOf(
                RelevantDato(
                    datotype = DATOTYPE_EKSPEDERT,
                    dato = ekspedertDato
                ),
                RelevantDato(
                    datotype = DATOTYPE_SENDT_PRINT,
                    dato = sendtPrintDato
                ),
                RelevantDato(
                    datotype = DATOTYPE_JOURNALFOERT,
                    dato = journalFoertDato
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(ekspedertDato, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker sendt print dato sekundært om ekspedert ikke eksisterer for Utgående`() {
        val sendtPrintDato = LocalDateTime.parse("2017-10-10T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
        val journalFoertDato = LocalDateTime.parse("2016-01-01T13:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost = lagJournalpost().copy(
            journalposttype = JOURNALPOSTTYPE_UT,
            relevanteDatoer = listOf(
                RelevantDato(
                    datotype = DATOTYPE_SENDT_PRINT,
                    dato = sendtPrintDato
                ),
                RelevantDato(
                    datotype = DATOTYPE_JOURNALFOERT,
                    dato = journalFoertDato
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(sendtPrintDato, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker journalfoert dato tertiert om ekspedert og sendt print ikke eksisterer for Utgående`() {
        val journalFoertDato = LocalDateTime.parse("2016-01-01T13:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost = lagJournalpost().copy(
            journalposttype = JOURNALPOSTTYPE_UT,
            relevanteDatoer = listOf(
                RelevantDato(
                    datotype = DATOTYPE_JOURNALFOERT,
                    dato = journalFoertDato
                )
            )
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(journalFoertDato, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker dokument dato for Intern`() {
        val journalFoertDato = LocalDateTime.parse("2018-11-11T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
        val dokumentDato = LocalDateTime.parse("2017-10-10T09:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost = lagJournalpost().copy(
            relevanteDatoer = listOf(
                RelevantDato(
                    datotype = DATOTYPE_JOURNALFOERT,
                    dato = journalFoertDato
                ),
                RelevantDato(
                    datotype = DATOTYPE_DOKUMENT,
                    dato = dokumentDato
                )
            ),
            journalposttype = JOURNALPOSTTYPE_INTERN
        )

        val dokumentMetadata = DokumentMetadata().fraSafJournalpost(journalpost)

        assertEquals(dokumentDato, dokumentMetadata.dato)
    }
}

private fun lagJournalpost(): Journalpost {
    val bruker = Bruker(id = aremarkFNR, type = "FNR")
    val dokumenter: List<DokumentInfo> = listOf(lagHoveddokument(), lagVedlegg())
    val relevanteDatoer = listOf(RelevantDato(dato = relevantdato, datotype = datotype))
    val sak = lagSak()

    return Journalpost(
        avsenderMottaker = lagAvsenderMottaker(),
        bruker = bruker,
        dokumenter = dokumenter,
        journalpostId = journalpostId,
        tittel = journalpostTittel,
        datoOpprettet = datoOpprettet,
        journalposttype = journalpostType,
        journalstatus = journalStatus,
        relevanteDatoer = relevanteDatoer,
        tema = tema,
        temanavn = temanavn,
        sak = sak
    )
}

private fun lagSak(): Sak {
    return Sak(
        arkivsaksnummer = arkivsaknummer,
        arkivsaksystem = arkivsaksystem,
        fagsakId = fagsakId,
        fagsaksystem = fagsakSystem
    )
}

private fun lagAvsenderMottaker() = AvsenderMottaker(true, avsenderMottakerNavn)
private fun lagVedlegg(): DokumentInfo = lagDokumentInfo(vedleggTittel)
private fun lagHoveddokument(): DokumentInfo = lagDokumentInfo(hovedDokumentTittel)

private fun lagDokumentInfo(tittel: String): DokumentInfo {
    return DokumentInfo(
        tittel = tittel,
        dokumentInfoId = dokumentinfoid,
        dokumentvarianter = listOf(lagDokumentVariant()),
        logiskeVedlegg = listOf(LogiskVedlegg(logiskVedleggtittel))
    )
}

private fun lagDokumentVariant(): Dokumentvariant =
    Dokumentvariant(true, variantformat, null)

private fun mockLocalDateTimeNow(): LocalDateTime {
    mockkStatic(LocalDateTime::class)
    val nowDate = LocalDateTime.now()
    every {
        LocalDateTime.now()
    } returns nowDate
    return nowDate
}
