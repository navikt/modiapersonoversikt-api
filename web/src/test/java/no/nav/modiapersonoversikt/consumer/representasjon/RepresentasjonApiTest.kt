package no.nav.modiapersonoversikt.consumer.representasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.representasjon.generated.infrastructure.ClientException
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

internal class RepresentasjonApiTest {
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

        val result = RepresentasjonApiImpl("http://localhost", client).hentFullmakterForFullmaktsgiver(Fnr("12345678910"))

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
            RepresentasjonApiImpl("http://localhost", client).hentFullmakterForFullmaktsgiver(Fnr("12345678910"))
        }
    }

    @Test
    fun `404 betyr at personen ikke finnes i PDL`() {
        val client = client(404, """{"title":"Not Found"}""")

        assertTrue(RepresentasjonApiImpl("http://localhost", client).hentFullmakterForFullmaktsgiver(Fnr("12345678910")).isEmpty())
    }

    @Test
    fun `tom 200-liste betyr ingen fullmakter`() {
        val client = client(200, "[]")

        assertTrue(RepresentasjonApiImpl("http://localhost", client).hentFullmakterForFullmaktsgiver(Fnr("12345678910")).isEmpty())
    }

    @Test
    fun `andre klientfeil skal ikke tolkes som ingen fullmakter`() {
        val client = client(403, """{"title":"Forbidden"}""")

        assertEquals(
            403,
            assertThrows(ClientException::class.java) {
                RepresentasjonApiImpl("http://localhost", client).hentFullmakterForFullmaktsgiver(Fnr("12345678910"))
            }.statusCode,
        )
    }

    @Test
    fun `tom respons fra repr-api skal ikke tolkes som ingen fullmakter`() {
        val client = client(204, "")

        assertThrows(IllegalArgumentException::class.java) {
            RepresentasjonApiImpl("http://localhost", client).hentFullmakterForFullmaktsgiver(Fnr("12345678910"))
        }
    }

    @Test
    fun `henter fullmakter motsatt vei uten endringslogg`() {
        val client =
            client(
                200,
                """
                [{
                  "fullmaktId": "db40e7d2-44dd-4dab-a954-114513c761d1",
                  "fullmaktsgiver": "55555666000",
                  "fullmektig": "12345678910",
                  "gyldigFraOgMed": "2026-01-01",
                  "leserettigheter": ["AAP"],
                  "skriverettigheter": [],
                  "endringslogg": []
                }]
                """.trimIndent(),
            ) {
                assertEquals("/api/v2/internbruker/fullmakt/bruker-som-fullmektig/alle-fullmakter", it.url.encodedPath)
                assertEquals("false", it.url.queryParameter("endringslogg"))
                val body = Buffer()
                it.body!!.writeTo(body)
                assertEquals("""{"ident":"12345678910"}""", body.readUtf8())
                assertEquals("POST", it.method)
            }
        val result = RepresentasjonApiImpl("http://localhost", client).hentFullmakterForFullmektig(Fnr("12345678910"))
        assertEquals("55555666000", result.single().fullmaktsgiver)
    }

    @Test
    fun `henter vergemal hvor brukeren er verge`() {
        val client =
            client(
                200,
                """
                [{
                  "id": 42,
                  "verge": "12345678910",
                  "vergehaver": "55555666000",
                  "vergemaalstype": "VOKSEN",
                  "gyldigFraOgMed": "2026-01-01",
                  "aktivt": false,
                  "leserettigheter": ["AAP"],
                  "skriverettigheter": []
                }]
                """.trimIndent(),
            ) {
                assertEquals("/api/v2/internbruker/vergemaal/bruker-som-verge/aktive-og-utgaatte-vergemaal", it.url.encodedPath)
                assertEquals("false", it.url.queryParameter("endringslogg"))
                val body = Buffer()
                it.body!!.writeTo(body)
                assertEquals("""{"ident":"12345678910"}""", body.readUtf8())
                assertEquals("POST", it.method)
            }
        val result = RepresentasjonApiImpl("http://localhost", client).hentVergemalForVerge(Fnr("12345678910"))
        assertEquals("55555666000", result.single().vergehaver)
        assertEquals(false, result.single().aktivt)
    }

    @Test
    fun `nye oppslag skiller tom liste og dokumentert 404 fra feil`() {
        for (hent in listOf(RepresentasjonApi::hentFullmakterForFullmektig, RepresentasjonApi::hentVergemalForVerge)) {
            for (status in listOf(401, 403, 500, 503)) {
                val api = RepresentasjonApiImpl("http://localhost", client(status, """{"title":"Feil"}"""))
                assertThrows(Exception::class.java) { hent(api, Fnr("12345678910")) }
            }
            for ((status, body) in listOf(200 to "[]", 404 to """{"title":"Not Found"}""")) {
                val api = RepresentasjonApiImpl("http://localhost", client(status, body))
                assertTrue(hent(api, Fnr("12345678910")).isEmpty())
            }
            for ((status, body) in listOf(204 to "", 200 to "ugyldig json")) {
                val api = RepresentasjonApiImpl("http://localhost", client(status, body))
                assertThrows(Exception::class.java) { hent(api, Fnr("12345678910")) }
            }
        }
    }
}
