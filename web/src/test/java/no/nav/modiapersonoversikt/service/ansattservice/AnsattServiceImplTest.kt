package no.nav.modiapersonoversikt.service.ansattservice

import io.mockk.every
import io.mockk.mockk
import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.axsys.AxsysEnhet
import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.service.ansattservice.domain.AnsattEnhet
import no.nav.modiapersonoversikt.service.azure.AzureADService
import no.nav.modiapersonoversikt.service.azure.Gruppe
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AnsattServiceImplTest {
    private val nomClient: NomClient = mockk()
    private val axsys: AxsysClient = mockk()
    private val azureADService: AzureADService = mockk()
    private val ansattServiceImpl: AnsattServiceImpl = AnsattServiceImpl(axsys, nomClient, azureADService)

    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    @Test
    fun `skal kunne hente navn ansatt`() {
        every { nomClient.finnNavn(listOf(NavIdent("111"))) } returns listOf(lagNavAnsatt("Kalle", "Karlsson", "111"))

        val navn = ansattServiceImpl.hentVeileder(NavIdent("111")).navn

        snapshot.assertMatches(navn)
    }

    @Test
    fun `skal kunne hente liste over ansatt sine fagområder`() {
        every { axsys.hentTilganger(NavIdent("Z994404")) } returns
            listOf(
                lagNavEnhet("111", "enhet123", listOf("AAP", "DAG")),
                lagNavEnhet("222", "testEnhet2", listOf("SYK", "SYM")),
            )

        val fagomraader = ansattServiceImpl.hentAnsattFagomrader("Z994404", "111")

        snapshot.assertMatches(fagomraader)
    }

    @Test
    fun `skal kunne hente ansatte for enhet`() {
        every { azureADService.hentEnhetGruppe("123") } returns Gruppe("0000_GA_ENHET_123", "123456")

        val listeMedAnsatte =
            listOf(
                Ansatt("Kalle", "Karlsson", "111"),
                Ansatt("Klara", "Svensson", "222"),
                Ansatt("Knut", "Larsson", "333"),
                Ansatt("Kristina", "Johansson", "444"),
            )

        every { azureADService.hentAnsatteForEnhet("123", "123456") } returns listeMedAnsatte

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

    private fun lagNavEnhet(
        enhetsId: String,
        enhetsNavn: String,
        temaer: List<String>? = null,
    ): AxsysEnhet =
        AxsysEnhet()
            .setEnhetId(EnhetId(enhetsId))
            .setNavn(enhetsNavn)
            .setTemaer(temaer)
}
