package no.nav.modiapersonoversikt.infrastructure.kabac.utils

import no.nav.modiapersonoversikt.infrastructure.kabac.AttributeValue
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.KabacException
import no.nav.modiapersonoversikt.infrastructure.kabac.impl.EvaluationContextImpl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class EvaluationContextTest {
    val dummyKey = Key<String>("dummy")
    val nullableKey = Key<String?>("dummy")
    val dummyProvider = AttributeValue(dummyKey, "dummy-value")
    val nullProvider = AttributeValue(nullableKey, null)

    @Test
    internal fun `get value based on providerkey`() {
        val ctx = EvaluationContextImpl(listOf(dummyProvider))

        val value = ctx.getValue(dummyKey)

        assertThat(value).isEqualTo("dummy-value")
    }

    @Test
    internal fun `get value based on provide`() {
        val ctx = EvaluationContextImpl(listOf(dummyProvider))

        val value = ctx.getValue(dummyProvider)

        assertThat(value).isEqualTo("dummy-value")
    }

    @Test
    internal fun `require value based on key`() {
        val ctx = EvaluationContextImpl(listOf(dummyProvider))

        val value = ctx.getValue(dummyKey)

        assertThat(value).isEqualTo("dummy-value")
    }

    @Test
    internal fun `require value based on provider`() {
        val ctx = EvaluationContextImpl(listOf(dummyProvider))

        val value = ctx.getValue(dummyProvider)

        assertThat(value).isEqualTo("dummy-value")
    }

    @Test
    internal fun `get nullable value should return null`() {
        val ctx = EvaluationContextImpl(listOf(nullProvider))

        val value = ctx.getValue(nullProvider)

        assertThat(value).isNull()
    }

    @Test
    internal fun `require nullable value should throw exception`() {
        val ctx = EvaluationContextImpl(listOf(nullProvider))

        assertThat(ctx.getValue(nullProvider)).isNull()
    }

    @Test
    internal fun `getting value from non-configured provider should throw exception`() {
        val ctx = EvaluationContextImpl(emptyList())

        assertThatThrownBy {
            ctx.getValue(dummyProvider)
        }.isInstanceOf(KabacException.MissingPolicyInformationPointException::class.java)
    }

    @Test
    internal fun `values retrived from providers should be cached`() {
        val fastMockProvider = object : Kabac.PolicyInformationPoint<String> {
            var executionCount: Int = 0
            override val key = Key<String>("mock-key")
            override fun provide(ctx: Kabac.EvaluationContext): String {
                executionCount++
                return "mock-value"
            }
        }
        val ctx = EvaluationContextImpl(listOf(fastMockProvider))

        val values = listOf(
            ctx.getValue(fastMockProvider),
            ctx.getValue(fastMockProvider)
        )

        assertThat(values).isEqualTo(listOf("mock-value", "mock-value"))
        assertThat(fastMockProvider.executionCount).isEqualTo(1)
    }

    @Test
    internal fun `should throw error if cyclic pip usage is found`() {
        val size = 4
        val keys = mutableListOf<Key<Any>>()
        val providers = mutableListOf<Kabac.PolicyInformationPoint<Any>>()
        repeat(size) { i -> keys.add(Key("key${i + 1}")) }
        repeat(size) { i -> providers.add(createCyclicProvider(keys[i], keys[(i + 1) % size])) }
        val ctx = EvaluationContextImpl(providers)

        assertThatThrownBy { ctx.getValue(keys[0]) }
            .isInstanceOf(KabacException.CyclicDependenciesException::class.java)
            .hasMessage("Cycle: key1 -> key2 -> key3 -> key4 -> key1")
    }

    @Test
    internal fun `self referencing pip should throw error`() {
        val size = 1
        val keys = mutableListOf<Key<Any>>()
        repeat(size) { i -> keys.add(Key("key${i + 1}")) }
        val providers = mutableListOf<Kabac.PolicyInformationPoint<Any>>(
            createCyclicProvider(keys[0], keys[0])
        )
        val ctx = EvaluationContextImpl(providers)

        assertThatThrownBy { ctx.getValue(keys[0]) }
            .isInstanceOf(KabacException.CyclicDependenciesException::class.java)
            .hasMessage("Cycle: key1 -> key1")
    }

    @Test
    internal fun `break cyclic pip usage by providing a single value`() {
        val size = 10
        val keys = mutableListOf<Key<Any>>()
        val providers = mutableListOf<Kabac.PolicyInformationPoint<Any>>()
        repeat(size) { i -> keys.add(Key("key${i + 1}")) }
        repeat(size) { i -> providers.add(createCyclicProvider(keys[i], keys[(i + 1) % size])) }

        val providedKey = keys[5]
        val ctx = EvaluationContextImpl(
            providers + listOf(
                AttributeValue(providedKey, "OK")
            )
        )

        val result = ctx.getValue(keys[0])

        assertThat(result).isEqualTo("OK")
    }

    private fun <T> createCyclicProvider(key: Key<T>, dependent: Key<T>) = object : Kabac.PolicyInformationPoint<T> {
        override val key: Key<T> = key
        override fun provide(ctx: Kabac.EvaluationContext): T {
            return ctx.getValue(dependent)
        }
    }
}
