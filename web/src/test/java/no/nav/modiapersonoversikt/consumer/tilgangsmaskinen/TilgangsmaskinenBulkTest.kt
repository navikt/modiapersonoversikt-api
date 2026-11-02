package no.nav.modiapersonoversikt.consumer.tilgangsmaskinen

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgang.AVVIST
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgang.GODKJENT
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgang.UAVKLART
import no.nav.modiapersonoversikt.utils.WireMockUtils.json
import no.nav.modiapersonoversikt.utils.WireMockUtils.post
import no.nav.modiapersonoversikt.utils.WireMockUtils.status
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.OkHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TilgangsmaskinenBulkTest {
    companion object {
        @JvmField
        @RegisterExtension
        val wiremock: WireMockExtension = WireMockExtension.newInstance().build()

        private const val VEILEDER = "Z999999"
        private const val BULK_URL = "/api/v1/bulk/ccf/$VEILEDER/KJERNE_REGELTYPE"
    }

    private val objectMapper = jacksonObjectMapper()
    private val logger = mockk<TjenestekallLogger>(relaxed = true)
    private val tilgangsmaskinen: Tilgangsmaskinen =
        TilgangsmaskinenImpl("http://localhost:${wiremock.port}", OkHttpClient(), logger, objectMapper)

    private fun fnr(nr: Int) = nr.toString().padStart(11, '0')

    private fun resultater(
        ansattId: String = VEILEDER,
        vararg resultater: Pair<String, Int>,
    ): String =
        objectMapper.writeValueAsString(
            mapOf(
                "ansattId" to ansattId,
                "resultater" to resultater.map { (brukerId, status) -> mapOf("brukerId" to brukerId, "status" to status) },
            ),
        )

    private fun sjekk(vararg fnrs: String) = tilgangsmaskinen.sjekkTilganger(NavIdent(VEILEDER), fnrs.toList())

    private fun mottatteBolker(): List<List<String>> =
        wiremock.findAll(postRequestedFor(urlEqualTo(BULK_URL))).map { objectMapper.readValue(it.bodyAsString) }

    @Test
    fun `207 med blandede statuser mappes til GODKJENT, AVVIST og UAVKLART`() {
        wiremock.post {
            status(207)
            json(resultater(VEILEDER, fnr(1) to 204, fnr(2) to 403, fnr(3) to 404, fnr(4) to 500))
        }

        val resultat = sjekk(fnr(1), fnr(2), fnr(3), fnr(4))

        assertThat(resultat).isEqualTo(mapOf(fnr(1) to GODKJENT, fnr(2) to AVVIST, fnr(3) to UAVKLART, fnr(4) to UAVKLART))
        wiremock.verify(
            postRequestedFor(urlEqualTo(BULK_URL)).withRequestBody(equalToJson("""["${fnr(1)}","${fnr(2)}","${fnr(3)}","${fnr(4)}"]""")),
        )
    }

    @Test
    fun `tom liste gir tomt resultat uten kall`() {
        assertThat(sjekk()).isEmpty()
        assertThat(mottatteBolker()).isEmpty()
    }

    @Test
    fun `dupliserte identer sendes kun en gang`() {
        wiremock.post {
            status(207)
            json(resultater(VEILEDER, fnr(1) to 204, fnr(2) to 403))
        }

        val resultat = sjekk(fnr(1), fnr(2), fnr(1))

        assertThat(resultat).isEqualTo(mapOf(fnr(1) to GODKJENT, fnr(2) to AVVIST))
        assertThat(mottatteBolker()).containsExactly(listOf(fnr(1), fnr(2)))
    }

    @Test
    fun `1000 identer sendes i en bolk`() {
        val fnrs = (1..1000).map(::fnr)
        wiremock.post {
            status(207)
            json(resultater(VEILEDER, *fnrs.map { it to 204 }.toTypedArray()))
        }

        val resultat = tilgangsmaskinen.sjekkTilganger(NavIdent(VEILEDER), fnrs)

        assertThat(mottatteBolker()).containsExactly(fnrs)
        assertThat(resultat).hasSize(1000).allSatisfy { _, tilgang -> assertThat(tilgang).isEqualTo(GODKJENT) }
    }

    @Test
    fun `1001 identer deles i to bolker og feilet bolk beholder resultater fra vellykket bolk`() {
        val fnrs = (1..1001).map(::fnr)
        wiremock.post {
            status(207)
            json(resultater(VEILEDER, *fnrs.take(1000).map { it to 204 }.toTypedArray()))
        }
        wiremock.stubFor(
            WireMock
                .post(urlEqualTo(BULK_URL))
                .atPriority(1)
                .withRequestBody(containing(fnr(1001)))
                .willReturn(aResponse().withStatus(500)),
        )

        val resultat = tilgangsmaskinen.sjekkTilganger(NavIdent(VEILEDER), fnrs)

        assertThat(mottatteBolker()).containsExactlyInAnyOrder(fnrs.take(1000), listOf(fnr(1001)))
        assertThat(resultat).hasSize(1001)
        assertThat(resultat.filterKeys { it != fnr(1001) }.values).containsOnly(GODKJENT)
        assertThat(resultat[fnr(1001)]).isEqualTo(UAVKLART)
        verify {
            logger.error(any(), match { fields -> fields.values.none { it.toString().contains(fnr(1001)) } }, any(), any())
        }
    }

    @Test
    fun `annen status enn 207 gir UAVKLART for hele bolken`() {
        wiremock.post {
            status(200)
            json(resultater(VEILEDER, fnr(1) to 204))
        }

        assertThat(sjekk(fnr(1))).isEqualTo(mapOf(fnr(1) to UAVKLART))
    }

    @Test
    fun `ugyldig json gir UAVKLART for hele bolken`() {
        wiremock.post {
            status(207)
            json("""{"ansattId":"$VEILEDER","resultater":[""")
        }

        assertThat(sjekk(fnr(1), fnr(2))).isEqualTo(mapOf(fnr(1) to UAVKLART, fnr(2) to UAVKLART))
    }

    @Test
    fun `ugyldig responsformat gir UAVKLART for hele bolken`() {
        wiremock.post {
            status(207)
            json(
                """{"ansattId":"$VEILEDER","resultater":[{"brukerId":"${fnr(1)}","status":204},""" +
                    """{"brukerId":"${fnr(2)}","status":"403"}]}""",
            )
        }

        assertThat(sjekk(fnr(1), fnr(2))).isEqualTo(mapOf(fnr(1) to UAVKLART, fnr(2) to UAVKLART))
    }

    @Test
    fun `ansattId som ikke matcher gir UAVKLART for hele bolken`() {
        wiremock.post {
            status(207)
            json(resultater("Z111111", fnr(1) to 204, fnr(2) to 204))
        }

        assertThat(sjekk(fnr(1), fnr(2))).isEqualTo(mapOf(fnr(1) to UAVKLART, fnr(2) to UAVKLART))
    }

    @Test
    fun `manglende, duplikate og ukjente identer gir ikke tilgang`() {
        wiremock.post {
            status(207)
            json(
                resultater(
                    VEILEDER,
                    fnr(1) to 204,
                    fnr(2) to 204,
                    fnr(2) to 403,
                    fnr(3) to 204,
                    fnr(3) to 204,
                    fnr(9) to 204,
                ),
            )
        }

        val resultat = sjekk(fnr(1), fnr(2), fnr(3), fnr(4))

        assertThat(resultat).isEqualTo(mapOf(fnr(1) to GODKJENT, fnr(2) to UAVKLART, fnr(3) to UAVKLART, fnr(4) to UAVKLART))
        assertThat(resultat).doesNotContainKey(fnr(9))
    }
}
