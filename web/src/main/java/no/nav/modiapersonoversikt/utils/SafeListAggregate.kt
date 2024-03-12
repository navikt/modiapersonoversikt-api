package no.nav.modiapersonoversikt.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SafeListAggregate<TYPE, ERRORTYPE>(
    private val successes: List<TYPE>,
    private val failures: List<ERRORTYPE> = mutableListOf(),
) {
    private val log: Logger = LoggerFactory.getLogger(SafeListAggregate::class.java)

    fun filter(predicate: (TYPE) -> Boolean): SafeListAggregate<TYPE, TYPE> {
        val group = this.successes.groupBy(predicate)
        return SafeListAggregate(
            successes = group[true] ?: emptyList(),
            failures = group[false] ?: emptyList(),
        )
    }

    fun <NEWTYPE> fold(
        transformSuccess: (TYPE) -> NEWTYPE,
        transformFailure: (TYPE) -> ERRORTYPE,
    ): SafeListAggregate<NEWTYPE, ERRORTYPE> {
        val mappedSuccesses: MutableList<NEWTYPE> = mutableListOf()
        val mappedFailures: MutableList<ERRORTYPE> = this.failures.toMutableList()
        for (item in successes) {
            try {
                mappedSuccesses.add(transformSuccess(item))
            } catch (e: Exception) {
                log.error("Could not transform $item", e)
                mappedFailures.add(transformFailure(item))
            }
        }
        return SafeListAggregate(mappedSuccesses, mappedFailures)
    }

    fun getWithFailureHandling(handleFailures: (List<ERRORTYPE>) -> Unit): List<TYPE> {
        if (this.failures.isNotEmpty()) {
            handleFailures(this.failures)
        }
        return this.successes
    }
}
