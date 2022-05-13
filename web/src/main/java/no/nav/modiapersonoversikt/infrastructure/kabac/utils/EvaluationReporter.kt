package no.nav.modiapersonoversikt.infrastructure.kabac.utils

interface EvaluationReporter {
    fun getReport(): String
    fun addToReport(value: Any?): EvaluationReporter
    fun tab(): EvaluationReporter
    fun untab(): EvaluationReporter

    class Impl : EvaluationReporter {
        val sb = StringBuilder()
        var tab = ""
        override fun addToReport(value: Any?): EvaluationReporter {
            sb.appendLine("$tab$value")
            return this
        }

        override fun tab(): EvaluationReporter {
            tab = "$tab\t"
            return this
        }

        override fun untab(): EvaluationReporter {
            tab = tab.removePrefix("\t")
            return this
        }

        override fun getReport(): String {
            return sb.toString()
        }
    }
}
