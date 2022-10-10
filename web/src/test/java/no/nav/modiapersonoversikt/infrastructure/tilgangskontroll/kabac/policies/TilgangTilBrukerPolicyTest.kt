package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.mockk
import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.axsys.AxsysEnhet
import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentAdressebeskyttelse
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.KabacTestUtils
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.*
import no.nav.modiapersonoversikt.service.ansattservice.AnsattServiceImpl
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.junit.jupiter.api.Test
import java.util.*

internal class TilgangTilBrukerPolicyTest {
    private val policy = KabacTestUtils.PolicyTester(TilgangTilBrukerPolicy)
    private val ldap = mockk<LDAPService>()
    private val pdl = mockk<PdlOppslagService>()
    private val norg = mockk<NorgApi>()
    private val nom = mockk<NomClient>()
    private val axsys = mockk<AxsysClient>()
    private val skjermedePersoner = mockk<SkjermedePersonerApi>()
    private val ansattService = AnsattServiceImpl(axsys, nom, ldap)

    private val ident = NavIdent("Z999999")
    private val fnr = Fnr("10108000398")
    private val aktorId = AktorId("987654321987")

    @Test
    internal fun `permit om veileder har nasjonal tilgang`() {
        gittAtBrukerIkkeHarAdressebeskyttelse()
        gittAtBrukerIkkeErSkjermet()
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0202"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang", "0000-ga-gosys_nasjonal")

        policy.assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    internal fun `permit om bruker (FNR) ikke har nav kontor`() {
        gittAtBrukerIkkeHarAdressebeskyttelse()
        gittAtBrukerIkkeErSkjermet()
        gittAtBrukerHarEnhet(null)
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0202"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang")

        policy.assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    internal fun `permit om bruker (AKTOR_ID) ikke har nav kontor`() {
        gittFnrAktorIdMapping(fnr to aktorId)
        gittAtBrukerIkkeHarAdressebeskyttelse()
        gittAtBrukerIkkeErSkjermet()
        gittAtBrukerHarEnhet(null)
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0202"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang")

