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
}
