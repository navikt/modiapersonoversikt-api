package no.nav.modiapersonoversikt.infrastructure.scientist

import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.data.Offset.offset
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test

internal class ScientistTest {
    @Test
    fun `experiment name should be in report`() {
        Scientist.createExperiment<String>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = Scientist.FixedValueRate(1.0),
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
                experimentRate = Scientist.FixedValueRate(1.0),
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
                experimentRate = Scientist.FixedValueRate(1.0),
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
                experimentRate = Scientist.FixedValueRate(0.7),
                reporter = { _, _ -> experimentsRun++ }
            )
        )
        repeat(1000) {
            experiment.run({ "Hello" }, { "World" })
        }
        assertThat(experimentsRun).isCloseTo(700, offset(100))
    }

    @Test
    internal fun `experiment should respect experiment rate override`() {
        var experimentsRun = 0
        val experiment = Scientist.createExperiment<String>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = Scientist.FixedValueRate(0.1),
                reporter = { _, _ -> experimentsRun++ }
            )
        )
        repeat(1000) {
            experiment.run(
                control = { "Hello" },
                experiment = { "World" },
                overrideRate = Scientist.FixedValueRate(0.7)
            )
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
                experimentRate = Scientist.FixedValueRate(1.0),
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

    private data class Dataholder(val id: String, val count: Int, val isDone: Boolean)

    @Test
    fun `experiment should compare untyped maps to typed objects correctly`() {
        val controlResult = listOf(
            mapOf(
                "id" to "123",
                "count" to 4,
                "isDone" to false
            ),
            mapOf(
                "id" to "126",
                "count" to 6,
                "isDone" to true
            )
        )
        val experimentResult = listOf(
            Dataholder("123", 4, false),
            Dataholder("126", 6, true)
        )
        Scientist.createExperiment<List<Map<String, Any?>>>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = Scientist.FixedValueRate(1.0),
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

    @Test
    internal fun `should report metadatafields`() {
        Scientist.createExperiment<String>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = Scientist.FixedValueRate(1.0),
                reporter = { header, fields ->
                    assertThat(fields).containsEntry("ok", true)
                    assertThat(fields).containsKey("control")
                    assertThat(fields).containsKey("experiment")
                    assertThat(fields).containsKey("control-extra")
                    assertThat(fields).containsKey("experiment-extra")
                }
            )
        ).runIntrospected(
            control = { "Hello, World" },
            experiment = { "Hello, World" },
            dataFields = { control, triedExperiment ->
                mapOf(
                    "control-extra" to 1,
                    "experiment-extra" to "value"
                )
            }
        )
    }

    @Test
    internal fun `should run experiment in parallel`() {
        val startTime = System.currentTimeMillis()
        Scientist.createExperiment<String>(
            Scientist.Config(
                name = "DummyExperiment",
                experimentRate = Scientist.FixedValueRate(1.0),
                reporter = { _, _ ->
                    val endTime = System.currentTimeMillis()
                    assertThat(endTime - startTime).isCloseTo(2000, Percentage.withPercentage(15.0))
                }
            )
        ).run(
            control = {
                Thread.sleep(2000L)
                "Control"
            },
            experiment = {
                Thread.sleep(500L)
                "Experiment"
            }
        )
    }
}
