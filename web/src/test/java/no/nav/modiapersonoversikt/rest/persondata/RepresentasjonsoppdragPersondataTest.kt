package no.nav.modiapersonoversikt.rest.persondata

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.HentPersonBolkResult
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.Person
import no.nav.modiapersonoversikt.consumer.representasjon.RepresentasjonApi
import no.nav.modiapersonoversikt.consumer.representasjon.generated.infrastructure.Serializer
import no.nav.modiapersonoversikt.consumer.representasjon.generated.models.FullmaktDto
import no.nav.modiapersonoversikt.consumer.representasjon.generated.models.VergemaalDto
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.Tilgangsavslag
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.Tilgangsmaskinen
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgang
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgangResultat
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.persondata.Persondata
import no.nav.modiapersonoversikt.service.persondata.PersondataResult.InformasjonElement
import no.nav.modiapersonoversikt.service.persondata.PersondataServiceImpl
import no.nav.modiapersonoversikt.testutils.AuthContextExtension
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException
import java.time.LocalDate
import java.util.*

internal class RepresentasjonsoppdragPersondataTest {
    @JvmField
    @RegisterExtension
    val auth =
        AuthContextExtension(AuthContext(UserRole.INTERN, PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())))

    private val fnr = Fnr("00000000000")
    private val repr = mockk<RepresentasjonApi>()
    private val pdl = mockk<PdlOppslagService>()
    private val tilgang = mockk<Tilgangsmaskinen>()
    private val kodeverk = mockk<EnhetligKodeverk.Service>()
    private val krr = mockk<Krr.Service>(relaxed = true)
    private val service =
        PersondataServiceImpl(
            pdl = pdl,
            representasjonApi = repr,
            krrService = krr,
            norgApi = mockk(),
            skjermedePersonerApi = mockk { every { erSkjermetPerson(any()) } returns false },
            kontonummerService = mockk { every { hentKontonummer(any()) } returns null },
            oppfolgingService =
                mockk {
                    every { hentOppfolgingsinfo(any()) } returns ArbeidsrettetOppfolging.Info(false, false, null, null)
                },
            policyEnforcementPoint = mockk(),
            kodeverk = kodeverk,
            tjenestekallLogger = TjenestekallLogg,
            tilgangsmaskinen = tilgang,
        )
    private val start = LocalDate.of(2026, 1, 1)

    init {
        every { kodeverk.hentKodeverk(any<EnhetligKodeverk.Kilde<String, String>>()) } returns gittKodeverk()
        every { pdl.hentPersondata(fnr.get()) } returns
            mockk<HentPersondata.Result> {
                every { hentPerson } returns
                    testPerson.copy(
                        vergemaalEllerFremtidsfullmakt = emptyList(),
                        foreldreansvar = emptyList(),
                        sivilstand = emptyList(),
                        forelderBarnRelasjon = emptyList(),
                        kontaktinformasjonForDoedsbo = emptyList(),
                    )
                every { hentGeografiskTilknytning } returns null
            }
        every { repr.hentFullmakterForFullmaktsgiver(fnr) } returns emptyList()
        every { repr.hentFullmakterForFullmektig(fnr) } returns emptyList()
        every { repr.hentVergemalForVerge(fnr) } returns emptyList()
        every { tilgang.sjekkTilganger(any(), emptyList()) } returns emptyMap()
    }

    private fun fullmakt(ident: String) = FullmaktDto(UUID.randomUUID(), ident, fnr.get(), start, setOf("AAP"), emptySet(), emptyList())

    private fun vergemal(ident: String) = VergemaalDto(42, fnr.get(), ident, "VOKSEN", setOf("AAP"), emptySet(), start, false)

    private fun pdlPerson(
        ident: String,
        person: Person? = mockk(relaxed = true),
    ): HentPersonBolkResult =
        mockk {
            every { this@mockk.ident } returns ident
            every { this@mockk.person } returns person
        }

    @Test
    fun `samler unike motparter men beholder flere forhold og historikk`() {
        val ident = "00000000001"
        every { repr.hentFullmakterForFullmektig(fnr) } returns listOf(fullmakt(ident), fullmakt(ident))
        every { repr.hentVergemalForVerge(fnr) } returns listOf(vergemal(ident))
        every { tilgang.sjekkTilganger(any(), listOf(ident)) } returns
            mapOf(ident to TredjepersonTilgangResultat(TredjepersonTilgang.GODKJENT))
        every { pdl.hentTredjepartspersondata(listOf(ident)) } returns listOf(pdlPerson(ident))

        val data = service.hentPerson(fnr.get())
        val result = data.person

        assertEquals(2, result.fullmaktsgivere.size)
        assertEquals(
            ident,
            result.fullmaktsgivere
                .first()
                .person.ident,
        )
        assertEquals(
            "Arbeidsavklaringspenger",
            result.fullmaktsgivere
                .first()
                .leserettigheter
                .single()
                .beskrivelse,
        )
        assertFalse(result.vergehavere.single().aktivt)
        assertTrue(data.feilendeSystemer.isEmpty())
        verify(exactly = 1) { pdl.hentTredjepartspersondata(listOf(ident)) }
        verifyOrder {
            tilgang.sjekkTilganger(any(), listOf(ident))
            pdl.hentTredjepartspersondata(listOf(ident))
        }
        verify(exactly = 0) { krr.hentDigitalKontaktinformasjon(ident) }
    }

    @Test
    fun `deler tilgangskontroll og PDL-oppslag med eksisterende fullmektige`() {
        val ident = "00000000001"
        val annen = "00000000002"
        every { repr.hentFullmakterForFullmaktsgiver(fnr) } returns
            listOf(fullmakt(fnr.get()).copy(fullmektig = ident))
        every { repr.hentFullmakterForFullmektig(fnr) } returns listOf(fullmakt(ident))
        every { repr.hentVergemalForVerge(fnr) } returns listOf(vergemal(annen))
        every { tilgang.sjekkTilganger(any(), listOf(ident, annen)) } returns
            listOf(ident, annen).associateWith { TredjepersonTilgangResultat(TredjepersonTilgang.GODKJENT) }
        every { pdl.hentTredjepartspersondata(listOf(ident, annen)) } returns
            listOf(pdlPerson(ident), pdlPerson(annen))

        val person = service.hentPerson(fnr.get()).person

        assertEquals(ident, person.fullmektige.single().ident)
        assertEquals(
            ident,
            person.fullmaktsgivere
                .single()
                .person.ident,
        )
        assertEquals(
            annen,
            person.vergehavere
                .single()
                .person.ident,
        )
        verify(exactly = 1) { tilgang.sjekkTilganger(any(), any()) }
        verify(exactly = 1) { pdl.hentTredjepartspersondata(any()) }
        verify(exactly = 1) { krr.hentDigitalKontaktinformasjon(ident) }
        verify(exactly = 0) { krr.hentDigitalKontaktinformasjon(annen) }
    }

    @Test
    fun `avviste og uavklarte motparter lekker ikke ident gjennom relasjonsdata`() {
        val avvist = "00000000001"
        val uavklart = "00000000002"
        val avslag = Tilgangsavslag("AVVIST_SKJERMING", "Du har ikke tilgang til skjermet person")
        every { repr.hentFullmakterForFullmektig(fnr) } returns listOf(fullmakt(avvist))
        every { repr.hentVergemalForVerge(fnr) } returns listOf(vergemal(uavklart))
        every { tilgang.sjekkTilganger(any(), any()) } returns
            mapOf(
                avvist to TredjepersonTilgangResultat(TredjepersonTilgang.AVVIST, avslag),
                uavklart to TredjepersonTilgangResultat(TredjepersonTilgang.UAVKLART),
            )

        val data = service.hentPerson(fnr.get())
        val result = data.person
        val json = Serializer.jacksonObjectMapper.writeValueAsString(result)

        assertFalse(json.contains(avvist))
        assertFalse(json.contains(uavklart))
        assertFalse(json.contains("endringslogg"))
        assertEquals(
            Persondata.Skjulingsaarsak.MANGLER_TILGANG,
            result.fullmaktsgivere
                .single()
                .person.skjulingsaarsak,
        )
        assertEquals(
            avslag,
            result.fullmaktsgivere
                .single()
                .person.tilgangsavslag,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.UAVKLART,
            result.vergehavere
                .single()
                .person.skjulingsaarsak,
        )
        assertEquals(listOf(InformasjonElement.TILGANGSMASKINEN_TREDJEPARTSPERSONER), data.feilendeSystemer)
        verify(exactly = 0) { pdl.hentTredjepartspersondata(any()) }
    }

    @Test
    fun `krever entydig PDL-treff selv om tilgang er godkjent`() {
        val identer = listOf("00000000001", "00000000002", "00000000003", "00000000004")
        every { repr.hentFullmakterForFullmektig(fnr) } returns identer.map(::fullmakt)
        every { tilgang.sjekkTilganger(any(), any()) } returns
            identer.associateWith { TredjepersonTilgangResultat(TredjepersonTilgang.GODKJENT) }
        every { pdl.hentTredjepartspersondata(any()) } returns
            listOf(pdlPerson(identer[0]), pdlPerson(identer[1]), pdlPerson(identer[1]), pdlPerson(identer[2], null))

        val data = service.hentPerson(fnr.get())
        val result = data.person

        assertEquals(listOf(identer[0], null, null, null), result.fullmaktsgivere.map { it.person.ident })
        assertTrue(result.fullmaktsgivere.drop(1).all { it.person.skjulingsaarsak == Persondata.Skjulingsaarsak.UAVKLART })
        assertTrue(InformasjonElement.PDL_TREDJEPARTSPERSONER in data.feilendeSystemer)
    }

    @Test
    fun `feil i ett repr-oppslag skjuler ikke den andre listen`() {
        every { repr.hentFullmakterForFullmektig(fnr) } throws IOException("utilgjengelig")
        every { repr.hentVergemalForVerge(fnr) } returns listOf(vergemal("00000000001"))
        every { tilgang.sjekkTilganger(any(), any()) } returns emptyMap()

        val data = service.hentPerson(fnr.get())
        val result = data.person

        assertTrue(result.fullmaktsgivere.isEmpty())
        assertEquals(1, result.vergehavere.size)
        assertTrue(InformasjonElement.REPR_API_FULLMAKTSGIVERE in data.feilendeSystemer)
        assertFalse(InformasjonElement.REPR_API_VERGEHAVERE in data.feilendeSystemer)
    }

    @Test
    fun `store lister deles i PDL-bolker og feil i en bolk beholder resten`() {
        val identer = (1..205).map { it.toString().padStart(11, '0') }
        every { repr.hentFullmakterForFullmektig(fnr) } returns identer.map(::fullmakt)
        every { tilgang.sjekkTilganger(any(), any()) } returns
            identer.associateWith { TredjepersonTilgangResultat(TredjepersonTilgang.GODKJENT) }
        val bolker = mutableListOf<List<String>>()
        every { pdl.hentTredjepartspersondata(capture(bolker)) } answers {
            val bolk = firstArg<List<String>>()
            if (bolk.first() == identer[100]) throw IOException("feil i bolk")
            bolk.map { pdlPerson(it) }
        }

        val data = service.hentPerson(fnr.get())
        val result = data.person

        assertEquals(listOf(100, 100, 5), bolker.map { it.size })
        assertEquals(205, result.fullmaktsgivere.size)
        assertEquals(105, result.fullmaktsgivere.count { it.person.ident != null })
        assertEquals(listOf(InformasjonElement.PDL_TREDJEPARTSPERSONER), data.feilendeSystemer)
    }

    @Test
    fun `tomme lister gir ingen personoppslag og feil part i repr-respons avvises`() {
        assertTrue(service.hentPerson(fnr.get()).feilendeSystemer.isEmpty())
        verify(exactly = 0) { pdl.hentTredjepartspersondata(any()) }
        every { repr.hentFullmakterForFullmektig(fnr) } returns listOf(fullmakt("00000000001").copy(fullmektig = "00000000002"))

        val result = service.hentPerson(fnr.get())

        assertTrue(result.person.fullmaktsgivere.isEmpty())
        assertEquals(listOf(InformasjonElement.REPR_API_FULLMAKTSGIVERE), result.feilendeSystemer)
    }

    @Test
    fun `blank motpartsident forblir uavklart uten personoppslag`() {
        every { repr.hentFullmakterForFullmektig(fnr) } returns listOf(fullmakt(""))
        every { repr.hentVergemalForVerge(fnr) } returns listOf(vergemal(""))

        val person = service.hentPerson(fnr.get()).person

        listOf(person.fullmaktsgivere.single().person, person.vergehavere.single().person).forEach {
            assertNull(it.ident)
            assertNull(it.navn)
            assertEquals(Persondata.Skjulingsaarsak.UAVKLART, it.skjulingsaarsak)
        }
        verify(exactly = 0) { pdl.hentTredjepartspersondata(any()) }
    }
}
