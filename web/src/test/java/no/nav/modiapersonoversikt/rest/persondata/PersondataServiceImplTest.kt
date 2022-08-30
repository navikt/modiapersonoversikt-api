package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.dkif.Dkif
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentTredjepartspersondata
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class PersondataServiceImplTest {
    val pdl: PdlOppslagService = mockk()
    val dkif: Dkif.Service = mockk()
    val norgApi: NorgApi = mockk()
    val personV3: PersonV3 = mockk()
    val skjermedePersonerApi: SkjermedePersonerApi = mockk()
    val policyEnforcementPoint: Kabac.PolicyEnforcementPoint = mockk()
    val kodeverk: EnhetligKodeverk.Service = mockk()

    val persondataServiceImpl = PersondataServiceImpl(pdl, dkif, norgApi, personV3, skjermedePersonerApi, policyEnforcementPoint, kodeverk)
    val fnr = "12345678910"
    val ugyldigGT = "0301"

    @Test
    internal fun `skal filtrere vekk ugyldig gt`() {
        every { skjermedePersonerApi.erSkjermetPerson(Fnr(fnr)) } returns false
        every { dkif.hentDigitalKontaktinformasjon(fnr) } returns digitalKontaktinformasjon
        every { personV3.hentPerson(any()) } returns utenlandskBankkonto
        every { kodeverk.hentKodeverk<String, String>(any()) } returns gittKodeverk()
        every { pdl.hentPersondata(fnr) } returns testPerson
        every { pdl.hentGeografiskTilknyttning(fnr) } returns ugyldigGT
        every { pdl.hentTredjepartspersondata(any()) } returns listOf(
            HentTredjepartspersondata.HentPersonBolkResult(
                ident = fnr,
                person = null
            )
        )
        every { norgApi
            .finnNavKontor(any(), any())
            ?.enhetId
        } returns ugyldigGT

        every { norgApi.hentKontaktinfo(EnhetId(ugyldigGT)) } returns gittNavKontorEnhet()
        val navEnhet = persondataServiceImpl.hentNavEnhetFraNorg(
            adressebeskyttelse = persondataServiceImpl.persondataFletter.hentAdressebeskyttelse(
                testPerson.adressebeskyttelse),
            geografiskeTilknytning = persondataServiceImpl.hentGeografiskTilknyttning(fnr, testPerson)
        ).getOrNull()

        assertEquals(null, navEnhet?.enhet?.enhetId)
    }

    @Test
    internal fun `skal gi navEnhet`() {
        every { skjermedePersonerApi.erSkjermetPerson(Fnr(fnr)) } returns false
        every { dkif.hentDigitalKontaktinformasjon(fnr) } returns digitalKontaktinformasjon
        every { personV3.hentPerson(any()) } returns utenlandskBankkonto
        every { kodeverk.hentKodeverk<String, String>(any()) } returns gittKodeverk()
        every { pdl.hentPersondata(fnr) } returns testPerson
        every { pdl.hentGeografiskTilknyttning(fnr) } returns "0123"
        every { pdl.hentTredjepartspersondata(any()) } returns listOf(
            HentTredjepartspersondata.HentPersonBolkResult(
                ident = fnr,
                person = null
            )
        )
        every { norgApi
            .finnNavKontor(any(), any())
            ?.enhetId
        } returns "0123"
        every { norgApi.hentKontaktinfo(EnhetId("0123")) } returns gittNavKontorEnhet()

        val navEnhet = persondataServiceImpl.hentNavEnhetFraNorg(
            adressebeskyttelse = persondataServiceImpl.persondataFletter.hentAdressebeskyttelse(
                testPerson.adressebeskyttelse),
            geografiskeTilknytning = persondataServiceImpl.hentGeografiskTilknyttning(fnr, testPerson)
        ).getOrNull()

        assertEquals("0123", navEnhet?.enhet?.enhetId)
    }



}