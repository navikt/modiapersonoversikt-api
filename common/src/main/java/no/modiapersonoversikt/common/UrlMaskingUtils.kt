package no.modiapersonoversikt.common

object UrlMaskingUtils {
    private val replacements =
        listOf(
            "$1{fnr}" to "(^|/)\\d{11}(?=$|/)".toRegex(),
            "$1{orgnr}" to "(^|/)\\d{9}(?=$|/)".toRegex(),
            "$1{ident}" to "(^|/)[a-zA-Z]\\d{6}(?=$|/)".toRegex(),
            "$1{enhet}" to "(^|/)\\d{4,6}(?=$|/)".toRegex(),
            "$1{kjedeId}" to "(^|/)100[0-9A-Zoi]{6}(?=$|/)".toRegex(), // HenvendelseId
            "$1{kjedeId}" to "(^|/)(?=[^/]*?[0-9])(?=[^/]*?[a-z])[a-z0-9]{18}(?=$|/)".toRegex(), // SalesforceId
        )

    fun maskSensitiveInfo(stringToMask: String): String {
        return replacements.fold(stringToMask) { current, (replacement, regex) ->
            current.replace(regex, replacement)
        }
    }
}
