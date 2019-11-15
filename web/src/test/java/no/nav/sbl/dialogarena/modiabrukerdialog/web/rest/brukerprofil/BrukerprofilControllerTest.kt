package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil

import com.nhaarman.mockito_kotlin.*
import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi
import no.nav.behandlebrukerprofil.consumer.support.mock.BehandleBrukerprofilMockFactory.getBruker
import no.nav.brukerprofil.domain.BankkontoUtland
import no.nav.brukerprofil.domain.adresser.Gateadresse
import no.nav.brukerprofil.domain.adresser.Matrikkeladresse
import no.nav.brukerprofil.domain.adresser.Postboksadresse
import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kjerneinfo.consumer.fim.behandleperson.BehandlePersonServiceBi
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse
import no.nav.kjerneinfo.domain.person.Fodselsnummer
import no.nav.kjerneinfo.domain.person.Person
import no.nav.kjerneinfo.domain.person.Personfakta
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserUgyldigInput
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import javax.ws.rs.BadRequestException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.core.Response.Status
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val AREMARK_FNR = "10108000398"
private const val D_NUMMER = "44000000000"
private const val INNLOGGET_SAKSBEHANDLER = "z111111"

private const val FORNAVN = "Peter"
private const val MELLOMNAVN = "Wessel"
private const val ETTERNAVN = "Zapffe"

private const val TILLEGGSADRESSE = "Art Vandelay"
private const val TILLEGGSADRESSETYPE = "C/O"
private const val GATENAVN = "Storgata"
private const val HUSNUMMER = "42"
private const val HUSBOKSTAV = "A"
private const val POSTNUMMER = "0123"
private const val BOLIGNUMMER = "H0201"
private const val POSTBOKSANLEGG = "Falkum"
private const val POSTBOKSNUMMER = "55"
private const val EIENDOMSNAVN = "Flåm"
private const val ADRESSELINJE_1 = "Linje 1"
private const val ADRESSELINJE_2 = "Linje 2"
private const val ADRESSELINJE_3 = "Linje 3"
private const val LANDKODE_KODE = "IOT"

private const val HJEMIDENTIFIKATOR = "00000000"
private const val MOBILIDENTIFIKATOR = "00000001"
private const val RETNINGSNUMMER_NORGE = "47"
private const val RETNINGSNUMMER_IKKE_NORGE = "48"

private const val SWIFT = "Ganskelang-medbokstaverogtall-1234"
private const val BANKNAVN = "Pengebingen ASA"
private const val KONTONUMMER = "11111111111"

private const val TOLKEHJELP_KODE = "TOHJ"

private val OM_FIRE_UKER = LocalDate.now().plusWeeks(4)

class BrukerprofilControllerTest {

    private val behandlePersonService: BehandlePersonServiceBi = mock()
    private val behandleBrukerProfilService: BehandleBrukerprofilServiceBi = mock()
    private val kjerneinfoService: PersonKjerneinfoServiceBi = mock()
    private val tilgangskontrollContext: TilgangskontrollContext = mock()
    private val tilgangskontroll: Tilgangskontroll = Tilgangskontroll(tilgangskontrollContext)

    private val controller = BrukerprofilController(
            behandlePersonService,
            behandleBrukerProfilService,
            kjerneinfoService,
            tilgangskontroll
    )

    @BeforeEach
    fun before() {
        whenever(tilgangskontrollContext.harSaksbehandlerRolle(any())).thenReturn(true)
        whenever(kjerneinfoService.hentBrukerprofil(any())).thenReturn(getBruker())
    }

