@file:DependsOn("no.nav.common:yaml:2.2021.12.09_11.56-a71c36a61ba3")

import Manuelle_fikser_for_api_main.ChangeUtils.JsonSource
import Manuelle_fikser_for_api_main.ChangeUtils.YamlSource
import Manuelle_fikser_for_api_main.ChangeUtils.changeFile
import Manuelle_fikser_for_api_main.ChangeUtils.forDefinition
import Manuelle_fikser_for_api_main.ChangeUtils.removeProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import no.nav.common.json.JsonMapper
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

changeFile(
    from = JsonSource("norg-api/src/main/resources/norg/openapi.json"),
    to = YamlSource("norg-api/src/main/resources/norg/openapi-fixed.yaml"),
) {
    /**
     * NorgApi specen bruker arv og subklasser
     * `RsPostboksadresse` og `RsStedsadresse` arver begge fra `RsAdresse` som spesifiserer feltene `postnummer` og `poststed`
     *
     * For at Jackson skal lage riktig klasse ved deserialisering legges `x-discriminator-value` til.
     * Dette feltet er spesifisert på modellen i norg, men er ikke tilgjengelig via openapi-specen.
     *
     * For å unngå kollisjoner på feltnavn fjernes `postnummer` og `poststed` fra klassene siden disse arves fra `RsAdresse`
     */
    forDefinition("RsPostboksadresse") {
        put("x-discriminator-value", "postboksadresse")
        removeProperty("postnummer", "poststed")
    }
    forDefinition("RsStedsadresse") {
        put("x-discriminator-value", "stedsadresse")
        removeProperty("postnummer", "poststed")
    }
}

/**
 * Må ligge i samme fil pga bug med kotlin-scripts sin @file:Import funksjon
 * Når det blir løst kan dette flytte ut til egen fil
 */
typealias Json = MutableMap<String, Any>
object ChangeUtils {
    val asJson = object : TypeReference<Json>() {}
    val charset = Charset.forName("UTF-8")
    val yamlParser = JsonMapper
        .applyDefaultConfiguration(ObjectMapper(YAMLFactory()))
        .enable(SerializationFeature.INDENT_OUTPUT)
    val jsonParser = JsonMapper
        .defaultObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)

    sealed class Source(
        val mapper: ObjectMapper,
        val source: String
    )
    class JsonSource(source: String) : Source(jsonParser, source)
    class YamlSource(source: String) : Source(yamlParser, source)

    fun getFileName(file: String) = file.substring(file.lastIndexOf("/") + 1)
    fun readFile(file: String): String = Files.readString(Path.of(file), charset)
    fun writeFile(file: String, content: String) = Files.writeString(Path.of(file), content, charset)
    fun changeFile(from: Source, to: Source, block: Json.() -> Unit = {}) {
        runCatching {
            val content = readFile(from.source)
            val json = from.mapper.readValue(content, asJson)
            block(json)
            val mutatedJson = to.mapper.writeValueAsString(json)
            writeFile(to.source, mutatedJson)
        }
            .onSuccess { println("Mutated ${from.source} -> ${to.source}") }
            .onFailure { println("Mutation of ${from.source} failed: $it") }
    }
    inline fun <reified T> Json.getTyped(key: String): T = requireNotNull(this[key]) as T
    fun Json.has(key: String): Boolean = this[key] != null
    fun Json.forDefinition(name: String, block: Json.() -> Unit) {
        block(this.getTyped<Json>("definitions").getTyped(name))
    }
    fun Json.removeProperty(vararg names: String) {
        if (this.has("properties")) {
            val properties = this.getTyped<Json>("properties")
            names.forEach { properties.remove(it) }
        }

        if (this.has("allOf")) {
            this.getTyped<List<Json>>("allOf").forEach {
                it.removeProperty(*names)
            }
        }
    }
}
