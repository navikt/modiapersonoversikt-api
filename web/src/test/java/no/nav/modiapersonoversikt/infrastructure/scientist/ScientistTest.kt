package no.nav.modiapersonoversikt.infrastructure.scientist

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Test

internal class ScientistTest {
    @Test
    fun `experiment name should be in report`() {
        Scientist.createExperiment<String>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = 1.0,
                reporter = { header, fields ->
                    assertThat(header).contains("[SCIENCE] DummyExperiment")
                }
            )
        ).run({ "Hello" }, { "World" })
    }

    @Test
    fun `experiment should not report error if results are equal`() {
        Scientist.createExperiment<String>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = 1.0,
                reporter = { header, fields ->
                    assertThat(fields).containsEntry("ok", true)
                    assertThat(fields).containsKey("control")
                    assertThat(fields).containsKey("experiment")
                }
            )
        ).run({ "Hello, World" }, { "Hello, World" })
    }

    @Test
    fun `experiment should report error if results are not equal`() {
        Scientist.createExperiment<String>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = 1.0,
                reporter = { header, fields ->
                    assertThat(fields).containsEntry("ok", false)
                    assertThat(fields).containsKey("control")
                    assertThat(fields).containsKey("experiment")
                    assertThat((fields["control"] as Scientist.TimedValue<Any>).value).isEqualTo("Hello")
                    assertThat((fields["experiment"] as Scientist.TimedValue<Any>).value).isEqualTo("World")
                }
            )
        ).run({ "Hello" }, { "World" })
    }

    @Test
    fun `experiment should respect the experiment rate`() {
        var experimentsRun = 0
        val experiment = Scientist.createExperiment<String>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = 0.7,
                reporter = { _, _ -> experimentsRun++ }
            )
        )
        repeat(1000) {
            experiment.run({ "Hello" }, { "World" })
        }
        assertThat(experimentsRun).isCloseTo(700, offset(100))
    }
}