    @Nested
    inner class Navn {

        @Test
        fun `Kaller behandlePersonService med riktig request ved navneendring`() {
            whenever(kjerneinfoService.hentKjerneinformasjon(any()))
                    .thenReturn(mockKjerneinformasjonResponse(D_NUMMER))

            controller.endreNavn(D_NUMMER, lagRequest(D_NUMMER))

            verify(behandlePersonService, times(1)).endreNavn(check {
                assertEquals(D_NUMMER, it.fnr)
                assertEquals(FORNAVN, it.fornavn)
                assertEquals(MELLOMNAVN, it.mellomnavn)
                assertEquals(ETTERNAVN, it.etternavn)
            })
        }

        @Test
        fun `Kaster feil hvis noen prøver å endre navn til en person med vanlig fødselsnummer`() {
            whenever(kjerneinfoService.hentKjerneinformasjon(any()))
                    .thenReturn(mockKjerneinformasjonResponse(AREMARK_FNR))

            assertFailsWith<ForbiddenException> { controller.endreNavn(AREMARK_FNR, lagRequest(AREMARK_FNR)) }
        }

        @Test
        fun `Kan endre navn til person med vanlig fødselsnummer, men som er utvandret`() {
            val kjerneinformasjon = mockKjerneinformasjonResponse(AREMARK_FNR)
            kjerneinformasjon.person.personfakta.bostatus = Kodeverdi("UTVA", "UTVA")
            whenever(kjerneinfoService.hentKjerneinformasjon(any())).thenReturn(kjerneinformasjon)

            controller.endreNavn(AREMARK_FNR, lagRequest(AREMARK_FNR))

            verify(behandlePersonService, times(1)).endreNavn(any())
        }

        @Test
        fun `Kaster feil om saksbehandler ikke har riktig rolle`() {
            whenever(kjerneinfoService.hentKjerneinformasjon(any())).thenReturn(mockKjerneinformasjonResponse(D_NUMMER))
            whenever(tilgangskontrollContext.harSaksbehandlerRolle(any())).thenReturn(false)

            val feilmelding = assertFailsWith<ForbiddenException> { controller.endreNavn(AREMARK_FNR, lagRequest(D_NUMMER)) }

            assert(feilmelding.message!!.contains("har ikke tilgang til å endre navn"), { "Feilmelding skal inneholde påkrevd rolle" })
        }

        private fun lagRequest(ident: String) =
                EndreNavnRequest(
                        fornavn = FORNAVN,
                        mellomnavn = MELLOMNAVN,
                        etternavn = ETTERNAVN,
                        fødselsnummer = ident
                )

        private fun mockKjerneinformasjonResponse(fødselsnummer: String) =
                HentKjerneinformasjonResponse().apply {
                    person = Person().apply {
                        fodselsnummer = Fodselsnummer(fødselsnummer)
                        personfakta = Personfakta().apply { bostatus = Kodeverdi() }
                    }
                }

    }

