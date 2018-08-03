package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import no.nav.kjerneinfo.common.domain.Periode
import java.time.format.DateTimeFormatter

const val DATOFORMAT = "yyyy-MM-dd"

fun <K, V> mapOfNotNullOrEmpty(vararg pairs: Pair<K, V>) = pairs
        .filterNot { it.second == null }
        .filterNot { it.second is Map<*, *> && (it.second as Map<*, *>).isEmpty() }
        .filterNot { it.second is Collection<*> && (it.second as Collection<*>).isEmpty() }
        .toMap()

fun lagPeriode(periode: Periode) = mapOf(
        "fra" to periode.from?.toString(DATOFORMAT),
        "til" to periode.to?.toString(DATOFORMAT) )

fun lagPleiepengePeriode(periode: no.nav.sykmeldingsperioder.domain.pleiepenger.Periode) = mapOf(
        "fom" to periode.fraOgMed?.format(DateTimeFormatter.ofPattern(DATOFORMAT)),
        "tom" to periode.tilOgMed?.format(DateTimeFormatter.ofPattern(DATOFORMAT)) )
