package no.nav.modiapersonoversikt.service.saf

import io.mockk.every
import io.mockk.mockkStatic
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.Entitet.*
import no.nav.modiapersonoversikt.consumer.saf.generated.enums.*
import no.nav.modiapersonoversikt.consumer.saf.generated.hentbrukersdokumenter.*
import no.nav.modiapersonoversikt.service.saf.SafDokumentMapper.fraSafJournalpost
import no.nav.modiapersonoversikt.service.saf.SafServiceImpl.Companion.JOURNALPOSTTYPE_INN
import no.nav.modiapersonoversikt.service.saf.SafServiceImpl.Companion.JOURNALPOSTTYPE_INTERN
import no.nav.modiapersonoversikt.service.saf.SafServiceImpl.Companion.JOURNALPOSTTYPE_UT
import no.nav.modiapersonoversikt.service.saf.domain.Dokument
import no.nav.modiapersonoversikt.service.saf.domain.Dokument.Variantformat.ARKIV
import no.nav.modiapersonoversikt.service.saf.domain.Dokument.Variantformat.SLADDET
import no.nav.modiapersonoversikt.service.saf.domain.Kommunikasjonsretning
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val datoOpprettet = LocalDateTime.parse("2019-02-04T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
private val temanavn = "Arbeidsavklaringspenger"
private val tema = Tema.AAP
private val journalStatus = Journalstatus.JOURNALFOERT
private val journalpostType = JOURNALPOSTTYPE_INN
private val journalpostTittel = "Journalpost Tittel"
private val journalpostId = "456"
private val avsenderMottakerNavn = "Aremark"
private val fagsakSystem = "FS22"
private val fagsakId = "987"
private val arkivsaknummer = "789"
private val datotype = Datotype.DATO_REGISTRERT
private val relevantdato = LocalDateTime.parse("2011-05-07T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
private val logiskVedleggtittel = "Logisk Vedlegg tittel"
private val variantformat = Variantformat.ARKIV
private val POL = SkjermingType.POL
private val dokumentinfoid = "123"
private val hovedDokumentTittel = "Dokument Tittel"
private val vedleggTittel = "Vedleggtittel"
private val aremarkFNR = "10108000398"
private val dokumentStatus = Dokumentstatus.FERDIGSTILT

internal class SafDokumentMapperTest {
    @Test
    fun `dokumentMetadata mapper hele objektet`() {
        val journalpost = lagJournalpost()

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(Kommunikasjonsretning.INN, dokumentMetadata.retning)
        assertEquals(relevantdato, dokumentMetadata.dato)
        assertEquals(avsenderMottakerNavn, dokumentMetadata.navn)
        assertEquals(journalpostId, dokumentMetadata.journalpostId)

        assertEquals(hovedDokumentTittel, dokumentMetadata.hoveddokument.tittel)
        assertEquals(dokumentinfoid, dokumentMetadata.hoveddokument.dokumentreferanse)
        assertEquals(true, dokumentMetadata.hoveddokument.isKanVises)
        assertEquals(false, dokumentMetadata.hoveddokument.isLogiskDokument)
        assertEquals(ARKIV, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(Dokument.DokumentStatus.FERDIGSTILT, dokumentMetadata.hoveddokument.dokumentStatus)

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
        assertEquals(tema.name, dokumentMetadata.temakode)
        assertEquals(temanavn, dokumentMetadata.temakodeVisning)
    }

    @Test
    fun `Sak uten hoveddokument kaster runtimeException`() {
        val journalpost =
            lagJournalpost().copy(
                dokumenter = emptyList(),
            )

        assertThrows(RuntimeException::class.java) {
            fraSafJournalpost(journalpost)
        }
    }

    @Test
    fun `Retning I mappes korrekt`() {
        val journalpost = lagJournalpost().copy(journalposttype = JOURNALPOSTTYPE_INN)

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(Kommunikasjonsretning.INN, dokumentMetadata.retning)
    }

    @Test
    fun `Retning U mappes korrekt`() {
        val journalpost = lagJournalpost().copy(journalposttype = JOURNALPOSTTYPE_UT)

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(Kommunikasjonsretning.UT, dokumentMetadata.retning)
    }

    @Test
    fun `Retning N mappes korrekt`() {
        val journalpost = lagJournalpost().copy(journalposttype = JOURNALPOSTTYPE_INTERN)

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(Kommunikasjonsretning.INTERN, dokumentMetadata.retning)
    }

    @Test
    fun `Sett variantformat til SLADDET om SLADDET eksisterer`() {
        val journalpost =
            lagJournalpost().copy(
                dokumenter =
                    listOf(
                        lagHoveddokument().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.ARKIV, null),
                                    Dokumentvariant(true, Variantformat.SLADDET, null),
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                ),
                        ),
                        lagVedlegg().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.ARKIV, null),
                                    Dokumentvariant(true, Variantformat.SLADDET, null),
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                ),
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(SLADDET, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(SLADDET, dokumentMetadata.vedlegg[0].variantformat)
        assertEquals(2, dokumentMetadata.vedlegg.size)
    }

    @Test
    fun `Sett variantformat til ARKIV om ingen SLADDET eksisterer`() {
        val journalpost =
            lagJournalpost().copy(
                dokumenter =
                    listOf(
                        lagHoveddokument().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                    Dokumentvariant(true, Variantformat.ARKIV, null),
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                ),
                        ),
                        lagVedlegg().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                    Dokumentvariant(true, Variantformat.ARKIV, null),
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                ),
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(ARKIV, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(ARKIV, dokumentMetadata.vedlegg[0].variantformat)
        assertEquals(2, dokumentMetadata.vedlegg.size)
    }

    @Test
    fun `fjerne dokumenter uten gyldige varianter`() {
        val journalpost =
            lagJournalpost().copy(
                dokumenter =
                    listOf(
                        lagHoveddokument().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.ARKIV, null),
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                ),
                        ),
                        lagVedlegg().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                ),
                        ),
                        lagVedlegg().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.SLADDET, null),
                                    Dokumentvariant(true, Variantformat.__UNKNOWN_VALUE, null),
                                ),
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))
        assertEquals(ARKIV, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(1, dokumentMetadata.vedlegg.filter { !it.isLogiskDokument }.size)
        assertEquals(SLADDET, dokumentMetadata.vedlegg[0].variantformat)
    }

    @Test
    fun `Hvis dokumentvarient er kun arkiv skjermet vis arkiv skjermet`() {
        val journalpost =
            lagJournalpost().copy(
                dokumenter =
                    listOf(
                        lagHoveddokument().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.ARKIV, SkjermingType.POL),
                                ),
                        ),
                        lagVedlegg().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.ARKIV, POL),
                                ),
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(ARKIV, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(POL.name, dokumentMetadata.hoveddokument.skjerming)
        assertEquals(ARKIV, dokumentMetadata.vedlegg[0].variantformat)
        assertEquals(POL.name, dokumentMetadata.vedlegg[0].skjerming)
    }

    @Test
    fun `Hvis arkiv skjermet men eksiterer sladdet ikke skjermet, vis sladdet ikke skjermet`() {
        val journalpost =
            lagJournalpost().copy(
                dokumenter =
                    listOf(
                        lagHoveddokument().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.ARKIV, POL),
                                    Dokumentvariant(true, Variantformat.SLADDET, null),
                                ),
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(SLADDET, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(null, dokumentMetadata.hoveddokument.skjerming)
    }

    @Test
    fun `Hvis arkiv skjermet og eksisterer sladdet som er skjermet vis sladdet skjermet`() {
        val journalpost =
            lagJournalpost().copy(
                dokumenter =
                    listOf(
                        lagHoveddokument().copy(
                            dokumentvarianter =
                                listOf(
                                    Dokumentvariant(true, Variantformat.SLADDET, POL),
                                    Dokumentvariant(true, Variantformat.ARKIV, POL),
                                ),
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(SLADDET, dokumentMetadata.hoveddokument.variantformat)
        assertEquals(POL.name, dokumentMetadata.hoveddokument.skjerming)
    }

    @Test
    fun `Bruker som avsender mappes korrekt`() {
        val navn = "Aremark"
        val journalpost =
            lagJournalpost().copy(
                avsenderMottaker = AvsenderMottaker(true, navn),
                journalposttype = JOURNALPOSTTYPE_INN,
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(SLUTTBRUKER, dokumentMetadata.avsender)
        assertEquals(NAV, dokumentMetadata.mottaker)
        assertEquals(navn, dokumentMetadata.navn)
    }

    @Test
    fun `Bruker som mottaker mappes korrekt`() {
        val navn = "Aremark"
        val journalpost =
            lagJournalpost().copy(
                avsenderMottaker = AvsenderMottaker(true, navn),
                journalposttype = JOURNALPOSTTYPE_UT,
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(NAV, dokumentMetadata.avsender)
        assertEquals(SLUTTBRUKER, dokumentMetadata.mottaker)
        assertEquals(navn, dokumentMetadata.navn)
    }

    @Test
    fun `Intern retning mappes korrekt`() {
        val journalpost =
            lagJournalpost().copy(
                avsenderMottaker = AvsenderMottaker(false, "Aremark"),
                journalposttype = JOURNALPOSTTYPE_INTERN,
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(NAV, dokumentMetadata.avsender)
        assertEquals(NAV, dokumentMetadata.mottaker)
    }

    @Test
    fun `Ekstern som mottaker mappes korrekt`() {
        val eksternNavn = "Aremark sin lege"
        val journalpost =
            lagJournalpost().copy(
                bruker = lagJournalpost().bruker?.copy(id = "10108000398"),
                avsenderMottaker = AvsenderMottaker(false, eksternNavn),
                journalposttype = JOURNALPOSTTYPE_UT,
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(NAV, dokumentMetadata.avsender)
        assertEquals(EKSTERN_PART, dokumentMetadata.mottaker)
        assertEquals(eksternNavn, dokumentMetadata.navn)
    }

    @Test
    fun `Ekstern som avsender mappes korrekt`() {
        val eksternNavn = "Aremark sin lege"
        val journalpost =
            lagJournalpost().copy(
                bruker = lagJournalpost().bruker?.copy(id = "10108000398"),
                avsenderMottaker = AvsenderMottaker(false, eksternNavn),
                journalposttype = JOURNALPOSTTYPE_INN,
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(NAV, dokumentMetadata.mottaker)
        assertEquals(EKSTERN_PART, dokumentMetadata.avsender)
        assertEquals(eksternNavn, dokumentMetadata.navn)
    }

    @Test
    fun `Navn blir ukjent når avsenderMottaker er null`() {
        val journalpost =
            lagJournalpost().copy(
                avsenderMottaker = null,
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals("ukjent", dokumentMetadata.navn)
    }

    @Test
    fun `Kaster feil ved ukjent journalposttype`() {
        val journalpost = lagJournalpost().copy(journalposttype = Journalposttype.__UNKNOWN_VALUE)

        assertThrows(RuntimeException::class.java) {
            fraSafJournalpost(journalpost)
        }
    }

    @Test
    fun `Setter nådato om relevant dato for type ikke eksisterer`() {
        mockkStatic(LocalDateTime::class) {
            val now = LocalDateTime.now()
            every { LocalDateTime.now() } returns now

            val journalpost =
                lagJournalpost().copy(
                    journalposttype = JOURNALPOSTTYPE_INN,
                    relevanteDatoer = emptyList(),
                )

            val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

            assertEquals(now, dokumentMetadata.dato)
        }
    }

    @Test
    fun `Bruker registrert dato for Inngående`() {
        val registrertDato = LocalDateTime.parse("2018-11-11T13:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost =
            lagJournalpost().copy(
                relevanteDatoer =
                    listOf(
                        RelevantDato(
                            datotype = Datotype.DATO_REGISTRERT,
                            dato = registrertDato,
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(registrertDato, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker ekspedert dato primært om ikke null for Utgående`() {
        val ekspedertDato = LocalDateTime.parse("2018-11-11T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
        val sendtPrintDato = LocalDateTime.parse("2017-10-10T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
        val journalFoertDato = LocalDateTime.parse("2016-01-01T13:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost =
            lagJournalpost().copy(
                journalposttype = JOURNALPOSTTYPE_UT,
                relevanteDatoer =
                    listOf(
                        RelevantDato(
                            datotype = Datotype.DATO_EKSPEDERT,
                            dato = ekspedertDato,
                        ),
                        RelevantDato(
                            datotype = Datotype.DATO_SENDT_PRINT,
                            dato = sendtPrintDato,
                        ),
                        RelevantDato(
                            datotype = Datotype.DATO_JOURNALFOERT,
                            dato = journalFoertDato,
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(ekspedertDato, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker sendt print dato sekundært om ekspedert ikke eksisterer for Utgående`() {
        val sendtPrintDato = LocalDateTime.parse("2017-10-10T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
        val journalFoertDato = LocalDateTime.parse("2016-01-01T13:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost =
            lagJournalpost().copy(
                journalposttype = JOURNALPOSTTYPE_UT,
                relevanteDatoer =
                    listOf(
                        RelevantDato(
                            datotype = Datotype.DATO_SENDT_PRINT,
                            dato = sendtPrintDato,
                        ),
                        RelevantDato(
                            datotype = Datotype.DATO_JOURNALFOERT,
                            dato = journalFoertDato,
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(sendtPrintDato, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker journalfoert dato tertiert om ekspedert og sendt print ikke eksisterer for Utgående`() {
        val journalFoertDato = LocalDateTime.parse("2016-01-01T13:23:57", DateTimeFormatter.ISO_DATE_TIME)

        val journalpost =
            lagJournalpost().copy(
                journalposttype = JOURNALPOSTTYPE_UT,
                relevanteDatoer =
                    listOf(
                        RelevantDato(
                            datotype = Datotype.DATO_JOURNALFOERT,
                            dato = journalFoertDato,
                        ),
                    ),
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(journalFoertDato, dokumentMetadata.dato)
    }

    @Test
    fun `Bruker journalfoert dato for Intern`() {
        val journalFoertDato = LocalDateTime.parse("2018-11-11T13:23:57", DateTimeFormatter.ISO_DATE_TIME)
        val journalpost =
            lagJournalpost().copy(
                relevanteDatoer =
                    listOf(
                        RelevantDato(
                            datotype = Datotype.DATO_JOURNALFOERT,
                            dato = journalFoertDato,
                        ),
                    ),
                journalposttype = JOURNALPOSTTYPE_INTERN,
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(journalFoertDato, dokumentMetadata.dato)
    }

    @Test
    fun `Setter datoLest for Utgående`() {
        val lestDato = LocalDateTime.now()
        val journalpost =
            lagJournalpost().copy(
                relevanteDatoer =
                    listOf(
                        RelevantDato(
                            datotype = Datotype.DATO_LEST,
                            dato = lestDato,
                        ),
                    ),
                journalposttype = JOURNALPOSTTYPE_UT,
            )

        val dokumentMetadata = requireNotNull(fraSafJournalpost(journalpost))

        assertEquals(lestDato, dokumentMetadata.lestDato)
    }
}

private fun lagJournalpost(): Journalpost {
    val bruker = Bruker(id = aremarkFNR, type = BrukerIdType.FNR)
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
        sak = sak,
    )
}

private fun lagSak(): Sak =
    Sak(
        arkivsaksnummer = arkivsaknummer,
        fagsakId = fagsakId,
        fagsaksystem = fagsakSystem,
    )

private fun lagAvsenderMottaker() = AvsenderMottaker(true, avsenderMottakerNavn)

private fun lagVedlegg(): DokumentInfo = lagDokumentInfo(vedleggTittel)

private fun lagHoveddokument(): DokumentInfo = lagDokumentInfo(hovedDokumentTittel)

private fun lagDokumentInfo(tittel: String): DokumentInfo =
    DokumentInfo(
        tittel = tittel,
        dokumentInfoId = dokumentinfoid,
        dokumentvarianter = listOf(lagDokumentVariant()),
        logiskeVedlegg = listOf(LogiskVedlegg(logiskVedleggtittel)),
        dokumentstatus = dokumentStatus,
    )

private fun lagDokumentVariant(): Dokumentvariant = Dokumentvariant(true, variantformat, null)
