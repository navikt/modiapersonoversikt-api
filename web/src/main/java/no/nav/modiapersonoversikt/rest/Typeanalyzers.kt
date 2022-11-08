package no.nav.modiapersonoversikt.rest

import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.personoversikt.common.typeanalyzer.Capture
import no.nav.personoversikt.common.typeanalyzer.Typeanalyzer

enum class Typeanalyzers(val analyzer: Typeanalyzer) {
    SAKER(SamplingTypeanalyzer(Scientist.FixedValueRate(0.05)))
}

class SamplingTypeanalyzer(private val rate: Scientist.ExperimentRate) : Typeanalyzer() {
    override fun capture(value: Any?): Capture? {
        return if (rate.shouldRunExperiment()) {
            super.capture(value)
        } else {
            null
        }
    }
}
