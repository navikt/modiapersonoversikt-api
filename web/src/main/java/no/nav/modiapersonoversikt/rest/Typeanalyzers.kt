package no.nav.modiapersonoversikt.rest

import no.nav.personoversikt.common.science.Rate
import no.nav.personoversikt.common.typeanalyzer.Capture
import no.nav.personoversikt.common.typeanalyzer.Typeanalyzer

enum class Typeanalyzers(val analyzer: Typeanalyzer) {
    SAKER(SamplingTypeanalyzer(Rate.FixedValue(0.05)))
}

class SamplingTypeanalyzer(private val rate: Rate) : Typeanalyzer() {
    override fun capture(value: Any?): Capture? {
        return if (rate.evaluate()) {
            super.capture(value)
        } else {
            null
        }
    }
}
