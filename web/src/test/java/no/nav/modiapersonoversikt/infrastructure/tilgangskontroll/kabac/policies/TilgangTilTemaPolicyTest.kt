package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import io.mockk.every
import io.mockk.mockk
import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.NavIdentPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersTemaPip
import no.nav.modiapersonoversikt.service.ansattservice.AnsattServiceImpl
import no.nav.modiapersonoversikt.service.azure.AzureADService
import no.nav.personoversikt.common.kabac.KabacTestUtils
import org.junit.jupiter.api.Test

internal class TilgangTilTemaPolicyTest {
    private val policy = KabacTestUtils.PolicyTester(TilgangTilTemaPolicy)
    private val nom = mockk<NomClient>()
    private val norgApi = mockk<NorgApi>()
    private val azureADService = mockk<AzureADService>()
    private val ident = NavIdent("Z999999")
    private val enheter = listOf("DAG", "AAP")

    @Test
    internal fun `permit om veileder har tema tilgang`() {
        every { azureADService.hentTemaerForVeileder(ident) } returns enheter
        policy.assertPermit(
            NavIdentPip.key.withValue(ident),
            VeiledersTemaPip(AnsattServiceImpl(norgApi, nom, azureADService)),
            CommonAttributes.ENHET.withValue(EnhetId("0100")),
            CommonAttributes.TEMA.withValue("DAG"),
        )
    }

    @Test
    internal fun `deny om veileder mangler tema tilgang`() {
        every { azureADService.hentTemaerForVeileder(ident) } returns enheter
        policy
            .assertDeny(
                NavIdentPip.key.withValue(ident),
                VeiledersTemaPip(AnsattServiceImpl(norgApi, nom, azureADService)),
                CommonAttributes.ENHET.withValue(EnhetId("0100")),
                CommonAttributes.TEMA.withValue("SYM"),
            ).withMessage("Veileder har ikke tilgang til SYM")
    }
}
