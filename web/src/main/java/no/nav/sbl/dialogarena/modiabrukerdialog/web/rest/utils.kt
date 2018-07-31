package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

fun <K, V> mapOfNotNullOrEmpty(vararg pairs: Pair<K, V>) = pairs
        .filterNot { it.second == null }
        .filterNot { it.second is Map<*, *> && (it.second as Map<*, *>).isEmpty() }
        .filterNot { it.second is Collection<*> && (it.second as Collection<*>).isEmpty() }
        .toMap()
