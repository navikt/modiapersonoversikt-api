package no.nav.modiapersonoversikt.service.ansattservice

import io.mockk.every
import io.mockk.mockk
import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.axsys.AxsysEnhet
import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.service.ansattservice.domain.AnsattEnhet
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AnsattServiceImplTest {
    private val nomClient: NomClient = mockk()
    private val axsys: AxsysClient = mockk()
    private val ldap: LDAPService = mockk()
    private val ansattServiceImpl: AnsattServiceImpl = AnsattServiceImpl(axsys, nomClient, ldap)

    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    @Test
    fun `skal hente alle ansatte for en enhet`() {
        every { axsys.hentTilganger(NavIdent("Z994404")) } returns listOf(
            lagNavEnhet("111", "testEnhet"),
            lagNavEnhet("222", "testEnhet2"),
            lagNavEnhet("333", "testEnhet3")
        )

        val enheter: List<AnsattEnhet> = AuthContextTestUtils.withIdent(
            "Z994404",
            UnsafeSupplier {
                ansattServiceImpl.hentEnhetsliste()
            }
        )

        snapshot.assertMatches(enheter)
    }

    @Test
    fun `skal kunne hente navn ansatt`() {
        every { nomClient.finnNavn(NavIdent("111")) } returns lagNavAnsatt("Kalle", "Karlsson", "111")

        val navn = ansattServiceImpl.hentVeileder(NavIdent("111")).navn

        snapshot.assertMatches(navn)
    }

    @Test
    fun `skal kunne hente liste over ansatt sine fagområder`() {
        every { axsys.hentTilganger(NavIdent("Z994404")) } returns listOf(
            lagNavEnhet("111", "enhet123", listOf("AAP", "DAG")),
            lagNavEnhet("222", "testEnhet2", listOf("SYK", "SYM"))
        )

        val fagomraader = ansattServiceImpl.hentAnsattFagomrader("Z994404", "111")

        snapshot.assertMatches(fagomraader)
    }

    @Test
    fun `skal kunne hente ansatte for enhet`() {
        val listeMedNavIder = listOf(NavIdent("111"), NavIdent("222"), NavIdent("333"), NavIdent("444"))

        every { axsys.hentAnsatte(EnhetId("123")) } returns listeMedNavIder
        every { nomClient.finnNavn(listeMedNavIder) } returns listOf(
            lagNavAnsatt("Kalle", "Karlsson", "111"),
            lagNavAnsatt("Klara", "Svensson", "222"),
            lagNavAnsatt("Knut", "Larsson", "333"),
            lagNavAnsatt("Kristina", "Johansson", "444")
        )

        val ansatteListe = ansattServiceImpl.ansatteForEnhet(
            AnsattEnhet(
                "123",
                "testEnhet"
            )
        )

        snapshot.assertMatches(ansatteListe)
    }

    private fun lagNavAnsatt(fornavn: String, etternavn: String, id: String): VeilederNavn {
        return VeilederNavn()
            .setFornavn(fornavn)
            .setEtternavn(etternavn)
            .setNavIdent(NavIdent(id))
            .setVisningsNavn("$fornavn $etternavn")
    }

    private fun lagNavEnhet(enhetsId: String, enhetsNavn: String, temaer: List<String>? = null): AxsysEnhet {
        return AxsysEnhet()
            .setEnhetId(EnhetId(enhetsId))
            .setNavn(enhetsNavn)
            .setTemaer(temaer)
    }
}
