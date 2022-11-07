package no.nav.modiapersonoversikt.rest

import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.personoversikt.common.typeanalyzer.Typeanalyzer

enum class Typeanalyzers(val analyzer: Typeanalyzer) {
    SAKER(Typeanalyzer())
}

fun Typeanalyzer.sampleRate(rate: Scientist.ExperimentRate): (Any?) -> Unit = { value ->
    if (rate.shouldRunExperiment()) {
        this.capture(value)
    }
}
