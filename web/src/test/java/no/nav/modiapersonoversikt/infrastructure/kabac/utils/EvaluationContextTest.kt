package no.nav.modiapersonoversikt.infrastructure.kabac.utils

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class EvaluationContextTest {
    val dummyKey = Key<String>("dummy")
    val dummyProvider = AttributeValue(dummyKey, "dummy-value")
    val nullProvider = AttributeValue(dummyKey, null)

    @Test
    internal fun `get value based on providerkey`() {
        val ctx = EvaluationContext(listOf(dummyProvider))

        val value = ctx.getValue(dummyKey)

        assertThat(value).isEqualTo("dummy-value")
    }

    @Test
    internal fun `get value based on provide`() {
        val ctx = EvaluationContext(listOf(dummyProvider))

        val value = ctx.getValue(dummyProvider)

        assertThat(value).isEqualTo("dummy-value")
    }

    @Test
    internal fun `require value based on key`() {
        val ctx = EvaluationContext(listOf(dummyProvider))

        val value = ctx.requireValue(dummyKey)

        assertThat(value).isEqualTo("dummy-value")
    }

    @Test
    internal fun `require value based on provider`() {
        val ctx = EvaluationContext(listOf(dummyProvider))

        val value = ctx.requireValue(dummyProvider)

        assertThat(value).isEqualTo("dummy-value")
    }

    @Test
    internal fun `get nullable value should return null`() {
        val ctx = EvaluationContext(listOf(nullProvider))

        val value = ctx.getValue(nullProvider)

        assertThat(value).isNull()
    }

    @Test
    internal fun `require nullable value should throw exception`() {
        val ctx = EvaluationContext(listOf(nullProvider))

        assertThatThrownBy {
            ctx.requireValue(nullProvider)
        }.isInstanceOf(Kabac.MissingAttributeValueException::class.java)
    }

    @Test
    internal fun `getting value from non-configured provider should throw exception`() {
        val ctx = EvaluationContext(emptyList())

        assertThatThrownBy {
            ctx.requireValue(dummyProvider)
        }.isInstanceOf(Kabac.MissingAttributeProviderException::class.java)
    }

    @Test
    internal fun `values retrived from providers should be cached`() {
        val fastMockProvider = object : Kabac.AttributeProvider<String> {
            var executionCount: Int = 0
            override val key = Key<String>("mock-key")
            override fun provide(ctx: EvaluationContext): String {
                executionCount++
                return "mock-value"
            }
        }
        val ctx = EvaluationContext(listOf(fastMockProvider))

        val values = listOf(
            ctx.requireValue(fastMockProvider),
            ctx.requireValue(fastMockProvider)
        )

        assertThat(values).isEqualTo(listOf("mock-value", "mock-value"))
        assertThat(fastMockProvider.executionCount).isEqualTo(1)
    }

    @Test
    internal fun `should throw error if cyclic pip usage is found`() {
        val size = 4
        val keys = mutableListOf<Key<Any>>()
        val providers = mutableListOf<Kabac.AttributeProvider<Any>>()
        repeat(size) { i -> keys.add(Key("key${i + 1}")) }
        repeat(size) { i -> providers.add(createCyclicProvider(keys[i], keys[(i + 1) % size])) }
        val ctx = EvaluationContext(providers)

        assertThatThrownBy { ctx.getValue(keys[0]) }
            .isInstanceOf(Kabac.CycleInPipUsageException::class.java)
            .hasMessage("Cycle: key1 -> key2 -> key3 -> key4 -> key1")
    }

    @Test
    internal fun `self referencing pip should throw error`() {
        val size = 1
        val keys = mutableListOf<Key<Any>>()
        repeat(size) { i -> keys.add(Key("key${i + 1}")) }
        val providers = mutableListOf<Kabac.AttributeProvider<Any>>(
            createCyclicProvider(keys[0], keys[0])
        )
        val ctx = EvaluationContext(providers)

        assertThatThrownBy { ctx.getValue(keys[0]) }
            .isInstanceOf(Kabac.CycleInPipUsageException::class.java)
            .hasMessage("Cycle: key1 -> key1")
    }

    @Test
    internal fun `break cyclic pip usage by providing a single value`() {
        val size = 10
        val keys = mutableListOf<Key<Any>>()
        val providers = mutableListOf<Kabac.AttributeProvider<Any>>()
        repeat(size) { i -> keys.add(Key("key${i + 1}")) }
        repeat(size) { i -> providers.add(createCyclicProvider(keys[i], keys[(i + 1) % size])) }

        val providedKey = keys[5]
        val ctx = EvaluationContext(
            providers + listOf(
                AttributeValue(providedKey, "OK")
            )
        )

        val result = ctx.getValue(keys[0])

        assertThat(result).isEqualTo("OK")
    }

    private fun <T : Any> createCyclicProvider(key: Key<T>, dependent: Key<T>) = object : Kabac.AttributeProvider<T> {
        override val key: Key<T> = key
        override fun provide(ctx: EvaluationContext): T? {
            return ctx.getValue(dependent)
        }
    }
}
