package com.kage.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KageApiClient {

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun fetchCatalogJson(url: String): String {
        return httpClient.get(url).bodyAsText()
    }
}
