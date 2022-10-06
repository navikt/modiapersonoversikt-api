package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import kotlinx.coroutines.*
import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.impl.PolicyDecisionPointImpl
import no.nav.modiapersonoversikt.infrastructure.kabac.impl.PolicyEnforcementPointImpl
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersEnheterPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import org.junit.jupiter.api.Test

internal class TilgangskontrollKabacTest {
    private val rollelistePip = object : Kabac.PolicyInformationPoint<RolleListe> {
        override fun provide(ctx: Kabac.EvaluationContext): RolleListe {
            return RolleListe("0000-ga-bd06_modiagenerelltilgang")
        }

        override val key: Key<RolleListe> = VeiledersRollerPip.key
    }
    private val enheterPip = object : Kabac.PolicyInformationPoint<List<EnhetId>> {
        override fun provide(ctx: Kabac.EvaluationContext): List<EnhetId> {
            return listOf(EnhetId("1234"))
        }

        override val key: Key<List<EnhetId>> = VeiledersEnheterPip.key
    }
    private val pdp = PolicyDecisionPointImpl().install(rollelistePip).install(enheterPip)
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

    private suspend fun <T> inParallell(times: Int, task: () -> T): List<T> {
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
