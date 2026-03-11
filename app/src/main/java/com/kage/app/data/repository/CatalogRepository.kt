package com.kage.app.data.repository

import com.kage.app.Config
import com.kage.app.data.model.Catalog
import com.kage.app.data.remote.KageApiClient

class CatalogRepository {

    suspend fun fetchCatalog(): Result<Catalog> {
        return try {
            val url = Config.CATALOG_URL
            if (url.isBlank()) {
                return Result.failure(IllegalStateException("URL del catálogo no configurada. Configura Config.CATALOG_URL."))
            }
            val body = KageApiClient.fetchCatalogJson(url)
            val catalog = KageApiClient.json.decodeFromString<Catalog>(body)
            Result.success(catalog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
