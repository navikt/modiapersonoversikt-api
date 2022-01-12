package no.nav.modiapersonoversikt.testutils

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import kotlin.test.assertEquals

class SnapshotRule(path: String = "src/test/resources/snapshots") : TestWatcher() {
    private val path: String
    private var name: String? = null
    private var counter: Int = 0
    private var hadMissingFile: Boolean = false
    companion object {
        private val json = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
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

    override fun starting(description: Description) {
        this.name = "${description.className}_${description.methodName}"
    }

    override fun finished(description: Description?) {
        if (hadMissingFile) {
            throw IllegalStateException("Snapshot did not exist, but was created. Rerun to verify.")
        }
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
            assertEquals(read(file), createSnapshot(value))
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
        Files.writeString(file.toPath(), createSnapshot(value), UTF_8)
    }

    private fun read(file: File): String {
        return Files.readString(file.toPath(), UTF_8)
    }

    private fun createSnapshot(value: Any): String {
        return json
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(value)
    }
}
