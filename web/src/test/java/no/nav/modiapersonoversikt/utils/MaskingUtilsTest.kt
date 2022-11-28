package no.nav.modiapersonoversikt.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MaskingUtilsTest {
    @Test
    fun returnsEmptyStringIfEmptyStringIsProvided() {
        val stringToTest = ""
        val maskingResult = MaskingUtils.maskSensitiveInfo(stringToTest)
        Assertions.assertEquals(stringToTest, maskingResult)
    }

    @Test
    fun doNotMaskStringIfStringContainsChars() {
        val stringToTest = "12EF5678910"
        val maskingResult = MaskingUtils.maskSensitiveInfo(stringToTest)
        Assertions.assertEquals(stringToTest, maskingResult)
    }

    @Test
    fun masksStringIfStringIsFnr() {
        val stringToTest = "12345678910"
        val maskingResult = MaskingUtils.maskSensitiveInfo(stringToTest)
        Assertions.assertEquals("{fnr}", maskingResult)
    }

    @Test
    fun masksPartOfPathThatIsFnr() {
        val fnr = "12345678910"
        val stringToTest = "/foo/$fnr/bar"
        val maskingResult = MaskingUtils.maskSensitiveInfo(stringToTest)
        Assertions.assertEquals("/foo/{fnr}/bar", maskingResult)
    }
}
