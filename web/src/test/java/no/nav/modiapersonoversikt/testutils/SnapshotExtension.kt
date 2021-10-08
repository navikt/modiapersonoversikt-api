package no.nav.modiapersonoversikt.testutils

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator
import org.junit.jupiter.api.extension.*
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import kotlin.test.assertEquals
import kotlin.test.fail

class SnapshotExtension(
    path: String = "src/test/resources/snapshots",
    val debug: Boolean = false
) : ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private val path: String
    private var name: String? = null
    private var counter: Int = 0
    private var hadMissingFile: Boolean = false
    companion object {
        private val json = ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .activateDefaultTyping(DefaultBaseTypeLimitingValidator())
    }

    init {
        if (path.endsWith("/")) {
            this.path = path
        } else {
            this.path = "$path/"
        }
        File(this.path).mkdirs()
    }

    override fun beforeTestExecution(context: ExtensionContext) {
        counter = 0
        val className = context.testClass.get().name
        val methodName = context.testMethod.get().name.replace("\$modiabrukerdialog_web", "")
        this.name = "${className}_$methodName"
    }

    override fun afterTestExecution(context: ExtensionContext) {
        if (hadMissingFile) {
            throw IllegalStateException("Snapshot did not exist, but was created. Rerun to verify.")
        }
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == SnapshotExtension::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return this
    }

    fun assertMatches(value: Any) {
        assertMatches(getFile(counter++), value)
    }

    fun updateSnapshot(value: Any) {
        val file = getFile(counter++)
        if (read(file) == createSnapshot(value)) {
            throw IllegalStateException("Cannot update snapshot since they already are equal")
        } else {
            save(file, value)
            throw IllegalStateException("Snapshot updated, replace call with call to `assertMatches`")
        }
    }

    private fun assertMatches(file: File, value: Any) {
        try {
            val snapshot = createSnapshot(value)
            if (debug) {
                fail("Debugmode enabled.\nSnapshot:\n$snapshot");

            } else {
                assertEquals(read(file), snapshot)
            }
        } catch (e: NoSuchFileException) {
            save(file, value)
            assertMatches(file, value)
            hadMissingFile = true
        }
    }

    private fun getFile(id: Int): File {
        return this.name
            ?.let { File("$path$it-$id.json") }
            ?: throw IllegalStateException("No name...")
    }

    private fun save(file: File, value: Any) {
        if (!debug) {
            Files.writeString(file.toPath(), createSnapshot(value), UTF_8)
        }
    }

    private fun read(file: File): String {
        return Files.readString(file.toPath(), UTF_8)
    }

    private fun createSnapshot(value: Any): String {
        if (value is String) {
            try {
                val tree = json.readTree(value)
                return json
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(tree)
            } catch (e: Exception) {
                // Not JSON
            }
        }
        return json
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(value)
    }
}
