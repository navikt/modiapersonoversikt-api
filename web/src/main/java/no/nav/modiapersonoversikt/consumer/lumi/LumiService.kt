package no.nav.modiapersonoversikt.consumer.lumi

import com.fasterxml.jackson.databind.JsonNode
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private val APPLICATION_JSON = "application/json".toMediaType()

interface LumiService {
    fun submitFeedback(transportPayload: JsonNode)
}

open class LumiServiceImpl(
    baseUrl: String,
    private val httpClient: OkHttpClient,
) : LumiService {
    private val url = baseUrl.removeSuffix("/")

    override fun submitFeedback(transportPayload: JsonNode) {
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$url/api/azure/v1/feedback")
                        .post(transportPayload.toString().toRequestBody(APPLICATION_JSON))
                        .build(),
                ).execute()

        val responseBody = response.body?.string()

        if (!response.isSuccessful) {
            throw IllegalStateException("Forventet 2xx-svar fra Lumi, men fikk ${response.code}\n$responseBody")
        }
    }
}
