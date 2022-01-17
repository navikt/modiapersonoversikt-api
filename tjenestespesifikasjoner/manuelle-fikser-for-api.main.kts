@file:DependsOn("no.nav.common:yaml:2.2021.12.09_11.56-a71c36a61ba3")

import Manuelle_fikser_for_api_main.ChangeUtils.Field
import Manuelle_fikser_for_api_main.ChangeUtils.JsonSource
import Manuelle_fikser_for_api_main.ChangeUtils.YamlSource
import Manuelle_fikser_for_api_main.ChangeUtils.addDefinition
import Manuelle_fikser_for_api_main.ChangeUtils.addResponse
import Manuelle_fikser_for_api_main.ChangeUtils.changeFile
import Manuelle_fikser_for_api_main.ChangeUtils.forDefinition
import Manuelle_fikser_for_api_main.ChangeUtils.forEndpoint
import Manuelle_fikser_for_api_main.ChangeUtils.getTyped
import Manuelle_fikser_for_api_main.ChangeUtils.objectOf
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

changeFile(
    from = YamlSource("oppgave-api/src/main/resources/oppgave/openapi.yaml"),
    to = YamlSource("oppgave-api/src/main/resources/oppgave/openapi-fixed.yaml")
) {
    /**
     * Oppgave-Api sitt kodeverk endepunkt har ikke riktig response-type.
     *
     * Vi lager derfor typene, og legger de til manuell her
     */
    forEndpoint("get", "/api/v1/kodeverk") {
        val schema: Any = mapOf(
            "type" to "array",
            "items" to mapOf(
                "\$ref" to "#/components/schemas/GetInterntKodeverkJson"
            )
        )
        addResponse("200", "application/json", schema)
    }
    val schemas = getTyped<Json>("components").getTyped<Json>("schemas")
    schemas.addDefinition(
        "GetInterntKodeverkJsonTema",
        objectOf(
            Field(name = "tema", type = "string", required = true),
            Field(name = "term", type = "string", required = true)
        )
    )
    schemas.addDefinition(
        "GetInterntKodeverkJsonOppgavetyper",
        objectOf(
            Field(name = "oppgavetype", type = "string", required = true),
            Field(name = "term", type = "string", required = true)
        )
    )
    schemas.addDefinition(
        "GetInterntKodeverkJsonGjelderverdier",
        objectOf(
            Field(name = "behandlingstema", type = "string"),
            Field(name = "behandlingstemaTerm", type = "string"),
            Field(name = "behandlingstype", type = "string"),
            Field(name = "behandlingstypeTerm", type = "string"),
        )
    )

    schemas.addDefinition(
        "GetInterntKodeverkJson",
        objectOf(
            Field(name = "tema", type = "#/components/schemas/GetInterntKodeverkJsonTema", isReference = true, required = true),
            Field(name = "oppgavetyper", type = "#/components/schemas/GetInterntKodeverkJsonOppgavetyper", isReference = true, required = true),
            Field(name = "gjelderverdier", type = "#/components/schemas/GetInterntKodeverkJsonGjelderverdier", isReference = true, required = true),
        )
    )
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
    inline fun <reified T> Json.getTyped(key: String): T = this[key] as T
    fun Json.has(key: String): Boolean = this[key] != null
    fun Json.forDefinition(name: String, block: Json.() -> Unit) {
        block(this.getTyped<Json>("definitions").getTyped(name))
    }
    fun Json.addDefinition(name: String, definition: Any) {
        assert(!this.has(name)) {
            "$name already exist in schema definition"
        }
        this[name] = definition
    }
    fun Json.forEndpoint(method: String, path: String, block: Json.() -> Unit) {
        block(this.getTyped<Json>("paths").getTyped<Json>(path).getTyped(method))
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
    fun Json.addResponse(statusCode: String, contentType: String, schema: Any) {
        require(this.has("responses")) {
            "Json did not contain a 'responses' field: ${this.keys}"
        }
        val responses: Json = this.getTyped("responses")
        val definition: Json = responses.getTyped(statusCode) ?: mutableMapOf()
        val content: Json = definition.getTyped("content") ?: mutableMapOf()
        val contentTypeValue: Json = definition.getTyped(contentType) ?: mutableMapOf()

        contentTypeValue["schema"] = schema
        content[contentType] = contentTypeValue
        definition["content"] = content
        responses[statusCode] = definition
    }
    fun objectOf(vararg fields: Field): Json {
        return mutableMapOf(
            "type" to "object",
            "properties" to fields
                .map { it.toSpec() }
                .reduce { s, t ->
                    t.forEach { (k, v) ->
                        s[k] = v
                    }
                    s
                },
            "required" to fields.filter { it.required }.map { it.name }
        )
    }
    data class Field(val name: String, val type: String, val isReference: Boolean = false, val required: Boolean = false) {
        fun toSpec(): Json {
            val typeKey = if (isReference) "\$ref" else "type"
            return mutableMapOf(
                name to mutableMapOf(
                    typeKey to type
                )
            )
        }
    }
}
