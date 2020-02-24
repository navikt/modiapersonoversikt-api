package no.nav.sbl.dialogarena.abac

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.RuntimeException
import java.lang.reflect.Type

private val responseType: Type = object : TypeToken<List<AbacResponse>>() {}.type
private val associatedAdviceType: Type = object : TypeToken<List<ObligationOrAdvice>>() {}.type
private val attributeAssignmentType: Type = object : TypeToken<List<AttributeAssignment>>() {}.type

private val serializer: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .setPrettyPrinting()
        .registerTypeAdapter(responseType, forcedListAdapter(AbacResponse::class.java))
        .registerTypeAdapter(associatedAdviceType, forcedListAdapter(ObligationOrAdvice::class.java))
        .registerTypeAdapter(attributeAssignmentType, forcedListAdapter(AttributeAssignment::class.java))
        .create()

private val deserializer: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        .setPrettyPrinting()
        .create()

internal inline fun <reified T> forcedListAdapter(cls: Class<T>): JsonDeserializer<List<T>> {
    return JsonDeserializer<List<T>> { json, typeOfT, context ->
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

object JsonMapper {
    fun serialize(obj: Any): String = serializer.toJson(obj)

    fun <T> deserialize(json: String, cls: Class<T>): T = deserializer.fromJson(json, cls)
}