package com.kage.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Catalog(
    val version: Int,
    @SerialName("updated_at") val updatedAt: String,
    val categories: List<Category>
)

@Serializable
data class Category(
    val name: String,
    val items: List<StreamItem>
)

@Serializable
data class StreamItem(
    val id: String,
    val title: String,
    val description: String = "",
    val thumbnail: String = "",
    @SerialName("stream_type") val streamType: String,
    @SerialName("stream_url") val streamUrl: String
) {
    val isAcestream: Boolean get() = streamType == "acestream"
    val isHls: Boolean get() = streamType == "hls"
}
