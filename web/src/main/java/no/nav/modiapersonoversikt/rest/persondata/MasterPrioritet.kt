package no.nav.modiapersonoversikt.rest.persondata

object MasterPrioritet {
    private const val UNKOWN_PRIORITY = 9999
    private val masterPriorityMap =
        mapOf(
            "PDL" to 1,
            "Freg" to 2,
        )

    operator fun get(value: String): Int = masterPriorityMap[value] ?: UNKOWN_PRIORITY
}