    @Nested
    inner class Adresse {

        @Test
        fun `Kaller tjenesten uten midlertidige adresser hvis folkeregistrertAdresse er true`() {
            controller.endreAdresse(AREMARK_FNR, EndreAdresseRequest(folkeregistrertAdresse = true))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                assertNull(it.bruker.midlertidigadresseNorge)
                assertNull(it.bruker.midlertidigadresseUtland)
            })

        }

        @Test
        fun `Kaller tjenesten med gateadresse hvis den skal settes`() {
            controller.endreAdresse(AREMARK_FNR, EndreAdresseRequest(EndreAdresseRequest.NorskAdresse(
                    EndreAdresseRequest.NorskAdresse.Gateadresse(
                            tilleggsadresse = TILLEGGSADRESSE,
                            gatenavn = GATENAVN,
                            husnummer = HUSNUMMER,
                            husbokstav = HUSBOKSTAV,
                            bolignummer = BOLIGNUMMER,
                            postnummer = POSTNUMMER,
                            gyldigTil = OM_FIRE_UKER
                    )
            )))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                it.bruker.run {
                    assertNull(midlertidigadresseUtland)
                    assertNotNull(midlertidigadresseNorge)
                    (midlertidigadresseNorge as Gateadresse).run {
                        assertEquals(TILLEGGSADRESSE, tilleggsadresse)
                        assertEquals(TILLEGGSADRESSETYPE, tilleggsadresseType)
                        assertEquals(GATENAVN, gatenavn)
                        assertEquals(HUSNUMMER, husnummer)
                        assertEquals(HUSBOKSTAV, husbokstav)
                        assertEquals(BOLIGNUMMER, bolignummer)
                        assertEquals(POSTNUMMER, poststed)
                        assertEquals("$OM_FIRE_UKER", "${postleveringsPeriode.to}")
                    }
                }
            })

        }

        @Test
        fun `Kaller tjenesten med postboksadresse hvis den skal settes`() {
            controller.endreAdresse(AREMARK_FNR, EndreAdresseRequest(EndreAdresseRequest.NorskAdresse(
                    postboksadresse = EndreAdresseRequest.NorskAdresse.Postboksadresse(
                            tilleggsadresse = TILLEGGSADRESSE,
                            postnummer = POSTNUMMER,
                            gyldigTil = OM_FIRE_UKER,
                            postboksanlegg = POSTBOKSANLEGG,
                            postboksnummer = POSTBOKSNUMMER
                    )
            )))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                it.bruker.run {
                    assertNull(midlertidigadresseUtland)
                    assertNotNull(midlertidigadresseNorge)
                    (midlertidigadresseNorge as Postboksadresse).run {
                        assertEquals(TILLEGGSADRESSE, tilleggsadresse)
                        assertEquals(TILLEGGSADRESSETYPE, tilleggsadresseType)
                        assertEquals(POSTNUMMER, poststed)
                        assertEquals("$OM_FIRE_UKER", "${postleveringsPeriode.to}")
                        assertEquals(POSTBOKSANLEGG, postboksanlegg)
                        assertEquals(POSTBOKSNUMMER, postboksnummer)
                    }
                }
            })

        }

        @Test
        fun `Kaller tjenesten med matrikkeladresse hvis den skal settes`() {
            controller.endreAdresse(AREMARK_FNR, EndreAdresseRequest(EndreAdresseRequest.NorskAdresse(
                    matrikkeladresse = EndreAdresseRequest.NorskAdresse.Matrikkeladresse(
                            tilleggsadresse = TILLEGGSADRESSE,
                            postnummer = POSTNUMMER,
                            gyldigTil = OM_FIRE_UKER,
                            eiendomsnavn = EIENDOMSNAVN
                    )
            )))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                it.bruker.run {
                    assertNull(midlertidigadresseUtland)
                    assertNotNull(midlertidigadresseNorge)
                    (midlertidigadresseNorge as Matrikkeladresse).run {
                        assertEquals(TILLEGGSADRESSE, tilleggsadresse)
                        assertEquals(TILLEGGSADRESSETYPE, tilleggsadresseType)
                        assertEquals(POSTNUMMER, poststed)
                        assertEquals("$OM_FIRE_UKER", "${postleveringsPeriode.to}")
                        assertEquals(EIENDOMSNAVN, eiendomsnavn)
                    }
                }
            })

        }

        @Test
        fun `Kaller tjenesten med utenlandsk adresse hvis den skal settes`() {
            controller.endreAdresse(AREMARK_FNR, EndreAdresseRequest(
                    utenlandskAdresse = EndreAdresseRequest.UtenlandskAdresse(
                            gyldigTil = OM_FIRE_UKER,
                            landkode = LANDKODE_KODE,
                            adresselinje1 = ADRESSELINJE_1,
                            adresselinje2 = ADRESSELINJE_2,
                            adresselinje3 = ADRESSELINJE_3
                    )
            ))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                it.bruker.run {
                    assertNotNull(midlertidigadresseUtland)
                    assertNull(midlertidigadresseNorge)
                    midlertidigadresseUtland.run {
                        assertEquals("$OM_FIRE_UKER", "${postleveringsPeriode.to}")
                        assertEquals(Kodeverdi().apply { kodeRef = LANDKODE_KODE }, landkode)
                        assertEquals(ADRESSELINJE_1, adresselinje1)
                        assertEquals(ADRESSELINJE_2, adresselinje2)
                        assertEquals(ADRESSELINJE_3, adresselinje3)
                    }
                }
            })

        }

        @Test
        fun `Returnerer 403 hvis tjenesten kaster sikkerhetsbegrensning`() {
            whenever(behandleBrukerProfilService.oppdaterKontaktinformasjonOgPreferanser(any()))
                    .thenThrow(OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning())

            val response = controller.endreAdresse(AREMARK_FNR, EndreAdresseRequest(folkeregistrertAdresse = true))

            assertEquals(Status.FORBIDDEN.statusCode, response.status)
        }

        @Test
        fun `Kaster 4   00 hvis ingen adresse er satt i norskAdresse`() =
                assertFailsWith<BadRequestException> {
                    controller.endreAdresse(AREMARK_FNR, EndreAdresseRequest(
                            norskAdresse = EndreAdresseRequest.NorskAdresse()
                    ))
                }

        @Test
        fun `Kaster 400 hvis ingen adresse er satt`() =
                assertFailsWith<BadRequestException> { controller.endreAdresse(AREMARK_FNR, EndreAdresseRequest()) }

    }

    @Nested
    inner class Telefon {

        @Test
        fun `Kaller tjenesten med hjemnummer`() {
            controller.endreTelefonnummer(AREMARK_FNR, EndreTelefonnummerRequest(
                    hjem = EndreTelefonnummerRequest.Telefon(HJEMIDENTIFIKATOR, RETNINGSNUMMER_NORGE)
            ))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                it.bruker.run {
                    assertNotNull(hjemTlf)
                    assertNull(mobil)
                    assertNull(jobbTlf)
                    hjemTlf.run {
                        assertEquals(HJEMIDENTIFIKATOR, identifikator)
                        assertEquals(RETNINGSNUMMER_NORGE, retningsnummer.kodeRef)
                    }
                }
            })

        }

        @Test
        fun `Kaller tjenesten med mobilnummer`() {
            controller.endreTelefonnummer(AREMARK_FNR, EndreTelefonnummerRequest(
                    hjem = EndreTelefonnummerRequest.Telefon(HJEMIDENTIFIKATOR, RETNINGSNUMMER_NORGE),
                    mobil = EndreTelefonnummerRequest.Telefon(MOBILIDENTIFIKATOR, RETNINGSNUMMER_IKKE_NORGE)
            ))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                it.bruker.run {
                    assertNotNull(hjemTlf)
                    assertNotNull(mobil)
                    assertNull(jobbTlf)
                    mobil.run {
                        assertEquals(MOBILIDENTIFIKATOR, identifikator)
                        assertEquals(RETNINGSNUMMER_IKKE_NORGE, retningsnummer.kodeRef)
                    }
                }
            })

        }

        @Test
        fun `Returnerer 400 ved ugyldig input`() {
            whenever(behandleBrukerProfilService.oppdaterKontaktinformasjonOgPreferanser(any()))
                    .thenThrow(OppdaterKontaktinformasjonOgPreferanserUgyldigInput())

            val response = controller.endreTelefonnummer(AREMARK_FNR, EndreTelefonnummerRequest(
                    jobb = EndreTelefonnummerRequest.Telefon(HJEMIDENTIFIKATOR, RETNINGSNUMMER_NORGE)
            ))

            assertEquals(Status.BAD_REQUEST.statusCode, response?.status)
        }


    }

    @Nested
    inner class Bankkonto {

        @Test
        fun `Kaller tjenesten med norsk kontonummer`() {
            controller.endreKontonummer(AREMARK_FNR, EndreKontonummerRequest(KONTONUMMER))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                assertEquals(KONTONUMMER, it.bruker.bankkonto.kontonummer)
            })
        }

        @Test
        fun `Kaller tjenesten med utenlandsk kontonummer`() {
            controller.endreKontonummer(AREMARK_FNR, EndreKontonummerRequest(
                    kontonummer = KONTONUMMER,
                    landkode = LANDKODE_KODE,
                    banknavn = BANKNAVN,
                    swift = SWIFT,
                    bankadresse = EndreKontonummerRequest.Adresse(
                            ADRESSELINJE_1,
                            ADRESSELINJE_2,
                            ADRESSELINJE_3
                    )
            ))

            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                (it.bruker.bankkonto as BankkontoUtland).run {
                    assertEquals(KONTONUMMER, kontonummer)
                    assertEquals(LANDKODE_KODE, landkode.kodeRef)
                    assertEquals(BANKNAVN, banknavn)
                    assertEquals(SWIFT, swift)
                    bankadresse.run {
                        assertEquals(ADRESSELINJE_1, adresselinje1)
                        assertEquals(ADRESSELINJE_2, adresselinje2)
                        assertEquals(ADRESSELINJE_3, adresselinje3)
                    }
                }
            })
        }

        @Test
        fun `Kaster 403 hvis saksbehandler ikke har riktig rolle`() {
            whenever(tilgangskontrollContext.harSaksbehandlerRolle(any())).thenReturn(false)

            assertFailsWith<ForbiddenException> {
                controller.endreKontonummer(AREMARK_FNR, EndreKontonummerRequest(KONTONUMMER))
            }
        }

        @Test
        fun `Returnerer 404 hvis personen ikke finnes`() {
            whenever(behandleBrukerProfilService.oppdaterKontaktinformasjonOgPreferanser(any()))
                    .thenThrow(OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet())

            val response = controller.endreKontonummer(AREMARK_FNR, EndreKontonummerRequest(KONTONUMMER))

            assertEquals(Status.NOT_FOUND.statusCode, response?.status)
        }

    }

    @Nested
    inner class `Tilrettelagt kommunikasjon` {

        @Test
        fun `Kaller tjenesten med tilrettelagt kommunikasjon`() {
            controller.endreTilrettelagtKommunikasjon(AREMARK_FNR, EndreTilrettelagtkommunikasjonRequest(tilrettelagtKommunikasjon = listOf(TOLKEHJELP_KODE)))
            verify(behandleBrukerProfilService).oppdaterKontaktinformasjonOgPreferanser(check {
                assertEquals(TOLKEHJELP_KODE, it.bruker.tilrettelagtKommunikasjon[0].kodeRef)
            })
        }

        @Test
        fun `Returnerer 410 hvis identen er utgått`() {
            whenever(behandleBrukerProfilService.oppdaterKontaktinformasjonOgPreferanser(any()))
                    .thenThrow(OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt())

            val response = controller.endreTilrettelagtKommunikasjon(AREMARK_FNR, EndreTilrettelagtkommunikasjonRequest(tilrettelagtKommunikasjon = listOf(TOLKEHJELP_KODE)))

            assertEquals(Status.GONE.statusCode, response?.status)
        }

    }

}