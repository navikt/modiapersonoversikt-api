package no.nav.modiapersonoversikt.utils

object UrlMaskingUtils {
    private val replacements = mapOf(
        "$1{fnr}" to "(^|/)\\d{11}(?=$|/)".toRegex(),
        "$1{ident}" to "(^|/)[a-zA-Z]\\d{6}(?=$|/)".toRegex(),
        "$1{enhet}" to "(^|/)\\d{4}(?=$|/)".toRegex()
    )

    fun maskSensitiveInfo(stringToMask: String): String {
        return replacements.entries.fold(stringToMask) { current, (replacement, regex) ->
            current.replace(regex, replacement)
        }
    }
}