        policy.assertPermit(*fellesPipTjenester(), CommonAttributes.AKTOR_ID.withValue(aktorId))
    }

    @Test
    internal fun `permit om veileder har tilgang til brukers nav kontor`() {
        gittAtBrukerIkkeHarAdressebeskyttelse()
        gittAtBrukerIkkeErSkjermet()
        gittAtBrukerHarEnhet(EnhetId("0101"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang")
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0101"))

        policy.assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    internal fun `permit om veileder har regional tilgang til brukers nav region kontor`() {
        gittAtBrukerIkkeHarAdressebeskyttelse()
        gittAtBrukerIkkeErSkjermet()
        gittAtBrukerHarEnhet(EnhetId("0101"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang", "0000-ga-gosys_regional")
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0202"))
        gittRegionEnheter(
            EnhetId("0101") to EnhetId("0600"),
            EnhetId("0202") to EnhetId("0600"),
        )

        policy.assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))

        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang")
        policy
            .assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
            .withMessage("Veileder har ikke tilgang til bruker basert på geografisk tilgang")
    }

    @Test
    internal fun `deny i alle andre tilfeller`() {
        gittAtBrukerIkkeHarAdressebeskyttelse()
        gittAtBrukerIkkeErSkjermet()
        gittAtBrukerHarEnhet(EnhetId("0101"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang", "0000-ga-gosys_regional")
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0202"))
        gittRegionEnheter(
            EnhetId("0101") to EnhetId("0600"),
            EnhetId("0202") to EnhetId("0601"),
        )

        policy
            .assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
            .withMessage("Veileder har ikke tilgang til bruker basert på geografisk tilgang")
    }

    @Test
    internal fun `deny om veileder mangler tilgang til modia`() {
        gittAtVeilederHarRoller("annen-rolle")
        policy
            .assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
            .withMessage("Veileder har ikke tilgang til modia")
    }

    @Test
    internal fun `deny om veileder mangler tilgang til skjermet bruker`() {
        gittAtBrukerIkkeHarAdressebeskyttelse()
        gittAtBrukerErSkjermet()
        gittAtBrukerHarEnhet(null)
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0202"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang")

        policy
            .assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
            .withMessage("Veileder har ikke tilgang til skjermet person")

        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang", "0000-ga-gosys_utvidet")
        policy
            .assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    internal fun `deny om veileder mangler tilgang til bruker med kode6`() {
        gittAtBrukerHarKode6()
        gittAtBrukerIkkeErSkjermet()
        gittAtBrukerHarEnhet(null)
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0202"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang")

        policy
            .assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
            .withMessage("Veileder har ikke tilgang til kode6")

        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang", "0000-ga-strengt_fortrolig_adresse")
        policy
            .assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    internal fun `deny om veileder mangler tilgang til bruker med kode7`() {
        gittAtBrukerHarKode7()
        gittAtBrukerIkkeErSkjermet()
        gittAtBrukerHarEnhet(null)
        gittAtVeilederHarTilgangTilEnhet(EnhetId("0202"))
        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang")

        policy
            .assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
            .withMessage("Veileder har ikke tilgang til kode7")

        gittAtVeilederHarRoller("0000-ga-bd06_modiagenerelltilgang", "0000-ga-fortrolig_adresse")
        policy
            .assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    private fun gittAtBrukerIkkeHarAdressebeskyttelse() {
        every { pdl.hentAdressebeskyttelse(fnr.get()) } returns listOf(
            HentAdressebeskyttelse.Adressebeskyttelse(
                HentAdressebeskyttelse.AdressebeskyttelseGradering.UGRADERT
            )
        )
    }

    private fun gittAtBrukerIkkeErSkjermet() {
        every { skjermedePersoner.erSkjermetPerson(fnr) } returns false
    }

    private fun gittAtBrukerErSkjermet() {
        every { skjermedePersoner.erSkjermetPerson(fnr) } returns true
    }

    private fun gittAtBrukerHarKode6() {
        every { pdl.hentAdressebeskyttelse(fnr.get()) } returns listOf(
            HentAdressebeskyttelse.Adressebeskyttelse(
                HentAdressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG
            )
        )
    }

    private fun gittAtBrukerHarKode7() {
        every { pdl.hentAdressebeskyttelse(fnr.get()) } returns listOf(
            HentAdressebeskyttelse.Adressebeskyttelse(
                HentAdressebeskyttelse.AdressebeskyttelseGradering.FORTROLIG
            )
        )
    }

    private fun gittAtBrukerHarEnhet(enhetId: EnhetId?) {
        val geografiskTilknyttning = UUID.randomUUID().toStr()
        every { pdl.hentGeografiskTilknyttning(fnr.get()) } returns geografiskTilknyttning

        if (enhetId == null) {
            every { norg.finnNavKontor(geografiskTilknyttning, null) } returns null
        } else {
            every { norg.finnNavKontor(geografiskTilknyttning, null) } returns NorgDomain.Enhet(
                enhetId = enhetId.get(),
                enhetNavn = "Navn",
                status = NorgDomain.EnhetStatus.AKTIV,
                oppgavebehandler = false
            )
        }
    }

    private fun gittAtVeilederHarRoller(vararg roller: String) {
        every { ldap.hentRollerForVeileder(ident) } returns roller.toList()
    }

    private fun gittAtVeilederHarTilgangTilEnhet(enhetId: EnhetId) {
        every { axsys.hentTilganger(ident) } returns listOf(AxsysEnhet().setEnhetId(enhetId))
    }

    private fun gittFnrAktorIdMapping(vararg fnraktoridMapping: Pair<Fnr, AktorId>) {
        val fnrmap = fnraktoridMapping.toMap()
        val aktormap = fnraktoridMapping.associate { Pair(it.second, it.first) }
        every { pdl.hentFnr(any()) } answers {
            aktormap[AktorId(arg<String>(0))]?.get()
        }
        every { pdl.hentAktorId(any()) } answers {
            fnrmap[Fnr(arg<String>(0))]?.get()
        }
    }

    private fun gittRegionEnheter(vararg regionEnhetMapping: Pair<EnhetId, EnhetId>) {
        val regionmap = regionEnhetMapping.toMap()
        every { norg.hentRegionalEnhet(any()) } answers {
            val enhetId = arg<EnhetId>(0)
            regionmap[enhetId]
        }
        every { norg.hentRegionalEnheter(any()) } answers {
            val enhetIder = arg<List<EnhetId>>(0)
            enhetIder.mapNotNull { regionmap[it] }
        }
    }

    private fun fellesPipTjenester(): Array<Kabac.PolicyInformationPoint<*>> {
        return arrayOf(
            NavIdentPip.key.withValue(ident),
            BrukersAktorIdPip(pdl),
            BrukersFnrPip(pdl),
            BrukersGeografiskeTilknyttningPip(pdl),
            BrukersEnhetPip(norg),
            BrukersDiskresjonskodePip(pdl),
            BrukersSkjermingPip(skjermedePersoner),
            BrukersRegionEnhetPip(norg),
            VeiledersRollerPip(ansattService),
            VeiledersEnheterPip(ansattService),
            VeiledersRegionEnheterPip(norg)
        )
    }
}
