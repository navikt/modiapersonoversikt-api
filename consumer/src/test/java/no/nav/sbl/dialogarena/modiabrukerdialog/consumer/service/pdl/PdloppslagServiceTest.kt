package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.*
import com.google.gson.GsonBuilder;
import org.junit.Test;

import org.junit.jupiter.api.Assertions

class PdloppslagServiceTest {
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()


    @Test
    fun skalParsefraJsonTilPdlIdentObject() {
        val jsonResponse = """{"data":{"hentIdenter":{"identer":[{"ident":"03016225435","gruppe":"FOLKEREGISTERIDENT"},{"ident":"2004819988162","gruppe":"AKTORID"}]}}}"""

        val objectContent = gson.fromJson(jsonResponse, PdlIdentResponse::class.java)
        val aktor = objectContent.data?.hentIdenter?.identer?.find { identer -> identer.gruppe == "AKTORID" }

        Assertions.assertEquals("2004819988162", aktor?.ident)


    }
}