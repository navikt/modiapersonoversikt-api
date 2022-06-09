package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import kotlinx.coroutines.*
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.impl.PolicyDecisionPointImpl
import no.nav.modiapersonoversikt.infrastructure.kabac.impl.PolicyEnforcementPointImpl
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import org.junit.jupiter.api.Test

internal class TilgangskontrollKabacTest {
    private val pip = object : Kabac.PolicyInformationPoint<RolleListe> {
        override fun provide(ctx: Kabac.EvaluationContext): RolleListe {
            return RolleListe("0000-ga-bd06_modiagenerelltilgang")
        }

        override val key: Key<RolleListe> = VeiledersRollerPip.key
    }
    private val pdp = PolicyDecisionPointImpl().install(pip)
    private val pep = PolicyEnforcementPointImpl(policyDecisionPoint = pdp)
    private val tilgangskontroll: Tilgangskontroll = TilgangskontrollKabac(
        enforcementPoint = pep,
        noAccessHandler = { throw IllegalStateException("") }
    )

    @Test
    internal fun `should be able to run in parallell`(): Unit = runBlocking {
        inParallell(times = 64) {
            tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.skipAuditLog) {
                    "Some content"
                }
        }
    }

    suspend fun <T> inParallell(times: Int, task: () -> T): List<T> {
        return coroutineScope {
            buildList {
                repeat(times) {
                    add(
                        async(Dispatchers.IO) {
                            task()
                        }
                    )
                }
            }.awaitAll()
        }
    }
}
