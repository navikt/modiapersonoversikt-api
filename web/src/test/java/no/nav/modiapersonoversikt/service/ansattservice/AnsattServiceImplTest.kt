package no.nav.modiapersonoversikt.service.ansattservice

import io.mockk.every
import io.mockk.mockk
import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetStatus
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.service.ansattservice.domain.AnsattEnhet
import no.nav.modiapersonoversikt.service.azure.AzureADService
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AnsattServiceImplTest {
    private val nomClient: NomClient = mockk()
    private val norgApi: NorgApi = mockk()
    private val azureADService: AzureADService = mockk()
    private val ansattServiceImpl: AnsattServiceImpl = AnsattServiceImpl(norgApi, nomClient, azureADService)

    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    @Test
    fun `skal hente alle ansatte for en enhet`() {
        every { azureADService.hentEnheterForVeileder(NavIdent("Z994404")) } returns
            listOf(EnhetId("111"), EnhetId("222"), EnhetId("333"))
        every { norgApi.hentEnheter() } returns
            mapOf(
                EnhetId("111") to NorgDomain.Enhet("111", "testEnhet", EnhetStatus.AKTIV, false),
                EnhetId("222") to NorgDomain.Enhet("222", "testEnhet2", EnhetStatus.AKTIV, false),
                EnhetId("333") to NorgDomain.Enhet("333", "testEnhet3", EnhetStatus.AKTIV, false),
            )

        val enheter: List<AnsattEnhet> =
            AuthContextTestUtils.withIdent(
                "Z994404",
                UnsafeSupplier {
                    ansattServiceImpl.hentEnhetsliste()
                },
            )

        snapshot.assertMatches(enheter)
    }

    @Test
    fun `skal kunne hente navn ansatt`() {
        every { nomClient.finnNavn(listOf(NavIdent("111"))) } returns listOf(lagNavAnsatt("Kalle", "Karlsson", "111"))

        val navn = ansattServiceImpl.hentVeileder(NavIdent("111")).navn

        snapshot.assertMatches(navn)
    }

    @Test
    fun `skal kunne hente liste over ansatt sine fagområder`() {
        every { azureADService.hentTemaerForVeileder(NavIdent("Z994404")) } returns
            listOf("AAP", "DAG")

        val fagomraader = ansattServiceImpl.hentAnsattFagomrader("Z994404")

        snapshot.assertMatches(fagomraader)
    }

    @Test
    fun `skal kunne hente ansatte for enhet`() {
        every { azureADService.hentAnsatteForEnhet(EnhetId("123")) } returns
            listOf(
                Ansatt("Kalle", "Karlsson", "111"),
                Ansatt("Klara", "Svensson", "222"),
                Ansatt("Knut", "Larsson", "333"),
                Ansatt("Kristina", "Johansson", "444"),
            )

        val ansatteListe =
            ansattServiceImpl.ansatteForEnhet(
                AnsattEnhet(
                    "123",
                    "testEnhet",
                ),
            )

        snapshot.assertMatches(ansatteListe)
    }

    private fun lagNavAnsatt(
        fornavn: String,
        etternavn: String,
        id: String,
    ): VeilederNavn =
        VeilederNavn()
            .setFornavn(fornavn)
            .setEtternavn(etternavn)
            .setNavIdent(NavIdent(id))
            .setVisningsNavn("$fornavn $etternavn")
}
