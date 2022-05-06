package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.infrastructure.kabac.KabacTestUtils
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.NavIdentPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import org.junit.jupiter.api.Test

internal class TilgangTilModiaPolicyTest {
    private val policy = KabacTestUtils.PolicyTester(PublicPolicies.tilgangTilModia)
    private val ldap = mockk<LDAPService>()
    private val ident = NavIdent("Z999999")

    @Test
    internal fun `permit med modia-generell rolle`() {
        every { ldap.hentRollerForVeileder(ident) } returns listOf("0000-ga-bd06_modiagenerelltilgang")
        policy.assertPermit(
            NavIdentPip.key.withValue(ident),
            VeiledersRollerPip(ldap),
        )
    }

    @Test
    internal fun `permit med modia-oppfolging rolle`() {
        every { ldap.hentRollerForVeileder(ident) } returns listOf("0000-ga-modia-oppfolging")
        policy.assertPermit(
            NavIdentPip.key.withValue(ident),
            VeiledersRollerPip(ldap),
        )
    }

    @Test
    internal fun `permit med modia-syfo rolle`() {
        every { ldap.hentRollerForVeileder(ident) } returns listOf("0000-ga-syfo-sensitiv")
        policy.assertPermit(
            NavIdentPip.key.withValue(ident),
            VeiledersRollerPip(ldap),
        )
    }

    @Test
    internal fun `deny om rolle manger`() {
        every { ldap.hentRollerForVeileder(ident) } returns listOf("annen-rolle")
        policy.assertDeny(
            NavIdentPip.key.withValue(ident),
            VeiledersRollerPip(ldap),
        ).withMessage("Veileder har ikke tilgang til modia")
    }

    @Test
    internal fun `deny om ingen roller`() {
        every { ldap.hentRollerForVeileder(ident) } returns emptyList()
        policy.assertDeny(
            NavIdentPip.key.withValue(ident),
            VeiledersRollerPip(ldap),
        ).withMessage("Veileder har ikke tilgang til modia")
    }
}
