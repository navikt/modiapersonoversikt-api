package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.NavIdentPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersEnheterPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.personoversikt.common.kabac.KabacTestUtils
import org.junit.jupiter.api.Test

internal class TilgangTilModiaPolicyTest {
    private val policy = KabacTestUtils.PolicyTester(TilgangTilModiaPolicy)
    private val ansattService = mockk<AnsattService>()
    private val ident = NavIdent("Z999999")

    @Test
    internal fun `permit med modia-generell rolle`() {
        every { ansattService.hentVeilederRoller(ident) } returns RolleListe("0000-ga-bd06_modiagenerelltilgang")
        policy.assertPermit(
            NavIdentPip.key.withValue(ident),
            VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
            VeiledersRollerPip(ansattService),
        )
    }

    @Test
    internal fun `permit med modia-oppfolging rolle`() {
        every { ansattService.hentVeilederRoller(ident) } returns RolleListe("0000-ga-modia-oppfolging")
        policy.assertPermit(
            NavIdentPip.key.withValue(ident),
            VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
            VeiledersRollerPip(ansattService),
        )
    }

    @Test
    internal fun `permit med modia-syfo rolle`() {
        every { ansattService.hentVeilederRoller(ident) } returns RolleListe("0000-ga-syfo-sensitiv")
        policy.assertPermit(
            NavIdentPip.key.withValue(ident),
            VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
            VeiledersRollerPip(ansattService),
        )
    }

    @Test
    internal fun `deny om rolle manger`() {
        every { ansattService.hentVeilederRoller(ident) } returns RolleListe("annen-rolle")
        policy
            .assertDeny(
                NavIdentPip.key.withValue(ident),
                VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
                VeiledersRollerPip(ansattService),
            ).withMessage("Veileder har ikke tilgang til modia")
    }

    @Test
    internal fun `deny om ingen roller`() {
        every { ansattService.hentVeilederRoller(ident) } returns RolleListe()
        policy
            .assertDeny(
                NavIdentPip.key.withValue(ident),
                VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
                VeiledersRollerPip(ansattService),
            ).withMessage("Veileder har ikke tilgang til modia")
    }

    @Test
    internal fun `deny om ingen enheter`() {
        every { ansattService.hentVeilederRoller(ident) } returns RolleListe("0000-ga-syfo-sensitiv")
        policy
            .assertDeny(
                NavIdentPip.key.withValue(ident),
                VeiledersEnheterPip.key.withValue(emptyList()),
                VeiledersRollerPip(ansattService),
            ).withMessage("Veileder har ikke tilgang til noen enheter")
    }
}
