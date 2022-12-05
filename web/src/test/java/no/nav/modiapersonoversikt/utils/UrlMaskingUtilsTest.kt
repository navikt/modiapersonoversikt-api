package no.nav.modiapersonoversikt.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UrlMaskingUtilsTest {
    @Test
    fun returnsEmptyStringIfEmptyStringIsProvided() {
        val stringToTest = ""
        val maskingResult = UrlMaskingUtils.maskSensitiveInfo(stringToTest)
        Assertions.assertEquals(stringToTest, maskingResult)
    }

    @Test
    fun doNotMaskStringIfStringContainsChars() {
        val stringToTest = "12EF5678910"
        val maskingResult = UrlMaskingUtils.maskSensitiveInfo(stringToTest)
        Assertions.assertEquals(stringToTest, maskingResult)
    }

    @Test
    fun masksStringIfStringIsFnr() {
        val stringToTest = "12345678910"
        val maskingResult = UrlMaskingUtils.maskSensitiveInfo(stringToTest)
        Assertions.assertEquals("{fnr}", maskingResult)
    }

    @Test
    fun masksPartOfPathThatIsFnr() {
        val fnr = "12345678910"
        val stringToTest = "/foo/$fnr/bar"
        val maskingResult = UrlMaskingUtils.maskSensitiveInfo(stringToTest)
        Assertions.assertEquals("/foo/{fnr}/bar", maskingResult)
    }

    @Test
    internal fun masksEnhetAtEndOfString() {
        val content = "/norg2/api/v1/enhet/navkontorer/0219"
        val masked = UrlMaskingUtils.maskSensitiveInfo(content)

        Assertions.assertEquals("/norg2/api/v1/enhet/navkontorer/{enhet}", masked)
    }

    @Test
    internal fun masksEnhetInTheMiddle() {
        val content = "/norg2/api/v1/enhet/navkontorer/0219/alle"
        val masked = UrlMaskingUtils.maskSensitiveInfo(content)

        Assertions.assertEquals("/norg2/api/v1/enhet/navkontorer/{enhet}/alle", masked)
    }

    @Test
    internal fun masksGTInTheMiddle() {
        val content = "/norg2/api/v1/enhet/navkontorer/021901/alle"
        val masked = UrlMaskingUtils.maskSensitiveInfo(content)

        Assertions.assertEquals("/norg2/api/v1/enhet/navkontorer/{enhet}/alle", masked)
    }

    @Test
    internal fun masksIdentInTheMiddle() {
        val content = "/norg2/api/v1/Z123456/navkontorer/0219"
        val masked = UrlMaskingUtils.maskSensitiveInfo(content)

        Assertions.assertEquals("/norg2/api/v1/{ident}/navkontorer/{enhet}", masked)
    }

    @Test
    internal fun masksOrgnrInTheMiddle() {
        val content = "/norg2/api/v1/Z123456/org/021998765/name"
        val masked = UrlMaskingUtils.maskSensitiveInfo(content)

        Assertions.assertEquals("/norg2/api/v1/{ident}/org/{orgnr}/name", masked)
    }

    @Test
    internal fun maskThemAll() {
        val content = "fnr/12345678910/enhet/0219/gt/987654/ident/Z999123/orgnr/123456789"
        val masked = UrlMaskingUtils.maskSensitiveInfo(content)

        Assertions.assertEquals("fnr/{fnr}/enhet/{enhet}/gt/{enhet}/ident/{ident}/orgnr/{orgnr}", masked)
    }

    @Test
    fun maskHenvendelseId() {
        val content1 = "/henvendelseinfo/henvendelse/{kjedeId}/status".replace("{" + "kjedeId" + "}", "a1jf31a358c79d24a6")
        val content2 = "/henvendelseinfo/henvendelse/{kjedeId}".replace("{" + "kjedeId" + "}", "a1jf31a358c79d24a6")
        val content3 = "/henvendelseinfo/henvendelse/{kjedeId}/status".replace("{" + "kjedeId" + "}", "10016JEAX")
        val content4 = "/henvendelseinfo/henvendelse/{kjedeId}".replace("{" + "kjedeId" + "}", "10016JEAX")

        val masked1 = UrlMaskingUtils.maskSensitiveInfo(content1)
        val masked2 = UrlMaskingUtils.maskSensitiveInfo(content2)
        val masked3 = UrlMaskingUtils.maskSensitiveInfo(content3)
        val masked4 = UrlMaskingUtils.maskSensitiveInfo(content4)

        Assertions.assertEquals("/henvendelseinfo/henvendelse/{kjedeId}/status", masked1)
        Assertions.assertEquals("/henvendelseinfo/henvendelse/{kjedeId}", masked2)
        Assertions.assertEquals("/henvendelseinfo/henvendelse/{kjedeId}/status", masked3)
        Assertions.assertEquals("/henvendelseinfo/henvendelse/{kjedeId}", masked4)
    }
}
