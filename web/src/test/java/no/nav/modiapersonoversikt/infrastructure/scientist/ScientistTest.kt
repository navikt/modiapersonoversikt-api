package no.nav.modiapersonoversikt.infrastructure.scientist

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sak
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
                    assertThat(fields).containsKey("controlTime")
                    assertThat(fields).containsKey("experiment")
                    assertThat(fields).containsKey("experimentTime")
                    assertThat((fields["control"] as String)).isEqualTo("\"Hello\"")
                    assertThat((fields["experiment"] as String)).isEqualTo("\"World\"")
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

    @Test
    fun `experiment should serialize results and do deep comparision`() {
        val controlResult = listOf(
            Sak().withSaksId("123").withTemakode("DAG"),
            Sak().withSaksId("456").withTemakode("AAP"),
            Sak().withSaksId("789").withTemakode("SYK")
        )
        val experimentResult = listOf(
            Sak().withSaksId("123").withTemakode("DAG"),
            Sak().withSaksId("456").withTemakode("AAP"),
            Sak().withSaksId("789").withTemakode("SYK")
        )
        Scientist.createExperiment<List<Sak>>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = 1.0,
                reporter = { header, fields ->
                    assertThat(fields).containsEntry("ok", true)
                    assertThat(fields).containsKey("control")
                    assertThat(fields).containsKey("controlTime")
                    assertThat(fields).containsKey("experiment")
                    assertThat(fields).containsKey("experimentTime")
                }
            )
        ).run({ controlResult }, { experimentResult })
    }
}
