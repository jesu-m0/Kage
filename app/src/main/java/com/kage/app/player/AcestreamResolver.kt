package com.kage.app.player

import com.kage.app.Config
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import com.kage.app.data.remote.KageApiClient

object AcestreamResolver {

    private val hashPattern = Regex("acestream://([a-fA-F0-9]+)")

    /**
     * Converts an acestream:// URL to a playable HTTP URL via the local Acestream Engine.
     * Returns null if the URL format is invalid.
     */
    fun resolve(acestreamUrl: String): String? {
        val match = hashPattern.find(acestreamUrl) ?: return null
        val hash = match.groupValues[1]
        return "http://${Config.ACESTREAM_ENGINE_HOST}:${Config.ACESTREAM_ENGINE_PORT}/ace/getstream?id=$hash"
    }

    /**
     * Checks if the Acestream Engine is running and reachable.
     */
    suspend fun isEngineRunning(): Boolean {
        return try {
            val url = "http://${Config.ACESTREAM_ENGINE_HOST}:${Config.ACESTREAM_ENGINE_PORT}/webui/api/service?method=get_version"
            val response = KageApiClient.httpClient.get(url)
            response.status.value == 200
        } catch (_: Exception) {
            false
        }
    }
}
