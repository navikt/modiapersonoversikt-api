package no.nav.modiapersonoversikt.consumer.representasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.reprApi.generated.infrastructure.ClientException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ReprApiTest {
    private val path = "/api/v2/internbruker/fullmakt/bruker-som-fullmaktsgiver/alle-fullmakter"

    private fun client(
        status: Int,
        body: String,
        onRequest: (Request) -> Unit = {},
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val request =
                    chain
                        .request()
                        .newBuilder()
                        .header("Authorization", "Bearer test-token")
                        .build()
                onRequest(request)
                Response
                    .Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(status)
                    .message("test")
                    .header("Content-Type", "application/json")
                    .body(body.toResponseBody("application/json".toMediaType()))
                    .build()
            }.build()

    @Test
    fun `henter fullmakter for fullmaktsgiver med ident og OBO-header`() {
        var requested = false
        val client =
            client(
                200,
                """
                [{
                  "fullmaktId": "db40e7d2-44dd-4dab-a954-114513c761d1",
                  "fullmaktsgiver": "12345678910",
                  "fullmektig": "55555666000",
                  "gyldigFraOgMed": "2026-01-01",
                  "leserettigheter": ["SAP"],
                  "skriverettigheter": [],
                  "endringslogg": [{
                    "endringId": 1,
                    "registrert": "2026-09-29T12:39:32.425883",
                    "registrertAv": "system",
                    "kilde": "SYSTEM",
                    "hendelse": "OPPRETTELSE_AV_BRUKER",
                    "gyldigFraOgMed": "2026-01-01",
                    "leserettigheter": ["SAP"],
                    "skriverettigheter": []
                  }]
                }]
                """.trimIndent(),
            ) { request ->
                requested = true
                val buffer = Buffer()
                request.body!!.writeTo(buffer)
                assertEquals("POST", request.method)
                assertEquals(path, request.url.encodedPath)
                assertEquals(1, request.url.queryParameterNames.size)
                assertEquals("""{"ident":"12345678910"}""", buffer.readUtf8())
                assertEquals("Bearer test-token", request.header("Authorization"))
            }

        val result = ReprApiImpl("http://localhost", client).hentfullmakterforfullmaktsgiver(Fnr("12345678910"))

        assertEquals("55555666000", result.single().fullmektig)
        assertEquals(
            1,
            result
                .single()
                .endringslogg
                .single()
                .endringId,
        )
        assertEquals(
            java.time.LocalDateTime.parse("2026-09-29T12:39:32.425883"),
            result
                .single()
                .endringslogg
                .single()
                .registrert,
        )
        assertEquals(true, requested)
    }

    @Test
    fun `feil fra repr-api skal ikke tolkes som tom liste`() {
        val client = client(503, """{"message":"utilgjengelig"}""")

        assertThrows(Exception::class.java) {
            ReprApiImpl("http://localhost", client).hentfullmakterforfullmaktsgiver(Fnr("12345678910"))
        }
    }

    @Test
    fun `404 betyr ingen fullmakter`() {
        val client = client(404, """{"title":"Not Found"}""")

        assertTrue(ReprApiImpl("http://localhost", client).hentfullmakterforfullmaktsgiver(Fnr("12345678910")).isEmpty())
    }

    @Test
    fun `tom 200-liste betyr ingen fullmakter`() {
        val client = client(200, "[]")

        assertTrue(ReprApiImpl("http://localhost", client).hentfullmakterforfullmaktsgiver(Fnr("12345678910")).isEmpty())
    }

    @Test
    fun `andre klientfeil skal ikke tolkes som ingen fullmakter`() {
        val client = client(403, """{"title":"Forbidden"}""")

        assertEquals(
            403,
            assertThrows(ClientException::class.java) {
                ReprApiImpl("http://localhost", client).hentfullmakterforfullmaktsgiver(Fnr("12345678910"))
            }.statusCode,
        )
    }

    @Test
    fun `tom respons fra repr-api skal ikke tolkes som ingen fullmakter`() {
        val client = client(204, "")

        assertThrows(IllegalArgumentException::class.java) {
            ReprApiImpl("http://localhost", client).hentfullmakterforfullmaktsgiver(Fnr("12345678910"))
        }
    }
}
