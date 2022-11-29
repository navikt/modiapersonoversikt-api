package no.nav.modiapersonoversikt.utils

private val maskingPattern = "(^|\\W)\\d{11}(?=$|\\W)".toRegex()

object MaskingUtils {
    fun maskSensitiveInfo(stringToMask: String): String {
        return stringToMask.replace(maskingPattern, "$1{fnr}")
    }
}
