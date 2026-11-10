package no.nav.modiapersonoversikt.rest.persondata

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.HentPersonBolkResult
import no.nav.modiapersonoversikt.consumer.representasjon.RepresentasjonApi
import no.nav.modiapersonoversikt.consumer.representasjon.generated.infrastructure.Serializer
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.Tilgangsavslag
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.Tilgangsmaskinen
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgang
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgangResultat
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.persondata.Persondata
import no.nav.modiapersonoversikt.service.persondata.PersondataResult
import no.nav.modiapersonoversikt.service.persondata.PersondataServiceImpl
import no.nav.modiapersonoversikt.testutils.AuthContextRule
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

internal class PersondataServiceImplTest {
    @Rule
    @JvmField
    val subject = AuthContextRule(AuthContext(UserRole.INTERN, PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())))

    private val ugyldigGT = "0301"
    private val gyldigGT = "030101"

    private val norgApi: NorgApi = mockk()
    private val pdl: PdlOppslagService = mockk()
    private val tilgangsmaskinen: Tilgangsmaskinen = mockk()
    private val representasjonApi: RepresentasjonApi = mockk()
    private val krrService: Krr.Service = mockk(relaxed = true)
    private val kodeverk: EnhetligKodeverk.Service = mockk()
    private val persondataServiceImpl =
        PersondataServiceImpl(
            norgApi = norgApi,
            pdl = pdl,
            krrService = krrService,
            kontonummerService = mockk(),
            skjermedePersonerApi = mockk(),
            oppfolgingService = mockk(),
            policyEnforcementPoint = mockk(),
            kodeverk = kodeverk,
            representasjonApi = representasjonApi,
            tjenestekallLogger = TjenestekallLogg,
            tilgangsmaskinen = tilgangsmaskinen,
        )

    @Test
    internal fun `skal filtrere vekk ugyldig gt`() {
        val navEnhet =
            persondataServiceImpl.hentNavEnhetFraNorg(
                adressebeskyttelse = emptyList(),
                geografiskeTilknytning = PersondataResult.of(ugyldigGT),
            )

        assertTrue(navEnhet is PersondataResult.NotRelevant<*>)
        verify(exactly = 0) { norgApi.finnNavKontor(any(), any()) }
        verify(exactly = 0) { norgApi.hentKontaktinfo(any()) }
    }

    @Test
    internal fun `skal gi navEnhet`() {
        every { norgApi.finnNavKontor(any(), any())?.enhetId } returns "0123"
        every { norgApi.hentKontaktinfo(EnhetId("0123")) } returns gittNavKontorEnhet()

        val navEnhet =
            persondataServiceImpl.hentNavEnhetFraNorg(
                adressebeskyttelse = emptyList(),
                geografiskeTilknytning = PersondataResult.of(gyldigGT),
            )

        assertTrue(navEnhet is PersondataResult.Success<*>)
        verify(exactly = 1) { norgApi.finnNavKontor(any(), any()) }
        verify(exactly = 1) { norgApi.hentKontaktinfo(any()) }
    }

    @Test
    internal fun `representasjon-fullmektige skal slaas opp som tredjeparter uten duplikater`() {
        val repr = PersondataResult.of(listOf(fullmaktPerson, fullmaktPerson.copy(fullmektig = "99999999999")))

        assertEquals(listOf("55555666000", "99999999999"), persondataServiceImpl.finnFullmektigIdenter(repr))
    }

    @Test
    fun `avslag for en tredjeperson blokkerer ikke godkjente personer`() {
        val tillatt = fullmaktPerson.fullmektig
        val avvist = "99999999999"
        val mangler = "88888888888"
        gittPersonMedFullmektige(tillatt, avvist, mangler)
        every { tilgangsmaskinen.sjekkTilganger(any(), any()) } returns
            mapOf(
                tillatt to TredjepersonTilgangResultat(TredjepersonTilgang.GODKJENT),
                avvist to TredjepersonTilgangResultat(TredjepersonTilgang.AVVIST),
                mangler to TredjepersonTilgangResultat(TredjepersonTilgang.GODKJENT),
            )
        every { pdl.hentTredjepartspersondata(any()) } returns
            listOf(
                mockk<HentPersonBolkResult> {
                    every { ident } returns tillatt
                    every { person } returns mockk(relaxed = true)
                },
            )

        val result = persondataServiceImpl.hentPerson("12345678910")

        assertEquals(listOf(tillatt, "", ""), result.person.fullmektige.map { it.ident })
        assertEquals(
            listOf(null, Persondata.Skjulingsaarsak.MANGLER_TILGANG, Persondata.Skjulingsaarsak.UAVKLART),
            result.person.fullmektige.map { it.skjulingsaarsak },
        )
        verify { pdl.hentTredjepartspersondata(listOf(tillatt, mangler)) }
        verify(exactly = 0) { krrService.hentDigitalKontaktinformasjon(avvist) }
        verify(exactly = 0) { krrService.hentDigitalKontaktinformasjon(mangler) }
        verify(exactly = 1) { krrService.hentDigitalKontaktinformasjon(tillatt) }
    }

    @Test
    fun `avviste og uavklarte fullmektige hentes ikke fra PDL eller KRR`() {
        val avvist = fullmaktPerson.fullmektig
        val uavklart = "99999999999"
        val avslag = Tilgangsavslag("AVVIST_SKJERMING", "Du har ikke tilgang til skjermet person")
        gittPersonMedFullmektige(avvist, uavklart)
        every { tilgangsmaskinen.sjekkTilganger(any(), any()) } returns
            mapOf(
                avvist to TredjepersonTilgangResultat(TredjepersonTilgang.AVVIST, avslag),
                uavklart to TredjepersonTilgangResultat(TredjepersonTilgang.UAVKLART, avslag),
            )

        val result = persondataServiceImpl.hentPerson("12345678910")

        assertEquals(listOf("", ""), result.person.fullmektige.map { it.ident })
        assertEquals(listOf(avslag, null), result.person.fullmektige.map { it.tilgangsavslag })
        val json = Serializer.jacksonObjectMapper.writeValueAsString(result)
        assertFalse(json.contains("tredjepersonTilgang"))
        assertFalse(json.contains("skjermedeRelasjoner"))
        assertEquals(
            listOf(forventetFullmakt, forventetFullmakt.copy(fullmektig = uavklart)),
            result.person.fullmektige.map { it.fullmakt },
        )
        assertTrue(result.person.fullmektige.all { it.navn == null && it.digitalKontaktinformasjonTredjepartsperson == null })
        assertEquals(
            listOf(Persondata.Skjulingsaarsak.MANGLER_TILGANG, Persondata.Skjulingsaarsak.UAVKLART),
            result.person.fullmektige.map { it.skjulingsaarsak },
        )
        assertTrue(PersondataResult.InformasjonElement.TILGANGSMASKINEN_TREDJEPARTSPERSONER in result.feilendeSystemer)
        verify(exactly = 0) { pdl.hentTredjepartspersondata(any()) }
        verify(exactly = 0) { krrService.hentDigitalKontaktinformasjon(avvist) }
        verify(exactly = 0) { krrService.hentDigitalKontaktinformasjon(uavklart) }
    }

    @Test
    fun `beholder innebygde relasjonsdata uten a hente avviste og uavklarte personer`() {
        val tredjeparter =
            listOf(
                fullmaktPerson.fullmektig,
                sivilstandPerson.relatertVedSivilstand!!,
                foreldreansvarData.ansvarlig!!,
                foreldreansvarData.ansvarssubjekt!!,
                kontaktinformasjonDodsbo.personSomKontakt!!.identifikasjonsnummer!!,
                vergemal.vergeEllerFullmektig.motpartsPersonident!!,
                historiskVergemal.vergeEllerFullmektig.motpartsPersonident!!,
                *forelderBarnRelasjonData.mapNotNull { it.relatertPersonsIdent }.toTypedArray(),
            ).distinct()
        gittPersonMedFullmektige(*tredjeparter.toTypedArray(), inkluderRelasjoner = true)

        mapOf(
            TredjepersonTilgang.AVVIST to Persondata.Skjulingsaarsak.MANGLER_TILGANG,
            TredjepersonTilgang.UAVKLART to Persondata.Skjulingsaarsak.UAVKLART,
        ).forEach { (tilgang, aarsak) ->
            every { tilgangsmaskinen.sjekkTilganger(any(), any()) } returns
                tredjeparter.associateWith { TredjepersonTilgangResultat(tilgang) }

            val result = persondataServiceImpl.hentPerson("12345678910")
            val person = result.person

            assertEquals(tredjeparter.size, person.fullmektige.size)
            assertTrue(person.fullmektige.all { it.ident.isEmpty() && it.skjulingsaarsak == aarsak })
            assertEquals(
                aarsak,
                person.sivilstand
                    .single()
                    .sivilstandRelasjon
                    ?.skjulingsaarsak,
            )
            assertEquals(
                aarsak,
                person.foreldreansvar
                    .single()
                    .ansvarlig
                    ?.skjulingsaarsak,
            )
            assertEquals(
                aarsak,
                person.foreldreansvar
                    .single()
                    .ansvarsubject
                    ?.skjulingsaarsak,
            )
            assertEquals(
                aarsak,
                person.dodsbo
                    .single()
                    .adressat.personSomAdressat
                    ?.skjulingsaarsak,
            )
            assertEquals(
                "Gateveien",
                person.dodsbo
                    .single()
                    .adresse.linje1,
            )
            assertEquals(gittNavn("Person Vergemål"), person.vergemal.single().navn)
            assertEquals(gittNavn("Person HistoriskVergemål"), person.historiskeVergemal.single().navn)
            assertEquals(null, person.vergemal.single().skjulingsaarsak)
            assertEquals(null, person.historiskeVergemal.single().skjulingsaarsak)
            val barn = person.forelderBarnRelasjon.filter { it.rolle == Persondata.ForelderBarnRelasjonRolle.BARN }
            assertEquals(
                forelderBarnRelasjonData.count { !it.relatertPersonsIdent.isNullOrBlank() },
                barn.size,
            )
            assertTrue(barn.all { it.skjulingsaarsak == aarsak })
            assertEquals(
                null,
                person.forelderBarnRelasjon
                    .single { it.rolle == Persondata.ForelderBarnRelasjonRolle.MEDMOR }
                    .skjulingsaarsak,
            )
            val json = Serializer.jacksonObjectMapper.writeValueAsString(person.copy(fullmektige = emptyList()))
            tredjeparter.forEach { assertFalse(json.contains(it), "Skjult ident finnes i responsen") }
            assertFalse(json.contains("tredjepersonTilgang"))
            assertFalse(json.contains("skjermedeRelasjoner"))
        }
        verify(exactly = 0) { pdl.hentTredjepartspersondata(any()) }
        tredjeparter.forEach { verify(exactly = 0) { krrService.hentDigitalKontaktinformasjon(it) } }
    }

    @Test
    fun `godkjenning uten PDL-treff beholder innebygde vergenavn og dodsboadresse`() {
        gittPersonMedFullmektige(inkluderRelasjoner = true)
        every { tilgangsmaskinen.sjekkTilganger(any(), any()) } answers {
            secondArg<List<String>>().associateWith { TredjepersonTilgangResultat(TredjepersonTilgang.GODKJENT) }
        }
        every { pdl.hentTredjepartspersondata(any()) } returns emptyList()

        val person = persondataServiceImpl.hentPerson("12345678910").person

        assertEquals(gittNavn("Person Vergemål"), person.vergemal.single().navn)
        assertEquals(gittNavn("Person HistoriskVergemål"), person.historiskeVergemal.single().navn)
        assertEquals(null, person.vergemal.single().ident)
        assertEquals(null, person.vergemal.single().skjulingsaarsak)
        assertEquals(
            "Gateveien",
            person.dodsbo
                .single()
                .adresse.linje1,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.UAVKLART,
            person.dodsbo
                .single()
                .adressat.personSomAdressat
                ?.skjulingsaarsak,
        )
        verify(exactly = 1) { pdl.hentTredjepartspersondata(any()) }
    }

    @Test
    fun `godkjenning uten entydig person i PDL beholder fullmaktsdata`() {
        val forespurt = fullmaktPerson.fullmektig
        gittPersonMedFullmektige(forespurt)
        every { tilgangsmaskinen.sjekkTilganger(any(), any()) } returns
            mapOf(forespurt to TredjepersonTilgangResultat(TredjepersonTilgang.GODKJENT))
        val pdlPerson =
            mockk<HentPersonBolkResult> {
                every { ident } returns forespurt
                every { person } returns mockk(relaxed = true)
            }
        val nullPerson =
            mockk<HentPersonBolkResult> {
                every { ident } returns forespurt
                every { person } returns null
            }
        val feilIdent =
            mockk<HentPersonBolkResult> {
                every { ident } returns "99999999999"
                every { person } returns mockk(relaxed = true)
            }
        listOf(emptyList(), listOf(nullPerson), listOf(feilIdent), listOf(pdlPerson, pdlPerson)).forEach { svar ->
            every { pdl.hentTredjepartspersondata(any()) } returns svar
            val result = persondataServiceImpl.hentPerson("12345678910")
            assertEquals(
                "",
                result.person.fullmektige
                    .single()
                    .ident,
            )
            assertEquals(
                forventetFullmakt,
                result.person.fullmektige
                    .single()
                    .fullmakt,
            )
            assertEquals(
                Persondata.Skjulingsaarsak.UAVKLART,
                result.person.fullmektige
                    .single()
                    .skjulingsaarsak,
            )
        }
        verify(exactly = 0) { krrService.hentDigitalKontaktinformasjon(forespurt) }
    }

    private fun gittPersonMedFullmektige(
        vararg identer: String,
        inkluderRelasjoner: Boolean = false,
    ) {
        every { kodeverk.hentKodeverk<String, String>(any()) } returns gittKodeverk()
        every { pdl.hentPersondata(any()) } returns
            mockk<HentPersondata.Result> {
                every { hentPerson } returns
                    if (inkluderRelasjoner) {
                        testPerson
                    } else {
                        testPerson.copy(
                            vergemaalEllerFremtidsfullmakt = emptyList(),
                            foreldreansvar = emptyList(),
                            sivilstand = emptyList(),
                            forelderBarnRelasjon = emptyList(),
                            kontaktinformasjonForDoedsbo = emptyList(),
                        )
                    }
                every { hentGeografiskTilknytning } returns null
            }
        every { representasjonApi.hentFullmakterForFullmaktsgiver(any()) } returns
            identer.map { fullmaktPerson.copy(fullmektig = it) }
    }
}
