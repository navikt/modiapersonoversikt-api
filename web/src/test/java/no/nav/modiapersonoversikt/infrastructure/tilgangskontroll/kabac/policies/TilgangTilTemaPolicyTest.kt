package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import io.mockk.every
import io.mockk.mockk
import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.axsys.AxsysEnhet
import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
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
    private val axsys = mockk<AxsysClient>()
    private val azureADService = mockk<AzureADService>()
    private val ident = NavIdent("Z999999")
    private val axsysEnhet = AxsysEnhet().setEnhetId(EnhetId("0100")).setTemaer(listOf("DAG", "AAP"))

    @Test
    internal fun `permit om veileder har tema tilgang`() {
        every { axsys.hentTilganger(ident) } returns listOf(axsysEnhet)
        policy.assertPermit(
            NavIdentPip.key.withValue(ident),
            VeiledersTemaPip(AnsattServiceImpl(axsys, nom, azureADService)),
            CommonAttributes.ENHET.withValue(EnhetId("0100")),
            CommonAttributes.TEMA.withValue("DAG"),
        )
    }

    @Test
    internal fun `deny om veileder mangler tema tilgang`() {
        every { axsys.hentTilganger(ident) } returns listOf(axsysEnhet)
        policy
            .assertDeny(
                NavIdentPip.key.withValue(ident),
                VeiledersTemaPip(AnsattServiceImpl(axsys, nom, azureADService)),
                CommonAttributes.ENHET.withValue(EnhetId("0100")),
                CommonAttributes.TEMA.withValue("SYM"),
            ).withMessage("Veileder har ikke tilgang til SYM")
    }
}
