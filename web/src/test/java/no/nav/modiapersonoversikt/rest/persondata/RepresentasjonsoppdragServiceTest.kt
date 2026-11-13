package no.nav.modiapersonoversikt.rest.persondata

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.Fnr
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
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.persondata.Persondata
import no.nav.modiapersonoversikt.service.persondata.RepresentasjonsoppdragFeil
import no.nav.modiapersonoversikt.service.persondata.RepresentasjonsoppdragService
import no.nav.modiapersonoversikt.testutils.AuthContextExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException
import java.time.LocalDate
import java.util.*

internal class RepresentasjonsoppdragServiceTest {
    @JvmField
    @RegisterExtension
    val auth =
        AuthContextExtension(AuthContext(UserRole.INTERN, PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())))

    private val fnr = Fnr("00000000000")
    private val repr = mockk<RepresentasjonApi>()
    private val pdl = mockk<PdlOppslagService>()
    private val tilgang = mockk<Tilgangsmaskinen>()
    private val kodeverk = mockk<EnhetligKodeverk.Service>()
    private val service = RepresentasjonsoppdragService(repr, pdl, tilgang, kodeverk)
    private val start = LocalDate.of(2026, 1, 1)

    init {
        every { kodeverk.hentKodeverk(any<EnhetligKodeverk.Kilde<String, String>>()) } returns gittKodeverk()
        every { repr.hentFullmakterForFullmektig(fnr) } returns emptyList()
        every { repr.hentVergemalForVerge(fnr) } returns emptyList()
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

        val result = service.hentRepresentasjonsOppdrag(fnr)

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
        assertTrue(result.feilendeSystemer.isEmpty())
        verify(exactly = 1) { pdl.hentTredjepartspersondata(listOf(ident)) }
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

        val result = service.hentRepresentasjonsOppdrag(fnr)
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
        assertEquals(setOf(RepresentasjonsoppdragFeil.TILGANGSMASKINEN), result.feilendeSystemer)
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

        val result = service.hentRepresentasjonsOppdrag(fnr)

        assertEquals(listOf(identer[0], null, null, null), result.fullmaktsgivere.map { it.person.ident })
        assertTrue(result.fullmaktsgivere.drop(1).all { it.person.skjulingsaarsak == Persondata.Skjulingsaarsak.UAVKLART })
        assertTrue(RepresentasjonsoppdragFeil.PERSONOPPLYSNINGER in result.feilendeSystemer)
    }

    @Test
    fun `feil i ett repr-oppslag skjuler ikke den andre listen`() {
        every { repr.hentFullmakterForFullmektig(fnr) } throws IOException("utilgjengelig")
        every { repr.hentVergemalForVerge(fnr) } returns listOf(vergemal("00000000001"))
        every { tilgang.sjekkTilganger(any(), any()) } returns emptyMap()

        val result = service.hentRepresentasjonsOppdrag(fnr)

        assertTrue(result.fullmaktsgivere.isEmpty())
        assertEquals(1, result.vergehavere.size)
        assertTrue(RepresentasjonsoppdragFeil.FULLMAKTER in result.feilendeSystemer)
        assertFalse(RepresentasjonsoppdragFeil.VERGEMAL in result.feilendeSystemer)
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

        val result = service.hentRepresentasjonsOppdrag(fnr)

        assertEquals(listOf(100, 100, 5), bolker.map { it.size })
        assertEquals(205, result.fullmaktsgivere.size)
        assertEquals(105, result.fullmaktsgivere.count { it.person.ident != null })
        assertEquals(setOf(RepresentasjonsoppdragFeil.PERSONOPPLYSNINGER), result.feilendeSystemer)
    }

    @Test
    fun `tomme lister gir ingen personoppslag og feil part i repr-respons avvises`() {
        assertTrue(service.hentRepresentasjonsOppdrag(fnr).feilendeSystemer.isEmpty())
        verify(exactly = 0) { tilgang.sjekkTilganger(any(), any()) }
        every { repr.hentFullmakterForFullmektig(fnr) } returns listOf(fullmakt("00000000001").copy(fullmektig = "00000000002"))

        val result = service.hentRepresentasjonsOppdrag(fnr)

        assertTrue(result.fullmaktsgivere.isEmpty())
        assertEquals(setOf(RepresentasjonsoppdragFeil.FULLMAKTER), result.feilendeSystemer)
    }
}
