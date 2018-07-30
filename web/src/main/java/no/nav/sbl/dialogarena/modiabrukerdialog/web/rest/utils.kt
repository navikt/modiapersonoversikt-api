package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

fun <K, V> mapOfNotNull(vararg pairs: Pair<K, V>) = pairs
        .filterNot { it.second == null || it.second is Map<*, *> && (it.second as Map<*, *>).isEmpty() }
        .toMap()
