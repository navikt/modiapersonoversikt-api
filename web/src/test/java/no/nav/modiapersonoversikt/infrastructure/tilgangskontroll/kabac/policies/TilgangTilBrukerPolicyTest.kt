package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import io.mockk.every
import io.mockk.mockk
import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.*
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TilgangsMaskinResponse
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.Tilgangsmaskinen
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.NavIdentPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.TilgangsMaskinenPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersEnheterPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import no.nav.modiapersonoversikt.service.ansattservice.AnsattServiceImpl
import no.nav.modiapersonoversikt.service.azure.AzureADService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.KabacTestUtils
import org.junit.jupiter.api.Test

class TilgangTilBrukerPolicyTest {
    private val policy =
        KabacTestUtils.PolicyTester(
            TilgangTilBrukerPolicy,
        )
    private val azureADService = mockk<AzureADService>()
    private val nom = mockk<NomClient>()
    private val norgApi = mockk<NorgApi>()
    private val ansattService = AnsattServiceImpl(norgApi, nom, azureADService)
    private val tilgangsmaskinen = mockk<Tilgangsmaskinen>()

    private val fnr = Fnr("10108000398")
    private val veilederIdent = NavIdent("Z99999")

    @Test
    fun `permit om veileder har tilgang til 0000-ga-modia-oppfolging og brukeren`() {
        every { azureADService.hentRollerForVeileder(veilederIdent) } returns listOf("0000-ga-modia-oppfolging")
        every { azureADService.hentEnheterForVeileder(veilederIdent) } returns listOf(EnhetId("0202"))
        every { tilgangsmaskinen.sjekkTilgang(veilederIdent, fnr) } returns TilgangsMaskinResponse(true)
        policy.assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    fun `permit om veileder har tilgang til bd06_modiagenerelltilgang og brukeren`() {
        every { azureADService.hentRollerForVeileder(veilederIdent) } returns listOf("0000-ga-bd06_modiagenerelltilgang")
        every { azureADService.hentEnheterForVeileder(veilederIdent) } returns listOf(EnhetId("0202"))
        every { tilgangsmaskinen.sjekkTilgang(veilederIdent, fnr) } returns TilgangsMaskinResponse(true)
        policy.assertPermit(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    fun `permit om veileder har tilgang til modia og brukeren men har ingen enhet`() {
        every { azureADService.hentRollerForVeileder(veilederIdent) } returns
            listOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-gosys_nasjonal")
        every { azureADService.hentEnheterForVeileder(veilederIdent) } returns listOf()
        every { tilgangsmaskinen.sjekkTilgang(veilederIdent, fnr) } returns TilgangsMaskinResponse(true)
        policy.assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    fun `permit om veileder har tilgang bruker men ikke til modia og og ingen enhet`() {
        every { azureADService.hentRollerForVeileder(veilederIdent) } returns listOf("annen-rolle")
        every { azureADService.hentEnheterForVeileder(veilederIdent) } returns listOf()
        every { tilgangsmaskinen.sjekkTilgang(veilederIdent, fnr) } returns TilgangsMaskinResponse(true)
        policy.assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    fun `permit om veileder har tilgang til modia og enhet men ikke til brukeren`() {
        every { azureADService.hentRollerForVeileder(veilederIdent) } returns
            listOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-gosys_nasjonal")
        every { azureADService.hentEnheterForVeileder(veilederIdent) } returns listOf(EnhetId("0202"))
        every { tilgangsmaskinen.sjekkTilgang(veilederIdent, fnr) } returns TilgangsMaskinResponse(false)
        policy.assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    @Test
    fun `deny om tilgangsmaskinen feiler`() {
        every { tilgangsmaskinen.sjekkTilgang(veilederIdent, fnr) } returns null
        every { azureADService.hentRollerForVeileder(veilederIdent) } returns
            listOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-gosys_nasjonal")
        every { azureADService.hentEnheterForVeileder(veilederIdent) } returns listOf(EnhetId("0202"))
        policy.assertDeny(*fellesPipTjenester(), CommonAttributes.FNR.withValue(fnr))
    }

    private fun fellesPipTjenester(): Array<Kabac.PolicyInformationPoint<*>> =
        arrayOf(
            NavIdentPip.key.withValue(veilederIdent),
            VeiledersRollerPip(ansattService),
            VeiledersEnheterPip(ansattService),
            TilgangsMaskinenPip(tilgangsmaskinen),
        )
}
