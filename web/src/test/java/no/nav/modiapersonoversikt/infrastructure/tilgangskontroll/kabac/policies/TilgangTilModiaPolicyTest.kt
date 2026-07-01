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
import no.nav.modiapersonoversikt.utils.TestUtils
import no.nav.personoversikt.common.kabac.KabacTestUtils
import org.junit.jupiter.api.Test

internal class TilgangTilModiaPolicyTest {
    private val policy = KabacTestUtils.PolicyTester(TilgangTilModiaPolicy)
    private val ansattService = mockk<AnsattService>()
    private val ident = NavIdent("Z999999")

    @Test
    internal fun `permit med modia-generell rolle`() {
        withTestGruppeIder {
            every { ansattService.hentVeilederRoller(ident) } returns RolleListe("uuid-modia-generell")
            policy.assertPermit(
                NavIdentPip.key.withValue(ident),
                VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
                VeiledersRollerPip(ansattService),
            )
        }
    }

    @Test
    internal fun `permit med modia-oppfolging rolle`() {
        withTestGruppeIder {
            every { ansattService.hentVeilederRoller(ident) } returns RolleListe("uuid-modia-oppfolging")
            policy.assertPermit(
                NavIdentPip.key.withValue(ident),
                VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
                VeiledersRollerPip(ansattService),
            )
        }
    }

    @Test
    internal fun `permit med modia-syfo rolle`() {
        withTestGruppeIder {
            every { ansattService.hentVeilederRoller(ident) } returns RolleListe("uuid-syfo-sensitiv")
            policy.assertPermit(
                NavIdentPip.key.withValue(ident),
                VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
                VeiledersRollerPip(ansattService),
            )
        }
    }

    @Test
    internal fun `deny om rolle mangler`() {
        withTestGruppeIder {
            every { ansattService.hentVeilederRoller(ident) } returns RolleListe("annen-uuid")
            policy
                .assertDeny(
                    NavIdentPip.key.withValue(ident),
                    VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
                    VeiledersRollerPip(ansattService),
                ).withMessage("Veileder har ikke tilgang til modia")
        }
    }

    @Test
    internal fun `deny om ingen roller`() {
        withTestGruppeIder {
            every { ansattService.hentVeilederRoller(ident) } returns RolleListe()
            policy
                .assertDeny(
                    NavIdentPip.key.withValue(ident),
                    VeiledersEnheterPip.key.withValue(listOf(EnhetId("1234"))),
                    VeiledersRollerPip(ansattService),
                ).withMessage("Veileder har ikke tilgang til modia")
        }
    }

    @Test
    internal fun `deny om ingen enheter`() {
        withTestGruppeIder {
            every { ansattService.hentVeilederRoller(ident) } returns RolleListe("uuid-syfo-sensitiv")
            policy
                .assertDeny(
                    NavIdentPip.key.withValue(ident),
                    VeiledersEnheterPip.key.withValue(emptyList()),
                    VeiledersRollerPip(ansattService),
                ).withMessage("Veileder har ikke tilgang til noen enheter")
        }
    }
}

internal fun withTestGruppeIder(fn: TestUtils.UnsafeRunneable) {
    fun withProp(name: String, value: String, inner: () -> Unit) {
        val original = System.getProperty(name)
        System.setProperty(name, value)
        try {
            inner()
        } finally {
            if (original == null) {
                System.clearProperty(name)
            } else {
                System.setProperty(name, original)
            }
        }
    }
    withProp("MODIA_GENERELL_TILGANG_ID", "uuid-modia-generell") {
        withProp("MODIA_OPPFOLGING_ID", "uuid-modia-oppfolging") {
            withProp("SYFO_SENSITIV_ID", "uuid-syfo-sensitiv") {
                withProp("STRENGT_FORTROLIG_ADRESSE_ID", "uuid-strengt-fortrolig") {
                    withProp("FORTROLIG_ADRESSE_ID", "uuid-fortrolig") {
                        withProp("EGNE_ANSATTE_ID", "uuid-egne-ansatte") {
                            fn.call()
                        }
                    }
                }
            }
        }
    }
}
