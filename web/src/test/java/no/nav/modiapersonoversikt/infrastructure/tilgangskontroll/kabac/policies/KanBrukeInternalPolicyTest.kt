package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.AuthContextPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.InternalTilgangPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.NavIdentPip
import no.nav.modiapersonoversikt.testutils.AuthContextExtension
import no.nav.modiapersonoversikt.utils.TestUtils
import no.nav.personoversikt.common.kabac.KabacTestUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class KanBrukeInternalPolicyTest {
    private val policy = KabacTestUtils.PolicyTester(KanBrukeInternalPolicy)

    companion object {
        @JvmField
        @RegisterExtension
        val subject =
            AuthContextExtension(
                AuthContext(
                    UserRole.INTERN,
                    PlainJWT(JWTClaimsSet.Builder().subject("Z000001").build()),
                ),
            )
    }

    @Test
    internal fun `permit om identen ligger i godkjent liste`() {
        TestUtils.withEnv("INTERNAL_TILGANG", "Z000001,Z000003") {
            policy.assertPermit(
                AuthContextPip,
                NavIdentPip,
                InternalTilgangPip(),
            )
        }
    }

    @Test
    internal fun `deny om idente ikke ligger i godkjent liste`() {
        subject.setContext(
            AuthContext(
                UserRole.INTERN,
                PlainJWT(JWTClaimsSet.Builder().subject("Z000002").build()),
            ),
        )

        TestUtils.withEnv("INTERNAL_TILGANG", "Z000001,Z000003") {
            policy.assertDeny(
                AuthContextPip,
                NavIdentPip,
                InternalTilgangPip(),
            ).withMessage("Veileder har ikke tilgang til internal")
        }
    }
}
