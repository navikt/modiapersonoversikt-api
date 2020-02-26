package no.nav.sbl.dialogarena.abac

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import no.nav.sbl.dialogarena.abac.mapper.JsonMapperTypes.*

private val serializer: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .setPrettyPrinting()
        .create()

private val deserializer: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .setPrettyPrinting()
        .registerTypeAdapter(responseType, forcedListAdapter<Response>())
        .registerTypeAdapter(associatedAdviceType, forcedListAdapter<Advice>())
        .registerTypeAdapter(attributeAssignmentType, forcedListAdapter<AttributeAssignment>())
        .create()

internal inline fun <reified T> forcedListAdapter(): JsonDeserializer<List<T>> {
    return JsonDeserializer { json, _, context ->
        val list = ArrayList<T>()
        when {
            json.isJsonArray -> {
                for (element in json.asJsonArray) {
                    list.add(context.deserialize(element, T::class.java))
                }
            }
            json.isJsonObject -> {
                list.add(context.deserialize(json, T::class.java))
            }
            else -> {
                throw RuntimeException("Unexpected JSON type: ${json.javaClass}")
            }
        }
        list
    }
}

internal object JsonMapper {
    fun serialize(obj: Any): String {
        return serializer.toJson(obj)
    }

    fun <T> deserialize(json: String, cls: Class<T>): T = deserializer.fromJson(json, cls)
}
